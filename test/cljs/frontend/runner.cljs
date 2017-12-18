(ns frontend.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [frontend.main-test]))

(doo-tests 'frontend.main-test)