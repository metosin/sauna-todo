(defproject sauna-todo "0.1.0-SNAPSHOT"
  :description "Simple full-stack TODO app example for demonstrating Clojure(script)"
  :url "https://github.com/metosin/sauna-todo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha19"]
                 [org.clojure/clojurescript "1.9.946"]
                 [binaryage/devtools "0.9.7"]
                 [integrant "0.6.1"]
                 [integrant/repl "0.2.0"]
                 [org.immutant/web "2.1.9"]
                 [hiccup "2.0.0-alpha1"]
                 [reagent "0.8.0-alpha1"]
                 [metosin/eines "0.0.7"]
                 [metosin/reagent-dev-tools "0.1.0"]]
  :main ^:skip-aot backend.main
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :target-path "target/%s"
  :repl-options {:init-ns user}
  :profiles {:dev {:resource-paths ["target/dev/resources"]
                   :sass {:target-path "target/dev/resources/css"}}
             :prod {:sass {:target-path "target/prod/resources/css"}
                    :resource-paths ["target/prod/resources"]}
             :uberjar {:uberjar-name "app.jar"
                       :aot [backend.main]}}
  :plugins [[lein-pdo "0.1.1"]
            [deraen/lein-sass4clj "0.3.1"]
            [lein-figwheel "0.5.13"]
            [lein-cljsbuild "1.1.7"]
            [metosin/boot-alt-test "0.4.0-SNAPSHOT"]]
  :sass {:source-paths ["src/sass"]
         :source-map true
         :output-style :compressed}
  :alt-test {:report :pretty}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljc" "src/cljs"]
                        :figwheel true
                        :compiler {:main "frontend.main"
                                   :asset-path "js/out"
                                   :external-config {:devtools/config {:features-to-install :all}}
                                   :preloads [devtools.preload]
                                   :output-to "target/dev/resources/js/main.js"
                                   :output-dir "target/dev/resources/js/out"}}
                       {:id "prod"
                        :source-paths ["src/cljc" "src/cljs"]
                        :compiler {:main "frontend.main"
                                   :optimizations :advanced
                                   :output-to "target/prod/resources/js/main.js"
                                   :output-dir "target/prod/resources/js/out"}}]}
  :auto-clean false
  :figwheel {:css-dirs ["target/dev/resources/css"]}
  :aliases {"dev" ["do" "clean"
                   ["pdo" ["sass4clj" "auto"] ["figwheel"]]]
            "prod" ["with-profile" "prod" "do"
                    "clean"
                    ["sass4clj" "once"]
                    ["cljsbuild" "once" "prod"]
                    "uberjar"]})
