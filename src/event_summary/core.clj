(ns event-summary.core)

(use 'dk.ative.docjure.spreadsheet)

(use 'event-summary.tickers-xslx)
(use 'event-summary.consumer)

(defn -main [& args]
  (println "Extracting tickers from xlsx...")
  (let [tickers (get-tickers)]
    (println "Done. Total tickers: " (count tickers))))
