(ns backend.main
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn -main [& args]
  (require 'backend.server)
  (let [config ((resolve 'integrant.core/read-string)
                (slurp (io/resource "config.edn")))]
    ((resolve 'integrant.core/init) config)))
