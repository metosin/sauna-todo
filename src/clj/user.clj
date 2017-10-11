(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :refer [clear go halt init reset reset-all]]))

(integrant.repl/set-prep!
  (fn []
    (require 'backend.server)
    (ig/read-string (slurp "resources/config.edn"))))
