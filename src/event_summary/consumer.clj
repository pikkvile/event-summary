(ns event-summary.consumer
  (:require [clj-http.client :as client])
  (:require [clj-http.cookies :as cookies]))

(def domain "169.45.250.98")
(def base (str "http://" domain ":3000"))
(def dashboard (str base "/dashboard"))
(def login (str base "/api/centipair/login"))
(def prepare (str base "/api/prepare"))
(def events-tickers (str base "/private/api/event/analytics?event-id=%s&symbol=%s&ref-key=close&n-key=close"))

(def cs (cookies/cookie-store))

(def cfg {:cookie-store cs :throw-exceptions? false})
(def tjson {"Content-Type" "application/transit+json"})
(def host-referer {"Host" (str domain ":3000") "Referer" dashboard})
(def uir {"Upgrade-Insecure-Requests" "1"})
(def creds {:body "[\"^ \", \"~:email\", \"Ilya@accrue.com\", \"~:password\", \"password\"]"})
(def csrf)
(def auth)

(defn authorize []
  (client/get dashboard cfg)
  (client/post login (merge cfg creds {:headers tjson}))
  (let [csrf-token (((client/get prepare (merge cfg {:as :json :headers host-referer})) :body) :csrf-token)]
    (def csrf {"X-CSRF-Token" csrf-token})
    (let [auth-token (((client/post login (merge cfg creds {:as :json :headers (merge tjson csrf)})) :body) :auth-token)]
      (def auth {:value auth-token :domain domain :path "/"}))))

(defn get-events-tickers [event-id ticker]
  (println (str "Retrieving analytics data for event " event-id " and symbol " symbol))
  ((client/get (format events-tickers event-id ticker) {:as :json :headers (merge csrf uir)
    ; Have to set cookies manually, as cookie-store does not work as expected.
    ; Probably will get rid of it in future.
    :cookies {:auth-token auth :JSESSIONID ((cookies/get-cookies cs) "JSESSIONID")}}) :body))
