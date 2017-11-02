(ns backend.todo
  (:require [backend.broadcast :as broadcast]))

(defn create-uuid! []
  (str (java.util.UUID/randomUUID)))

(defonce todos (atom [{:id (create-uuid!)
                       :text "Eat and drink"}
                      {:id (create-uuid!)
                       :text "Go to sauna!"}]))

(defn get-todos [_]
  {:message-type :todos
   :body @todos})

(defn add-todo! [message]
  (let [{:keys [send! send-fn]} message]
    (swap! todos conj {:id (create-uuid!)
                       :text (:body message)})
    (broadcast/broadcast! {:message-type :todos
                           :body @todos})))
