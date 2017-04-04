(ns frontend.main
  (:require [reagent.core :as reagent]))

(defn hello-view
  []
  [:h1 "Hello from Reagent!"])

(defn ^:export main
  []
  (reagent/render [hello-view]
                  (.getElementById js/document "app")))

(main)
