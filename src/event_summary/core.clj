(ns event-summary.core
  (:require [clojure.tools.cli :refer [cli]])
  (:gen-class))

(use 'event-summary.xlsx)
(use 'event-summary.consumer)
(use 'event-summary.db)

(import 'java.util.concurrent.Executors)

(defn all-pairs [events tickers] (for [x events y tickers] [x y]))
(defn slice [from to s] (->> s (take to) (drop from)))

(defn get-n-store [pair]
  (->> (get-events-tickers (pair 0) (pair 1))
       (remove nil?)
       (adapt-rows)
       (store)))

(def pool (Executors/newFixedThreadPool 25))
(defn pfetch [events]
  (cleanup)
  (let [tickers (sort (get-tickers))]
    (authorize)
    (.invokeAll pool (map #(fn [] (get-n-store %)) (all-pairs events tickers)))))

(defn abars [bars]
  (let [highest-hp-bar (last (sort-by :hp bars))]
    (if highest-hp-bar
    [(highest-hp-bar :hp) (highest-hp-bar :haph) (highest-hp-bar :hapl)] [nil nil nil])))

(defn aggregate [event-symbol-data]
  (concat [((first event-symbol-data) :symbol)]
    (abars (take 5 event-symbol-data))
    (abars (slice 5 10 event-symbol-data))
    (abars (slice 10 15 event-symbol-data))
    (abars (slice 15 20 event-symbol-data))
    (abars (drop 20 event-symbol-data))))

(defn load-n-print [event-id]
  (try
    (let [symbols (load-symbols-for-event event-id)]
      (let [symbols-data (map #(load-event-symbol-data event-id %) symbols)]
        (generate-output event-id (map aggregate symbols-data))))
        (catch Exception e (.printStackTrace e))))

(defn summary []
  (run! load-n-print (event-ids)))

; TODO: main should accept some args, then call pfetch with or without
; cleanup, and then summary... or just summary, if everything already fetched.
; Also it would be nice to write some comments and tests.
(def parameters-spec ["-h" "--help"])
(def help "Usage: es <options> mode")
(defn -main [& args]
  (let [[opts args banner] (cli args parameters-spec)]
      (case (first args)
        "fetch" (pfetch (range (Integer. (args 1)) (Integer. (args 2))))
        "xlsx" (summary)
        (println help)
      )
    )
  )
