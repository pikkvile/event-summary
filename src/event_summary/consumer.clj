(ns event-summary.consumer
  (:require [clj-http.client :as client])
  (:require [clj-http.cookies :as cookies]))

(def base "http://169.45.250.98:3000")
(def dashboard (str base "/dashboard"))
(def login (str base "/api/centipair/login"))
(def prepare (str base "/api/prepare"))

(def cs (cookies/cookie-store))

(def cfg {:cookie-store cs :throw-exceptions? false})
(def creds {:body
  "[\"^ \", \"~:email\", \"Ilya@accrue.com\", \"~:password\", \"password\"]"
  :headers {"Content-Type" "application/transit+json"}})

(defn authorize [] (
  (client/get dashboard cfg)
  (client/post login (merge cfg creds))
  (client/get prepare (merge cfg {:headers {
    "Host" "169.45.250.98:3000"
    "Referer" "http://169.45.250.98:3000/dashboard"}}))))
