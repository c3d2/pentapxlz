(ns pentapxlz.frame-generator.constant
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn constant-generator
  "Generates a frame of a single 'color' with 'length'"
  ;; like the segments-generator with only one segment
  [{:keys [color length]}]
  (repeat length color))

(defmethod resolve-generator
  :generator/constant [opts] (constant-generator opts))
