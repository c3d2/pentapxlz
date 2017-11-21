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

(defn zipvector [as bs]
  (mapv (fn [a b] [a b]) as bs))

(defn set-example-spiral-latitude! [targets]
  (set-rgbPxlz! (segments (zipvector (get-in @pxlz [(first targets) :geometry :spiral])
                                     (looped [red blue yellow green])))
                 targets))
