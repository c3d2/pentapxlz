(ns pentapxlz.specter
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.macros :as m]
            [pentapxlz.state :as state]))

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
    (next-fn (state/resolve-atom (state-kw s))))
  (transform* [this s next-fn]
    (do (next-fn (state/resolve-atom (state-kw s)))
        nil)))

(def STATE
  (path STATE-ATOM ATOM))

(defn partition-in-geo [geometry v]
  (let [asscending (reduce (fn [v e]
                             (conj v (+ (or (last v) 0) e)))
                           [] geometry)
        parts (partition 2 1 (into [0] asscending))]
    (into [] (map #(apply (partial subvec (vec v)) %) parts))))

(defn concatv [vs]
  (reduce (fn [result v]
            (apply conj result v))
          [] vs))

(m/defnav geometry-parts
  [geometry]
  (select* [this s next-fn]
    (next-fn (partition-in-geo geometry s)))
  (transform* [this s next-fn]
    (concatv (next-fn (partition-in-geo geometry s)))))
