(ns frontend.main
  (:require [reagent.core :as reagent]
            [eines.client :as eines]))

(defn hello-view
  []
  [:div
   [:h1 "Hello from Reagent!"]
   [:button
    {:on-click (fn [_]
                 (eines/send! {:message-type :test
                               :body {:x 1}}
                              (fn [response]
                                (js/console.log "RESPONSE: " (pr-str response)))))}
    "Yell to backend!"]])

(defn on-connect
  []
  (js/console.log "Connecting to backend."))

(defn on-message
  [message]
  (js/console.log "Got message from backend: " message))

(defn on-close
  []
  (js/console.log "Disconnected from backend."))

(defn on-error
  []
  (js/console.warn "Disconnected from backend because of an error."))

(defn ^:export main
  []
  (eines/init! {:on-connect on-connect
                :on-message on-message
                :on-close on-close
                :on-error on-error})
  (reagent/render [hello-view]
                  (.getElementById js/document "app")))

(main)
