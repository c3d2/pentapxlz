(defproject pentapxlz "0.1.0-SNAPSHOT"
  ;:description "FIXME: write description"
  ;:url "http://example.com/FIXME"
  ;:license {:name "Eclipse Public License"
  ;          :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 ;[spectrum "0.1.4-joe"]  ;; fix for bigdec? missing in clojure 1.9.0-RC1
                ]
  :main ^:skip-aot pentapxlz.core
  :target-path "target/%s"
  :profiles { :ustripe-clojure {:dependencies [[aleph "0.4.4"]
                                               [manifold "0.1.6"]]}
              :dev [:ustripe-clojure {:plugins [[lein-ring "0.11.0"]]}]
              :uberjar [:ustripe-clojure {:aot :all}]})
