(ns pentapxlz.pxlz-state
  (:require [pentapxlz.colors :refer [normalize-hysteresis black]]))

(defonce pxlz (atom {:ledbeere {:nrPxlz 226
                                :colors (fn [r g b] [b g r])
                                :colors-inverse (fn [b g r] [r g b])
                                :brightMax (* 3 0xff)
                                :ustripe {:host "ledbeere.hq.c3d2.de"
                                          :port 2342}}
                     :ledball1 {:nrPxlz 640
                                :colors (fn [r g b] [r b g])
                                :colors-inverse (fn [r b g] [r g b])
                                :brightMax (* 2 0xff)
                                :geometry {:spiral [48, 59, 70, 75, 80, 71, 61, 57, 42, 37, 26, 17, 31]}
                                :ustripe {:host "ledball1.hq.c3d2.de"
                                          :port 2342}}}))

(defn map-colors [rgbPxlz target]
  (let [colormapping (get-in @pxlz [target :colors])
        brightMax (get-in @pxlz [target :brightMax])]
       (->> rgbPxlz
            (map #(apply colormapping %))
            (map #(normalize-hysteresis % brightMax)))))

(defn set-rgbPxlz!
  ([rgbPxlz targets] (set-rgbPxlz! rgbPxlz targets true))
  ([rgbPxlz targets needColorMapping]
   (doseq [target targets]
     (let [pxlzState (if needColorMapping
                         (map-colors rgbPxlz target)
                         rgbPxlz)
   
           nrPxlz (get-in @pxlz [target :nrPxlz])
           pxlzStateLimited (take nrPxlz pxlzState)
           pxlzStatePadding (->> (repeat black)
                                 (take (- nrPxlz (count pxlzStateLimited))))
           pxlzStateAligned (concat pxlzStateLimited pxlzStatePadding)]
          (swap! pxlz #(assoc-in % [target :pxlzState] pxlzStateAligned))))))
