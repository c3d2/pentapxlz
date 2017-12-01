(defproject pentapxlz "0.1.0-SNAPSHOT"
  ;:description "FIXME: write description"
  ;:url "http://example.com/FIXME"
  ;:license {:name "Eclipse Public License"
  ;          :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [aero "1.1.2"]
                 [com.taoensso/timbre "4.10.0"]             ;; logging lib for clj/cljs
                 [com.rpl/specter "1.0.5"]
                 [org.clojure/core.logic "0.8.11"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [org.clojure/core.async "0.3.465"]
                 [com.ninjudd/eventual "0.5.5"]
                 [manifold "0.1.6"]
                 [aleph "0.4.4"]
                 [com.taoensso/sente "1.11.0"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"] ;; Includes ring-anti-forgery
                 [metosin/ring-http-response "0.9.0"]
                 [compojure "1.6.0"]
                 [metosin/compojure-api "2.0.0-alpha12"]
                 [metosin/spec-tools "0.5.1"]
                 [fn2api "0.1.1-SNAPSHOT"]
                 [quil "2.6.0"]
                 #_[spectrum "0.1.4-joe"]]                  ;; fix for bigdec? missing in clojure 1.9.0-RC1

  :source-paths ["src/clj" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :resource-paths ["resources" "target/cljsbuild"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js/compiled"]

  :main ^:skip-aot pentapxlz.core

  :repl-options {:init-ns user}

  :target-path "target/%s"
  :profiles {:ustripe-clojure {:dependencies [[aleph "0.4.4"]
                                              [manifold "0.1.6"]
                                              [org.clojure/core.async "0.3.465"]]}
             :webapi          {:dependencies [[org.clojure/core.async "0.3.465"]
                                              [com.ninjudd/eventual "0.5.5"]
                                              [manifold "0.1.6"]
                                              [aleph "0.4.4"]
                                              [com.taoensso/sente "1.11.0"]
                                              [ring "1.6.3"]
                                              [ring/ring-defaults "0.3.1"] ;; Includes ring-anti-forgery
                                              [metosin/ring-http-response "0.9.0"]
                                              [compojure "1.6.0"]
                                              [metosin/compojure-api "2.0.0-alpha12"]
                                              [metosin/spec-tools "0.5.1"]
                                              [fn2api "0.1.1-SNAPSHOT"]]}
             :figlet          {:dependencies [[clj-figlet "0.1.1"]]}
             :quil            {:dependencies [[quil "2.6.0"]]}
             :recommended     [:ustripe-clojure :webapi :figlet :quil]
             :dev             [:recommended :cljs/dev {:dependencies [[criterium "0.4.4"]]}]
             :uberjar         [:recommended {:aot :all}]
             :cljs/dev        {:dependencies [[figwheel-sidecar "0.5.14"]
                                              [com.cemerick/piggieback "0.2.2"]]
                               :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                               :plugins      [[lein-figwheel "0.5.14"]
                                              [lein-cljsbuild "1.1.7"]]
                               :cljsbuild
                                             {:builds
                                              {:app
                                               {:source-paths ["src/cljs" "src/cljc"]
                                                :figwheel     true
                                                :compiler
                                                              {:main          "pentapxlz.core"
                                                               :asset-path    "/js/compiled/out"
                                                               :output-to     "resources/public/js/compiled/app.js"
                                                               :output-dir    "resources/public/js/compiled/out"
                                                               :source-map    true
                                                               :optimizations :none
                                                               :pretty-print  true}}}}}})

