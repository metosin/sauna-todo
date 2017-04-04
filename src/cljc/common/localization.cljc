(ns common.localization)

(def defaults
  {:page-title "Sauna-TODO"
   :loading "We are loading stuff, please wait."})

(defn tr
  [k]
  (or (get defaults k)
      (str "Not yet localized: << " k " >>")))
