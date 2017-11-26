(ns pentapxlz.frame-generator.spiral
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defn- segments [nr+colors]
  (->> (for [[nr color] nr+colors]
         (take nr (repeat color)))
       (apply concat)))

(defn- looped [s]
  (for [i (range)]
    (nth s (mod i (count s)))))

(defn- zipvector [as bs]
  (mapv (fn [a b] [a b]) as bs))

(defn- bisect-segments [segments]
  (let [segments-quot2 (map #(quot % 2) segments)
        segments-rest (mapv - segments segments-quot2)]
    (interleave segments-quot2 segments-rest)))

(defn spiral [{:keys [geometry]}]
  (segments (zipvector (-> geometry
                           bisect-segments
                           bisect-segments)
                       (looped [:red :blue :yellow :green]))))

(defmethod resolve-generator
  :generator/spiral [opts] (spiral opts))