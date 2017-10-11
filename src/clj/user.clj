(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :refer [clear go halt init reset reset-all]]
            [backend.server]))

(integrant.repl/set-prep!
  (fn [] (ig/read-string (slurp "resources/config.edn"))))
