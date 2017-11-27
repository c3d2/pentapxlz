(ns pentapxlz.frame-generator.segments
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn segments [nr+colors]
  (->> (for [[nr color] nr+colors]
         (repeat nr color))
       (apply concat)))

(defn segments-generator
  [{:keys [nr+colors]}]
  (segments nr+colors))

(defmethod resolve-generator
  :generator/segments [opts] (segments-generator opts))
