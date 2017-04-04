(ns backend.routes
  (:require [backend.todo :as todo]))

(def routes {:get-todos todo/get-todos
             :new-todo todo/add-todo!})

(defn route-message
  [message]
  (let [f (get routes (:message-type message))]
    (f message)))
