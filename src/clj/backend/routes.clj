(ns backend.routes
  (:require [backend.todo :as todo]))

(def routes {:get-todos #'todo/get-todos
             :new-todo #'todo/add-todo!})

(defn route-message
  [{:keys [message-type] :as message}]
  (if-let [f (get routes message-type)]
    (f message)
    (throw (ex-info (str "No route found for " message-type) {}))))
