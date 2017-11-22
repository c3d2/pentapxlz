(ns pentapxlz.mappings.spiral
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.colors :refer :all]
            [pentapxlz.mappings.segments :refer [segments looped zipvector]]))

(defn bisect-segments [segments]
  (let [segments-quot2 (map #(quot % 2) segments)
        segments-rest (mapv - segments segments-quot2)]
       (interleave segments-quot2 segments-rest)))

(defn set-example-spiral-longitude-segments! [targets]
  (set-rgbPxlz! (segments (zipvector (-> (get-in @pxlz [(first targets) :geometry :spiral])
                                         bisect-segments
                                         bisect-segments)
                                     (looped [red blue yellow green])))
                targets))

(defn set-example-spiral-longitude! [targets]
  (let [spiralsegments (-> (get-in @pxlz [(first targets) :geometry :spiral])
                           bisect-segments
                           bisect-segments)]
       (set-rgbPxlz! (segments (interleave (looped [[1 red] [1 green] [1 yellow] [1 green]])
                                           (map #(vector (dec %) [1 1 1]) spiralsegments)))
                     targets)))
