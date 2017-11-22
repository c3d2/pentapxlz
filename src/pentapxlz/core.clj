(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.ustripe-clojure :refer [pxlz-renderer!]]
            [pentapxlz.mappings.segments :refer [segments looped set-example-spiral-latitude!]]
            [pentapxlz.mappings.spiral :refer [set-example-spiral-longitude-segments!]]
            [pentapxlz.animations.shift :refer [animate-shift!]]
            [pentapxlz.animations.spin :refer [animate-spin!]]
            [pentapxlz.animators.shift :as ashift]
            [pentapxlz.renderer.quil]
            [pentapxlz.renderer.ustripe]
            [pentapxlz.config :refer [config]]
            [pentapxlz.processes.registry :as pr]
            [pentapxlz.processes.resolve :refer [resolve-process]]
            [pentapxlz.processes.atom-registry :as ar]
            [pentapxlz.examples :as examples]))

(defn -main
  [& args]
  (let [targets [:ledball1 :ledbeere]]
    ;(set-example-spiral-latitude! targets)
    (set-example-spiral-longitude-segments! targets)
    (def myRenderer (pxlz-renderer! targets 50))
    (def myAnimation-spin (animate-spin! 10000))
    (def myAnimation-shift (animate-shift! [:ledbeere] 200 1))
    (require 'pentapxlz.webapi.core)))

(defn -main2 [& args]
  (let [processes (:processes config {})
        auto-start (:processes/auto-start config {})]
    (reset! (ar/resolve-atom :state/ledbeere) (examples/spiral [48 59 69 73 75 71 65 56 46 36 26 20]))
    (doseq [[k process-map] processes]
      (pr/register k (resolve-process process-map)))
    (apply pr/start! auto-start)))

