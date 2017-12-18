(ns frontend.main
  (:require [common.localization :refer [tr]]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [dispatch subscribe reg-event-fx reg-event-db reg-sub]]
            [re-frame.db :as re-frame-db]
            [eines.client :as eines]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent-dev-tools.core :as dev-tools]))

;;
;; State
;;

(dev-state/register-state-atom "App state" re-frame-db/app-db)

(defn on-message [message]
  (let [{:keys [message-type body]} (:body message)]
    (dispatch [message-type body])))

(re-frame/reg-fx :eines/send
  (fn [message]
    (if message
      (eines/send! (dissoc message :on-success)
                   (fn [response]
                     (if (:on-success message)
                       (dispatch (conj (:on-success message) (:body (:body response))))
                       (on-message response)))))))

(reg-event-fx ::on-connect
  (fn [{:keys [db]} _]
    (js/console.log "Connected to backend.")
    {:eines/send {:message-type :get-todos
                  :on-success [:todos]}}))

(reg-event-db :todos
  (fn [app-db [_ new-todos]]
    (assoc app-db :todos new-todos)))

(reg-sub ::todos
  (fn [db _]
    (:todos db)))

(reg-sub ::todo-item-count
  (fn [_]
    (subscribe [::todos]))
  (fn [todos _]
    (js/console.log "lasketaan count")
    (count todos)))

(reg-sub ::new-todo
  (fn [db _]
    (:new-todo db)))

(reg-event-db ::update-new-todo
  (fn [db [_ new-value]]
    (assoc db :new-todo new-value)))

(reg-event-fx ::new-todo
  (fn [{:keys [db]}]
    (let [new-todo (:new-todo db)]
      {:eines/send {:message-type :new-todo
                    :body new-todo
                    :on-success [::update-new-todo nil]}})))

;;
;; Views
;;

(defn todo-item-view [todo]
  [:div.todo-list-item
   [:span.todo-list-item__span (:text todo)]])

(defn todo-list-view []
  [:div.todo-list
   (for [todo @(subscribe [::todos])]
     ^{:key (:id todo)}
     [todo-item-view todo])
   [:p @(subscribe [::todo-item-count]) " kappaletta todo item"]])

(defn todo-input-view []
  [:input.todo-input
   {:placeholder  (tr :new-todo)
    :auto-focus   true
    :value        @(subscribe [::new-todo])
    :on-change    (fn [e]
                    (dispatch [::update-new-todo (.. e -target -value)]))
    :on-key-press (fn [e]
                    (case (.-charCode e)
                      13 (dispatch [::new-todo])
                      nil))}])

(defn main-view []
  [:div.todo-container
   [:h1.todo-title (tr :page-title)]
   [:div.todo-content
    [todo-input-view]
    [todo-list-view]]
   [:div.todo-footer (tr :we-love-clojure)]])

;;
;; Websockets
;;

(defn on-close []
  (js/console.log "Disconnected from backend."))

(defn on-error []
  (js/console.warn "Disconnected from backend because of an error."))

;;
;; Main
;;

(defn ^:export main []
  (eines/init! {:on-connect #(dispatch [::on-connect])
                :on-message on-message
                :on-close on-close
                :on-error on-error})
  (reagent/render [main-view] (.getElementById js/document "app"))
  (if-let [dev-el (.getElementById js/document "dev")]
    (reagent/render [dev-tools/dev-tool {}] dev-el)))

(main)
