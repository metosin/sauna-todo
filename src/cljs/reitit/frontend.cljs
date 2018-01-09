(ns reitit.frontend
  (:require [reitit.core :as reitit]
            [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            goog.Uri
            [reitit.coercion :as coercion]))

;;
;; Utilities
;;

(defn query-params [^goog.Uri uri]
  (let [q (.getQueryData uri)]
    (->> q
         (.getKeys)
         (map (juxt keyword #(.get q %)))
         (into {}))))

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
  function that will be called with the match. Default is nil."
  [controller match]
  (if-let [f (:params controller)]
    (f match)))

(defn apply-controller
  "Run side-effects (:start or :stop) for controller.
  The side-effect function is called with controller params."
  [controller method]
  (when-let [f (get controller method)]
    (f (::params controller))))

(defn apply-controllers
  "Applies changes between current controllers and
  those previously enabled. Resets controllers which
  parameters have changed."
  [old-controllers new-match]
  (let [new-controllers (map (fn [controller]
                               (assoc controller ::params (get-params controller new-match)))
                             (:controllers (:data new-match)))
        changed-controllers (->> (map (fn [old new]
                                        ;; different controllers, or params changed
                                        (if (not= old new)
                                          {:old old, :new new}))
                                      (pad-same-length old-controllers new-controllers)
                                      (pad-same-length new-controllers old-controllers))
                                 (keep identity)
                                 vec)]
    (doseq [controller (map :old changed-controllers)]
      (apply-controller controller :stop))
    (doseq [controller (map :new changed-controllers)]
      (apply-controller controller :start))
    new-controllers))

(defn hash-change [routes hash]
  (let [uri (goog.Uri/parse hash)
        match (or (reitit/match-by-path routes (.getPath uri))
                  {:data {:name :not-found}})
        q (query-params uri)
        ;; Coerce if coercion enabled
        c (if (:result match)
            (coercion/coerce-request (:result match) {:query-params q
                                                      :path-params (:params match)})
            {:query q
             :path (:param match)})
        ;; Replace original params with coerced params
        match (-> match
                  (assoc :query (:query c))
                  (assoc :params (:path c)))]
    match))
