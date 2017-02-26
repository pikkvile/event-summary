(ns event-summary.core)

(use 'dk.ative.docjure.spreadsheet)

(use 'event-summary.tickers-xslx)
(use 'event-summary.consumer)
(use 'event-summary.db)

(defn -main [& args]
  (println "Extracting tickers from xlsx...")
  (let [tickers (get-tickers)]
    (println "Done. Total tickers: " (count tickers))
    (println "Authorizing...")
    (authorize)
    (println "Done:")

))

(defn get-n-store [event-id, symbol]
  (->> (get-events-tickers event-id symbol)
       (adapt-rows)
       (store)))
