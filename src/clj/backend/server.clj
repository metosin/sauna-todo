(ns backend.server
  (:require [integrant.core :as ig]
            [immutant.web :as immutant]
            [ring.util.response :as resp]))

(defmethod ig/init-key :adapter/immutant [_ {:keys [handler] :as opts}]
  (immutant/run handler (-> opts (dissoc :handler))))

(defmethod ig/halt-key! :adapter/immutant [_ server]
  (immutant/stop server))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (fn [_] (resp/response (str "Hello " name))))
