(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.ustripe-clojure :refer [pxlz-renderer!]]
            [pentapxlz.mappings.segments :refer [segments looped set-example-spiral-latitude!]]
            [pentapxlz.mappings.spiral :refer [set-example-spiral-longitude-segments!]]
            [pentapxlz.animations.shift :refer [animate-shift!]]
            [pentapxlz.animations.spin :refer [animate-spin!]]
            [pentapxlz.animators.shift :as ashift]
            [pentapxlz.renderer.quil :as qr]
            [pentapxlz.registry :as r]))

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
  (let [frame-atom (atom [[255 0 0] [0 255 0] [0 0 255]])
        quil-renderer (qr/quil-frame-renderer {:frame-atom frame-atom :framerate 60})
        shift-animator (ashift/shift-animator {:framerate 1 :state-atom frame-atom :offset 1})]
    (r/register :animator/shift shift-animator)
    (r/register :renderer/quil quil-renderer)))

