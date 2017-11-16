(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.ustripe-clojure.core :refer [pxlz-renderer!]]
            [pentapxlz.colors :as colors :refer [normalize-hysteresis]]))

(defonce pxlz (atom {:ledbeere {:nrPxlz 226
                                :colors (fn [r g b] [b g r])
                                :brightMax (* 3 0xff)
                                :ustripe {:host "ledbeere.hq.c3d2.de"
                                          :port 2342}}
                     :ledball1 {:nrPxlz 640
                                :colors (fn [r g b] [r b g])
                                :brightMax (* 2 0xff)
                                #_#_:geometry {:spiral [48, 59, 70, 75, 80, 71, 61, 57, 42, 37, 26, 17, 31]}
                                :ustripe {:host "ledball1.hq.c3d2.de"
                                          :port 2342}}}))

(defn map-colors [rgbPxlz target]
  (let [colormapping (get-in @pxlz [target :colors])
        brightMax (get-in @pxlz [target :brightMax])]
       (->> rgbPxlz
            (map #(apply colormapping %))
            (map #(normalize-hysteresis % brightMax)))))

(defn set-rgbPxlz! [rgbPxlz targets]
  (doseq [target targets]
    (let [pxlzState (map-colors rgbPxlz target)
  
          nrPxlz (get-in @pxlz [target :nrPxlz])
          pxlzStateLimited (take nrPxlz pxlzState)
          pxlzStatePadding (->> (repeat colors/black)
                                (take (- nrPxlz (count pxlzStateLimited))))
          pxlzStateAligned (concat pxlzStateLimited pxlzStatePadding)]
         (swap! pxlz #(assoc-in % [target :pxlzState] pxlzStateAligned)))))

(defn -main
  [& args]
  (set-rgbPxlz! (repeat colors/aqua) [:ledbeere :ledball1])
  (def myRenderer (pxlz-renderer! pxlz [:ledbeere :ledball1] 50)))
