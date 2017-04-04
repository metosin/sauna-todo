(ns backend.main
  (:gen-class))

(defn -main
  [& args]
  (require 'backend.server)
  (let [config ((resolve 'integrant.core/read-string)
                (slurp "resources/config.edn"))]
    ((resolve 'integrant.core/init) config)))
