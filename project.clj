(defproject pentapxlz "0.1.0-SNAPSHOT"
  ;:description "FIXME: write description"
  ;:url "http://example.com/FIXME"
  ;:license {:name "Eclipse Public License"
  ;          :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [aero "1.1.2"]
                 [com.taoensso/timbre "4.10.0"] ;; logging lib for clj/cljs
                 [com.rpl/specter "1.0.5"]
                 #_[spectrum "0.1.4-joe"]]  ;; fix for bigdec? missing in clojure 1.9.0-RC1

  :main ^:skip-aot pentapxlz.core
  :target-path "target/%s"
  :profiles {:ustripe-clojure {:dependencies [[aleph "0.4.4"]
                                              [manifold "0.1.6"]
                                              [org.clojure/core.async "0.3.465"]]}
             :webapi          {:dependencies [[org.clojure/core.async "0.3.465"]
                                              [com.ninjudd/eventual "0.5.5"]
                                              [manifold "0.1.6"]
                                              [aleph "0.4.4"]
                                              [ring "1.6.3"]
                                              [metosin/ring-http-response "0.9.0"]
                                              [compojure "1.6.0"]
                                              [metosin/compojure-api "2.0.0-alpha12"]
                                              [metosin/spec-tools "0.5.1"]]}
             :figlet          {:dependencies [[clj-figlet "0.1.1"]]}
             :quil            {:dependencies [[quil "2.6.0"]]}
             :recommended     [:ustripe-clojure :webapi :figlet :quil]
             :dev             [:recommended {:dependencies [[criterium "0.4.4"]]}]
             :uberjar         [:recommended {:aot :all}]})
