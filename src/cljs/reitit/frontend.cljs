(ns reitit.frontend
  (:require [reitit.core :as reitit]
            [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            goog.Uri
            [reitit.coercion :as coercion]))

;;
;; Utilities
;;

(defn query-params [uri]
  (let [x (goog.Uri/parse uri)
        q (.getQueryData x)
        m (into {} (map (juxt keyword #(.get q %)) (.getKeys q)))]
    m))

(defn get-hash []
  (-> js/location.hash
      (subs 1)
      (str/replace #"/$" "")))

(defn- pad-same-length [a b]
  (concat a (take (- (count b) (count a)) (repeat nil))))

;;
;; Controller implementation
;;

(defn get-params
  "Get controller parameters given match. If controller provides :params
  function that will be called with the match. Default is (:params match),
  which contains all the params."
  [controller match]
  (if-let [f (:params controller)]
    (f match)
    (:params match)))

(defn apply-controller
  "Run side-effects (:start or :stop) for controller.
  The side-effect function is called with controller params."
  [controller method match]
  (when-let [f (get controller method)]
    (f (::params controller))))

(defn apply-controllers
  "Applies changes between current controllers and
  those previously enabled. Resets controllers which
  parameters have changed."
  [state old-match new-match]
  (if (:enable-controllers? state)
    (let [current-controllers (:controllers state)
          new-controllers (map (fn [controller]
                                 (assoc controller ::params (get-params controller new-match)))
                               (:controllers (:data new-match)))
          changed-controllers (->> (map (fn [old new]
                                          (if (not= old new)
                                            ;; different controllers, or params changed
                                            {:old old, :new new}
                                            nil))
                                        (pad-same-length current-controllers new-controllers)
                                        (pad-same-length new-controllers current-controllers))
                                   (keep identity)
                                   vec)]
      (doseq [controller (map :old changed-controllers)]
        (apply-controller controller :stop old-match))
      (doseq [controller (map :new changed-controllers)]
        (apply-controller controller :start new-match))
      (assoc state :controllers new-controllers))
    state))

(defn hash-change [state old-match hash]
  (let [match (as-> (or (reitit/match-by-path (:routes state) hash)
                        {:data {:name :not-found}}) $
                (assoc $ :query (query-params hash))
                (assoc $ :parameters (coercion/coerce! $)))]
    {:match match
     :state (apply-controllers state old-match match)}))
