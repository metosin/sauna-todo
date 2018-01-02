(ns frontend.main
  (:require [common.localization :refer [tr]]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [dispatch subscribe reg-event-fx reg-event-db reg-sub]]
            [re-frame.db :as re-frame-db]
            [eines.client :as eines]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent-dev-tools.core :as dev-tools]
            [schema.core :as s]
            [reitit.core :as reitit]
            [reitit.coercion :as coercion]
            [reitit.coercion.schema :as schema-coercion]
            [reitit.re-frame :as reitit-re-frame]))

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
                  :on-success [:todos]}
     ;; This would usually be called after login is done
     :dispatch [:routes/apply-controllers]}))

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

(defn todo-view [_]
  [:div.todo-content
    [todo-input-view]
    [todo-list-view]])

(def root-controller
  {:params (constantly nil)
   :start (fn [_]
            (js/console.log "Init app"))})

(def routes
  (reitit/router
    [["" {:name :frontpage
          :controllers [{:params (constantly nil)
                         :view todo-view}]}]

     ["/a" {:controllers [{:params (constantly nil)
                           :start (fn [_] (js/console.log "parent controller"))}]}
      ["/b" {:name :b-view
             :controllers [{:params (constantly nil)
                            :view (fn [_] [:h1 "B view"])
                            :start (fn [_] (js/console.log "b controller"))}]}]
      ["/c" {:name :c-view
             :controllers [{:params (constantly nil)
                            :view (fn [_] [:h1 "C view"])
                            :start (fn [_] (js/console.log "c controller"))}]}]
      ["/d" {:controllers [{:params (fn [{:keys [params]}]
                                      ;; This controller is responsible for both sub-views, which
                                      ;; don't have their own controllers.
                                      ;; re-init controller if :id has changed.
                                      (select-keys params [:id]))
                            :view (fn [params] [:h1 "D view, id " (:id params)])
                            :start (fn [params] (js/console.log "d controller" params))}]}
       ["" {:name :d-view}]
       ["/:id" {:name :d-view-id
                :parameters {:path {:id s/Int}}}]]]

     ["/another" {:controllers [{:params (constantly nil)
                                 :view (fn [_]
                                         [:h1 "Another view"])
                                 :start (fn [_] (js/console.log "another controller"))}]}
      ["" {:name :another-main}]
      ["/:name" {:name :another-with-params
                 :parameters {:path {:name s/Str}}
                 :controllers [{:params (fn [{:keys [params]}]
                                          (select-keys params [:name]))
                                :view (fn [params] [:h1 "Name: " (:name params)])
                                :start (fn [params] (js/console.log "another controller" params))}]}]]]
    {:compile coercion/compile-request-coercers
     :data {:controllers [root-controller]
            :coercion schema-coercion/coercion}}))

(defn main-view []
  [:div.todo-container
   [:h1.todo-title (tr :page-title)]
   [:ul
    [:li [:a {:href (reitit-re-frame/href :frontpage)} "Todo-app"]]
    [:li "Parent"
     [:ul
      [:li [:a {:href (reitit-re-frame/href :b-view)} "B-view"]]
      [:li [:a {:href (reitit-re-frame/href :c-view)} "C-view"]]
      [:li [:a {:href (reitit-re-frame/href :d-view)} "D-view"]
       [:ul
        [:li [:a {:href (reitit-re-frame/href :d-view-id {:id 1})} "ID: 1"]]
        [:li [:a {:href (reitit-re-frame/href :d-view-id {:id 2})} "ID: 2"]]]]]]
    [:li [:a {:href (reitit-re-frame/href :another-main)} "Another main"]
     [:ul
      [:li [:a {:href (reitit-re-frame/href :another-with-params {:name "bar"})} "Name: bar"]]
      [:li [:a {:href (reitit-re-frame/href :another-with-params {:name "foo"})} "Name: foo"]]]]
    ]
   [reitit-re-frame/routed-view]
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
  (dispatch [:routes/init routes])
  (reagent/render [main-view] (.getElementById js/document "app"))
  (if-let [dev-el (.getElementById js/document "dev")]
    (reagent/render [dev-tools/dev-tool {}] dev-el)))

(main)
