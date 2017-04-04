(ns frontend.main
  (:require [reagent.core :as reagent]
            [eines.client :as eines]))

(def state (reagent/atom nil))

(defn hello-view
  []
  [:div
   [:h1 "Hello from Reagent!"]
   [:h2 "State: " (pr-str @state)]])

(defn on-connect
  []
  (js/console.log "Connected to backend.")
  (eines/send! {:message-type :get-todos}
               (fn [response]
                 (reset! state (:body response)))))

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
