(ns pentapxlz.frame-generator.spiral
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            [pentapxlz.frame-generator.segments :refer [segments]]
            [pentapxlz.config :refer [config]]))

(defn- zipvector [as bs]
  (mapv (fn [a b] [a b]) as bs))

(defn- bisect-segments [segments]
  (let [segments-quot2 (map #(quot % 2) segments)
        segments-rest (mapv - segments segments-quot2)]
    (interleave segments-quot2 segments-rest)))

(defn spiral-generator [{:keys [geometry]}]
  (segments (zipvector (-> (if geometry
                               geometry
                               (get-in @config [:layout :ledball1 :geometry :spiral]))
                           bisect-segments
                           bisect-segments)
                       (cycle [:red :blue :yellow :green]))))

(defmethod resolve-generator
  :generator/spiral [opts] (spiral-generator opts))
