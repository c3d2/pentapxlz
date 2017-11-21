(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.ustripe-clojure :refer [pxlz-renderer!]]
            [pentapxlz.mappings.segments :refer [segments looped set-example-spiral-latitude!]]
            [pentapxlz.mappings.spiral :refer [set-example-spiral-longitude-segments!]]
            [pentapxlz.animations.shift :refer [animate-shift!]]
            [pentapxlz.animations.spin :refer [animate-spin!]]))

(defn -main
  [& args]
  (let [targets [:ledball1 :ledbeere]]
    ;(set-example-spiral-latitude! targets)
    ;(set-example-spiral-longitude-segments! targets)
    (def myRenderer (pxlz-renderer! targets 50))
    ;(def myAnimation (animate-shift! targets 200 1))
    (def myAnimation (animate-spin! 10000))
    (require 'pentapxlz.webapi.core)))

