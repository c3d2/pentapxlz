(ns pentapxlz.frame-generator.cycled
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn cycled-generator
  "loops the result of the generator specified by the first item of chain"
  [{:keys [chain] :as args}]
  (->> [:type (first chain) :chain (rest chain)]
       (apply assoc args)
       resolve-generator
       cycle))

(defmethod resolve-generator
  :generator/cycled [opts] (cycled-generator opts))
