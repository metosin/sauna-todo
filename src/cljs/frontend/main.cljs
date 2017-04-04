(ns frontend.main
  (:require [common.localization :refer [tr]]
            [reagent.core :as reagent]
            [eines.client :as eines]))

(defonce state (reagent/atom nil))

(defn todo-item-view
  [todo]
  [:div.todo-list__item (:text todo)])

(defn todo-list-view
  []
  [:div.todo-list
   (for [todo (:todos @state)]
     ^{:key (:id todo)}
     [todo-item-view todo])])

(defn todo-input-view
  []
  [:input.todo-input
   {:placeholder (tr :new-todo)
    :auto-focus true
    :value (:new-todo @state)
    :on-change (fn [e]
                 (let [new-todo (-> e .-target .-value)]
                   (swap! state assoc :new-todo new-todo)))
    :on-key-press (fn [e]
                    (if (= (.-charCode e) 13)
                      (js/console.log "Not Implemented Yet!")))}])

(defn main-view
  []
  [:div.todo-container
   [:h1.todo-title (tr :page-title)]
   [:div.todo-content
    [todo-input-view]
    [todo-list-view]]
   [:div.todo-footer (tr :we-love-clojure)]])

(defn on-connect
  []
  (js/console.log "Connected to backend.")
  (eines/send! {:message-type :get-todos}
               (fn [response]
                 (swap! state assoc :todos (:body response)))))

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
  (reagent/render [main-view]
                  (.getElementById js/document "app")))

(main)
