(defproject sauna-todo "0.1.0-SNAPSHOT"
  :description "Simple full-stack Clojure TODO app example for TiTe and Luuppi -sauna"
  :url "https://github.com/metosin/sauna-todo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [integrant "0.3.3"]
                 [integrant/repl "0.2.0"]
                 [org.immutant/web "2.1.6"]
                 [hiccup "1.0.5"]
                 [reagent "0.6.1"]]
  :main ^:skip-aot backend.main
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :target-path "target/%s"
  :repl-options {:init-ns user}
  :profiles {:dev {:resource-paths ["target/dev/resources"]
                   :sass {:target-path "target/dev/resources/css"}}
             :uberjar {:aot :all}}
  :plugins [[lein-pdo "0.1.1"]
            [deraen/lein-sass4clj "0.3.0"]
            [lein-figwheel "0.5.9"]]
  :sass {:source-paths ["src/sass"]
         :source-map true
         :output-style :compressed}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljc" "src/cljs"]
                        :figwheel true
                        :compiler {:main "frontend.main"
                                   :asset-path "js/out"
                                   :output-to "target/dev/resources/js/main.js"
                                   :output-dir "target/dev/resources/js/out"}}]}
  :figwheel {:css-dirs ["target/dev/resources/css"]}
  :aliases {"develop" ["do" "clean"
                      ["pdo" ["sass4clj" "auto"] ["figwheel"]]]})
