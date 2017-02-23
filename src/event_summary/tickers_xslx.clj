(ns event-summary.tickers-xslx)

(use 'dk.ative.docjure.spreadsheet)

(defn read-tickers []
  (->> (load-workbook-from-resource "tickers.xslx")
       (select-sheet "Table 1")
       (select-columns {:B :tb :C :tc :E :te})
       (remove nil?)))

(defn tickers-row2list [tmap] (list (tmap :tb) (tmap :tc) (tmap :te)))

(defn flat-tickers [tickers]
  (->> (mapcat tickers-row2list tickers)
       (remove nil?)
       (distinct)))

(defn get-tickers [] (flat-tickers (read-tickers)))
