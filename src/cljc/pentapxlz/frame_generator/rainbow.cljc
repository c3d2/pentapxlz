(ns pentapxlz.frame-generator.rainbow
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn rainbow-generator
  "Generates a rainbow"
  [{:keys []}] ;; might want stretch it to wanted length
  (concat (for [g (range 0 255)] [(- 255 g) g 0])
          (for [b (range 0 255)] [0 (- 255 b) b])
          (for [r (range 0 255)] [r 0 (- 255 r)])))

(defmethod resolve-generator
  :generator/rainbow [opts] (rainbow-generator opts))
