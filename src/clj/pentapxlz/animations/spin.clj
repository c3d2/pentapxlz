(ns pentapxlz.animations.spin
 #_ (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
              [pentapxlz.mappings.figlet :refer [set-example-figlet-ccc!]]))
#_
(defn animate-spin! [timeout]
  (future (loop []
                (set-example-figlet-ccc! (rand-int 91) (+ 1 (rand-int 2)))
                (Thread/sleep timeout)
                (recur))))
