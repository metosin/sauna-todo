(ns reitit.re-frame
  (:require [re-frame.core :as re :refer [dispatch]]
            [reitit.frontend :as reitit-frontend]
            [reitit.core :as reitit]
            [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            goog.Uri
            [reitit.coercion :as coercion]))

(re/reg-event-fx ::hash-change
  (fn [{:keys [db]} _]
    (let [{:keys [match state]} (reitit-frontend/hash-change (::routes db) (:view db) (reitit-frontend/get-hash))]
      {:db (assoc db
                  :view match
                  ::routes state)})))

;; Enables controllers (when used the first time)
;; and applies the changes.
(re/reg-event-db :routes/apply-controllers
  (fn [db _]
    (let [state (assoc (reitit-frontend/apply-controllers (::routes db) nil (:view db))
                       :enable-controllers? true)]
      (assoc db ::routes state))))

(re/reg-event-fx :routes/init
  (fn [{:keys [db]} [_ routes]]
    (set! js/window.onhashchange #(dispatch [::hash-change]))
    {:db (assoc db ::routes {:routes routes
                             :controllers []
                             :enable-controllers? false})
     :dispatch [::hash-change]}))

(re/reg-sub :routes/current-view
  (fn [db]
    (:view db)))

(re/reg-sub :routes/data
  :<- [:routes/current-view]
  (fn [current-view _]
    (:data current-view)))

(re/reg-sub :routes/routes
  (fn [db]
    (:routes (::routes db))))

(re/reg-sub :routes/match-by-name
  :<- [:routes/routes]
  (fn [routes [_ k]]
    (if routes
      (reitit/match-by-name routes k))))

(defn routed-view
  [views]
  (let [view @(re/subscribe [:routes/current-view])
        ;; Select downmost controller that has view component
        controller (first (filter :view (reverse (:controllers (:data view)))))]
    (if-let [f (:view controller)]
      ;; NOTE: View component is passed the complete params map, not just the controllers params
      ;; :routes/current-view doesn't know about controller params
      [f (:params view)]
      [:div "No view component defined for route: " (:name (:data view))])))

;;
;; Utils to create hrefs and change URI
;;

(re/reg-sub :routes/href
  :<- [:routes/routes]
  (fn [routes [_ name params]]
    ;; FIXME: query string
    ;; if last is map? -> append query string
    (if routes
      (if-let [path (:path (reitit/match-by-name routes name params))]
        (str "#" path)
        (js/console.error "Can't create URL for route " (pr-str name) (pr-str params))))))

(defn href
  ([name] (href name nil))
  ([name params]
   @(re/subscribe [:routes/href name params])))

(defn update-uri!
  ([name]
   (update-uri! name nil))
  ([name params]
   (set! js/window.location.hash (href name params))))

(re/reg-fx :update-uri
  (fn [v]
    (if v
      (apply update-uri! v))))

(re/reg-event-fx :routes/update-uri
  (fn [_ [_ & v]]
    {:update-uri v}))
