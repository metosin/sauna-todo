(defproject sauna-todo "0.1.0-SNAPSHOT"
  :description "Simple full-stack Clojure TODO app example for TiTe and Luuppi -sauna"
  :url "https://github.com/metosin/sauna-todo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot backend.main
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
