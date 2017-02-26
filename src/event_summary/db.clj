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

(defn adapt-rows [rows] (into [] (map adapt (filter  #(some? (% :symbol)) rows)))) ; what to do if :symbol is null?

(defn store [rows] (j/insert-multi! pg-db :event_analytics rows))
