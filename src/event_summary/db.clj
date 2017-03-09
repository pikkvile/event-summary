(ns event-summary.db
  (require [clojure.java.jdbc :as j]))

(def pg-db {:dbtype "postgresql"
            :dbname "evsum"
            :host "localhost"
            :user "maintenance"
            :password "maintenance"})

(defn adapt [row] {
  :event_id (read-string (row :event-id))
  :symbol (row :symbol)
  :day (row :day)
  :lp (row :lower-percent)
  :hp (row :higher-percent)
  :haph (row :higher-average-peak-higher)
  :hapl (row :higher-average-peak-lower)
  :laph (row :lower-average-peak-higher)
  :lapl (row :lower-average-peak-lower)})

(defn to-values-vector [m] (map #(into [] (vals %)) m))

(defn adapt-rows [rows] (into [] (map adapt (filter  #(some? (% :symbol)) rows)))) ; what to do if :symbol is null?

(defn store [rows] (j/insert-multi! pg-db :event_analytics rows))

(defn cleanup [] (println "Cleaning the db...") (j/delete! pg-db :event_analytics []))

(defn load-event-symbol-data [event-id symbol]
  (j/query pg-db ["select * from event_analytics where event_id=? and symbol=? order by day" event-id symbol]))

(defn load-symbols-for-event [event-id]
  (flatten (map vals
    (j/query pg-db ["select distinct symbol from event_analytics where event_id=? order by symbol" event-id]))))

(defn event-ids [] (map :event_id (j/query pg-db ["select distinct event_id from event_analytics"])))
