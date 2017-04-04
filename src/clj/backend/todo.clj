(ns backend.todo)

(def todos (atom [{:id 1
                   :text "Eat pizza!"}
                  {:id 2
                   :text "Go to sauna!"}]))

(defn get-todos
  [_]
  {:body @todos})
