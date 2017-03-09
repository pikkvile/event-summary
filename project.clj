(defproject event-summary "0.1.0-SNAPSHOT"
  :main event-summary.core
  :aot :all
  :uberjar-name "es.jar"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [dk.ative/docjure "1.11.0"]
                 [clj-http "3.4.1"]
                 [cheshire "5.7.0"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [org.postgresql/postgresql "9.4.1212"]
                 [com.cemerick/url "0.1.1"]
                 [org.clojure/tools.cli "0.3.5"]])
