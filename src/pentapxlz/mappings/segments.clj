(ns pentapxlz.mappings.segments
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.colors :refer :all]))

(defn segments [nr+colors]
  (->> (for [[nr color] nr+colors]
            (take nr (repeat color)))
       (apply concat)))

(defn looped [s]
  (for [i (range)]
       (nth s (mod i (count s)))))

(defn set-example-spiral-latitude! [targets]
  (set-rgbPxlz! (segments (zipmap (get-in @pxlz [:ledball1 :geometry :spiral])
                          (looped [red green blue yellow])))
                targets))
