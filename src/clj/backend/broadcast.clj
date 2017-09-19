(ns backend.broadcast
  (:require [eines.core :as eines]))

(defn broadcast! [message]
  (let [sockets (vals @eines/sockets)]
    (doseq [socket sockets]
      (let [{:keys [send!]} socket]
        (send! message)))))
