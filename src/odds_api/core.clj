(ns odds-api.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [ring.adapter.jetty :refer [run-jetty]]))


(def matches-endpoint "http://localhost:8080")
(def events-url "https://betano.p.rapidapi.com/odds_betano?eventId=%s&oddsFormat=decimal&raw=false")
(def header-map {:headers {:x-rapidapi-host "betano.p.rapidapi.com" :x-rapidapi-key "2bd1fb0331msh84b488989710bf2p182a0fjsn8efe5543b0f9"}})

(def odds-atom (atom {}))

(defn get-match-ids []
  (let [response (client/get matches-endpoint)
        body-parsed (json/parse-string (:body response) true)
        events (:events body-parsed)]
    (map :eventId (vals events))))

(defn fetch-odds [event-id]
  (let [resp (client/get (format events-url event-id) header-map)
        body-parsed (json/parse-string (:body resp) true)
        results-odd (get-in body-parsed [:markets :101 :outcomes])
        btts-odd (get-in body-parsed [:markets :104 :outcomes])]
    {:results [(get-in results-odd [:101 :bookmakers :betano :price]) (get-in results-odd [:102 :bookmakers :betano :price]) (get-in results-odd [:103 :bookmakers :betano :price])]
     :btts [(get-in btts-odd [:104 :bookmakers :betano :price]) (get-in btts-odd [:105 :bookmakers :betano :price])]}))

(defn update-odds-atom []
  (let [match-ids (get-match-ids)]
    (doseq [id match-ids]
      (swap! odds-atom assoc id (fetch-odds id)))))

(defn games-handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string @odds-atom {:pretty true})})

(defn -main [& args]
  (update-odds-atom)
  (run-jetty games-handler {:port 8081 :join? false})
  (println "Server running on port 8081"))