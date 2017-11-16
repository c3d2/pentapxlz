(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.ustripe-clojure.core :refer [pxlz-renderer!]]))

(defonce pxlz (atom {:ledbeere {:nrPxlz 226
                                :colors (fn [r g b] [b g r])
                                :ustripe {:host "ledbeere.hq.c3d2.de"
                                          :port 2342}}
                     :ledball1 {:nrPxlz 640
                                :colors (fn [r g b] [r b g])
                                #_#_:geometry {:spiral [48, 59, 70, 75, 80, 71, 61, 57, 42, 37, 26, 17, 31]}
                                :ustripe {:host "ledball1.hq.c3d2.de"
                                          :port 2342}}}))

(defn map-colors [rgbPxlz colormapping]
  (map #(apply colormapping %) rgbPxlz))

(defn set-rgbPxlz! [targets rgbPxlz]
  (doseq [target targets]
    (let [pxlzState (map-colors rgbPxlz (get-in @pxlz [target :colors]))
  
          nrPxlz (get-in @pxlz [target :nrPxlz])
          pxlzStateLimited (take nrPxlz pxlzState)
          pxlzStatePadding (->> (repeat [0 0 0])
                                (take (- nrPxlz (count pxlzStateLimited))))
          pxlzStateAligned (concat pxlzStateLimited pxlzStatePadding)]
         (swap! pxlz #(assoc-in % [target :pxlzState] pxlzStateAligned)))))

(defn -main
  [& args]
  (set-rgbPxlz! [:ledbeere :ledball1] (repeat [0 10 20]))
  (def myRenderer (pxlz-renderer! pxlz [:ledbeere :ledball1] 50)))
