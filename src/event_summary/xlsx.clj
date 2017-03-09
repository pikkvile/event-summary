(ns event-summary.xlsx)

(use 'dk.ative.docjure.spreadsheet)

(defn read-tickers []
  (->> (load-workbook-from-resource "tickers.xlsx")
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

(def highlight-after 70)
(defn need-highlight [cell]
  (and
    (or
      (= 1 (.getColumn (.getAddress cell)))
      (= 4 (.getColumn (.getAddress cell)))
      (= 7 (.getColumn (.getAddress cell)))
      (= 10 (.getColumn (.getAddress cell)))
      (= 13 (.getColumn (.getAddress cell))))
    (>= (.getNumericCellValue cell) highlight-after)))
(defn generate-output [event-id data]
  (println (str "Generating xslx for event " event-id))
  (let [wb (create-workbook "Event Summary"
                            (into [["Symbol" "Bars 1-5" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 6-10" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 11-15" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 16-20" "Avg Peak Higher" "Avg Peak Lower"
                                        "Bars 21+" "Avg Peak Higher" "Avg Peak Lower"]] data))
        sheet (select-sheet "Event Summary" wb)
        bold (create-cell-style! wb {:font {:bold true}})
        yellow-highlight (create-cell-style! wb {:background :yellow})
        yellow-bold (create-cell-style! wb {:background :yellow :font {:bold true}})
        header-row (first (row-seq sheet))]
    (do
      (set-row-style! header-row bold)
      (run! #(set-cell-style! % yellow-bold) (filter #(.contains (.getStringCellValue %) "Bars") (cell-seq header-row)))
      (run! #(set-cell-style! % yellow-highlight) (filter need-highlight (flatten (map cell-seq (rest (row-seq sheet))))))
      (save-workbook! (str "/tmp/event-summary-" event-id ".xlsx") wb)))
  )
