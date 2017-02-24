(ns event-summary.consumer
  (:require [clj-http.client :as client])
  (:require [clj-http.cookies :as cookies]))

(def base "http://169.45.250.98:3000")
(def dashboard (str base "/dashboard"))
(def login (str base "/api/centipair/login"))
(def prepare (str base "/api/prepare"))

(def cs (cookies/cookie-store))

(def cfg {:cookie-store cs :throw-exceptions? false})
(def tjson {"Content-Type" "application/transit+json"})
(def host-referer {"Host" "169.45.250.98:3000" "Referer" "http://169.45.250.98:3000/dashboard"})
(def creds {:body "[\"^ \", \"~:email\", \"Ilya@accrue.com\", \"~:password\", \"password\"]"})

(def tokens {:csrf nil :auth nil})
(defn authorize []
  (client/get dashboard cfg)
  (client/post login (merge cfg creds {:headers tjson}))
  (let [csrf-token (((client/get prepare (merge cfg {:as :json :headers host-referer})) :body) :csrf-token)]
    (let [auth-token (((client/post login (merge cfg creds {:as :json
      :headers (merge tjson {"X-CSRF-Token" csrf-token})})) :body) :auth-token)]
      (assoc tokens :csrf csrf-token :auth auth-token))))
