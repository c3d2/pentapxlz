(ns pentapxlz.frame-generator.spiral
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            #_[pentapxlz.frame-generator.segments :refer [segments]]
            [pentapxlz.frame-generator.stretched :refer [stretched]]
            [pentapxlz.frame-generator.rainbow :refer [rainbow-generator]]
            [pentapxlz.config :refer [config]]))

#_(defn- zipvector [as bs]
  (mapv (fn [a b] [a b]) as bs))

#_(defn- bisect-segments [segments]
  (let [segments-quot2 (map #(quot % 2) segments)
        segments-rest (mapv - segments segments-quot2)]
    (interleave segments-quot2 segments-rest)))

#_(defn spiral-generator-simple [{:keys [geometry]}]
  (segments (zipvector (-> (or geometry (get-in @config [:layout :ledball1 :geometry :spiral]))
                           bisect-segments
                           bisect-segments)
                       (cycle [:red :blue :yellow :green]))))

(defn spiral-generator-better [{:keys [geometry small-frame]}]
  (->> (for [line-length (or geometry (get-in @config [:layout :ledball1 :geometry :spiral]))]
            (stretched line-length (or small-frame (rainbow-generator {}))))
       (apply concat)))

(defmethod resolve-generator
  :generator/spiral [opts] (spiral-generator-better opts))
