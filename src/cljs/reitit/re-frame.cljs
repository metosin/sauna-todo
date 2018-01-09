(ns reitit.re-frame
  (:require [re-frame.core :as re :refer [dispatch]]
            [reitit.frontend :as reitit-frontend]
            [reitit.core :as reitit]
            [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            goog.Uri
            [reitit.coercion :as coercion]))

(re/reg-event-db ::hash-change
  (fn [db _]
    (let [match (reitit-frontend/hash-change (:routes (::routes db)) (reitit-frontend/get-hash))]
      (assoc db ::routes (assoc (::routes db)
                                :match match
                                :controllers (if (:enable-controllers? (::routes db))
                                               (reitit-frontend/apply-controllers (:controllers (::routes db)) match)
                                               (:controllers (::routes db))))))))

;; Enables controllers (when used the first time)
;; and applies the changes.
(re/reg-event-db :routes/apply-controllers
  (fn [db _]
    (assoc db ::routes (assoc (::routes db)
                              :controllers (reitit-frontend/apply-controllers (:controllers (::routes db)) (:match (::routes db)))
                              :enable-controllers? true))))

(re/reg-event-fx :routes/init
  (fn [{:keys [db]} [_ routes]]
    (set! js/window.onhashchange #(dispatch [::hash-change]))
    {:db (assoc db ::routes {:routes routes
                             :match nil
                             :controllers []
                             :enable-controllers? false})
     :dispatch [::hash-change]}))

(re/reg-sub ::state
  (fn [db]
    (::routes db)))

(re/reg-sub :routes/match
  :<- [::state]
  (fn [state]
    (:match state)))

(re/reg-sub :routes/data
  :<- [:routes/match]
  (fn [match _]
    (:data match)))

(re/reg-sub :routes/routes
  :<- [::state]
  (fn [state]
    (:routes state)))

(re/reg-sub :routes/match-by-name
  :<- [:routes/routes]
  (fn [routes [_ k params]]
    (if routes
      (reitit/match-by-name routes k params)
      ::not-initialized)))

(defn routed-view
  [views]
  (let [view @(re/subscribe [:routes/match])
        ;; Select downmost controller that has view component
        controller (first (filter :view (reverse (:controllers (:data view)))))]
    (if-let [f (:view controller)]
      ;; NOTE: View component is passed the complete params map, not just the controllers params
      ;; :routes/match doesn't know about controller params
      [f (:params view)]
      [:div "No view component defined for route: " (:name (:data view))])))

;;
;; Utils to create hrefs and change URI
;;

(re/reg-sub :routes/href
  (fn [[_ name params :as p]]
    (re/subscribe [:routes/match-by-name name params]))
  (fn [match [_ name params]]
    ;; FIXME: query string
    ;; if last is map? -> append query string
    (if-let [path (:path match)]
      (str "#" path)
      (if (not= ::not-initialized match)
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
