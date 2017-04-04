(ns backend.todo
  (:require [backend.broadcast :as broadcast]))

(defonce todos (atom [{:id 1
                       :text "Eat and drink"}
                      {:id 2
                       :text "Go to sauna!"}]))

(defn get-todos
  [_]
  {:message-type :todos
   :body @todos})

(defn add-todo!
  [message]
  (let [existing-ids (map :id @todos)
        new-id (inc (apply max 0 (conj existing-ids)))
        {:keys [send! send-fn]} message]
    (swap! todos conj {:id new-id
                       :text (:body message)})
    (broadcast/broadcast! {:message-type :todos
                           :body @todos})))
