(ns pentapxlz.specter
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.macros :as m]
            [pentapxlz.processes.atom-registry :as ar]))

(defn every-pred-index
  "Navigates to all elements of a seq whose indicies satisfy pred"
  [pred]
  (path INDEXED-VALS #(pred (first %)) LAST))

(def EVERY-ODD-ELEM
  (every-pred-index odd?))

(defn state-kw [kw]
  (keyword "state" (name kw)))

(m/defnav STATE-ATOM
  []
  (select* [this s next-fn]
    (next-fn (ar/resolve-atom (state-kw s))))
  (transform* [this s next-fn]
    (do (next-fn (ar/resolve-atom (state-kw s)))
        nil)))

