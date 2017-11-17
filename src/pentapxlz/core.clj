(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.ustripe-clojure :refer [pxlz-renderer!]]
            [pentapxlz.mappings.segments :refer [segments looped set-example-spiral-latitude!]]
            [pentapxlz.animations.shift :refer [animate-shift!]]))

(defn -main
  [& args]
  (let [targets [:ledball1 :ledbeere]]
    (set-example-spiral-latitude! targets)
    (def myRenderer (pxlz-renderer! targets 50))
    (def myAnimation (animate-shift! targets 200 1))))

