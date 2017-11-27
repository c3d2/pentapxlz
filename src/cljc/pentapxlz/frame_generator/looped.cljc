(ns pentapxlz.frame-generator.looped
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn looped [s]
  "returns a lazy sequence of the looped items of s"
  (for [i (range)]
       (nth s (mod i (count s)))))

(defn looped-generator
  "loops the result of the generator specified by the first item of chain"
  [{:keys [chain] :as args}]
  (->> [:type (first chain) :chain (rest chain)]
       (apply assoc args)
       resolve-generator
       looped))

(defmethod resolve-generator
  :generator/looped [opts] (looped-generator opts))
