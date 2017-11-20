(ns pentapxlz.animations.shift
  (:require [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]))

(defn animate-shift! [targets timeout offset]
  (future (loop [] (doseq [target targets]
                          (set-rgbPxlz! (let [s (get-in @pxlz [target :frame])]
                                             (drop (mod offset (count s))
                                                   (concat s s))) [target] false))
                   (Thread/sleep timeout)
                   (recur))))
