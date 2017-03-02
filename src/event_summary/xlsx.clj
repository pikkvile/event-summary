(ns event-summary.xlsx)

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
       (remove number?)
       (remove #(= % "Ticker"))
       (distinct)))

(defn get-tickers []
  (println "Extracting tickers from xlsx...")
  (flat-tickers (read-tickers)))

(defn generate-output [event-id data] ; TODO: colors, fonts, rounding
  (let [wb (create-workbook "Event Summary"
                            (into [["Symbol" "Bars 1-5" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 6-10" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 11-15" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 16-20" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 21+" "Avg Peak Higher" "Avg Peak Lower"]] data))
        sheet (select-sheet "Event Summary" wb)
        header-row (first (row-seq sheet))]
    (do
      (set-row-style! header-row (create-cell-style! wb {:font {:bold true}}))
      (save-workbook! (str "/tmp/event-summary-" event-id ".xlsx") wb)))
  )
