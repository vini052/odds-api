(defproject odds-api "0.1.0-SNAPSHOT"
  :description "Um app para pegar as odds dos eventos"
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.2"]
                 [ring "1.9.5"]]
  :main ^:skip-aot odds-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
