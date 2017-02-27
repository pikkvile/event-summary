(ns event-summary.core)

(use 'dk.ative.docjure.spreadsheet)

(use 'event-summary.tickers-xslx)
(use 'event-summary.consumer)
(use 'event-summary.db)

(def event-ids (range 43 45))

(defn all-pairs [tickers] (for [x event-ids y tickers] [x y]))

(defn get-n-store [pair]
  (->> (get-events-tickers (pair 0) (pair 1))
       (adapt-rows)
       (store)))

(defn -main [& args]
  (println "Extracting tickers from xlsx...")
  (let [tickers (get-tickers)]
    (println "Done. Total tickers: " (count tickers))
    (println "Authorizing...")
    (authorize)
    (println "Done:")

    (run! get-n-store (all-pairs tickers))))
