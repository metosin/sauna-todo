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
  (let [next-id (inc (apply max (map :id @todos)))
        {:keys [send! send-fn]} message]
    (swap! todos conj {:id next-id
                       :text (:body message)})
    (broadcast/broadcast! {:message-type :todos
                           :body @todos})))
