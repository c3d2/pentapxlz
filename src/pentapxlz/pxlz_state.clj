(ns pentapxlz.pxlz-state
  (:require [pentapxlz.config :refer [config]]
            [pentapxlz.colors :refer [colormapX+colormapY->colorX->colorY normalize-hysteresis black]]))

(defonce pxlz (atom (:pxlz config))) ;; todo: reader extension for colors

(defn map-colors [rgbPxlz target]
  (let [colormapY (get-in @pxlz [target :colors])
        colormapping (colormapX+colormapY->colorX->colorY [:r :g :b] colormapY)
        brightMax (get-in @pxlz [target :brightMax])]
       (->> rgbPxlz
            (map colormapping)
            (map #(normalize-hysteresis % brightMax)))))

(defn set-rgbPxlz!
  ([rgbPxlz targets] (set-rgbPxlz! rgbPxlz targets true))
  ([rgbPxlz targets needColorMapping]
   (doseq [target targets]
     (let [frame (if needColorMapping
                         (map-colors rgbPxlz target)
                         rgbPxlz)
           nrPxlz (get-in @pxlz [target :nrPxlz])
           wellSizedFrame (take nrPxlz (concat frame (repeat black)))]
          (swap! pxlz #(assoc-in % [target :frame] wellSizedFrame))))))
