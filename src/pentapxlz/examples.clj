(ns pentapxlz.examples
  (:require [pentapxlz.colors :refer :all]
            [pentapxlz.mappings.segments :refer [segments looped zipvector]]))


(defn bisect-segments [segments]
  (let [segments-quot2 (map #(quot % 2) segments)
        segments-rest (mapv - segments segments-quot2)]
    (interleave segments-quot2 segments-rest)))

(defn spiral [geometry]
  (segments (zipvector (-> geometry
                           bisect-segments
                           bisect-segments)
                       (looped [red blue yellow green]))))