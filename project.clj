(defproject sauna-todo "0.1.0-SNAPSHOT"
  :description "Simple full-stack Clojure TODO app example for TiTe and Luuppi -sauna"
  :url "https://github.com/metosin/sauna-todo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot sauna-todo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
