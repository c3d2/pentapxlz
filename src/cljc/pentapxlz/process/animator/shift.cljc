(ns pentapxlz.process.animator.shift
  (:require [pentapxlz.process.animator.common :as common]
            [pentapxlz.process.util.resolve :as r]
            [com.rpl.specter :as sp :include-macros true]))

(defn shift-fn [offset]
  (fn [s]
    (into []
          (take (count s)
                (drop (mod offset (count s))
                      (concat s s))))))

(defn shift-animator [{:keys [framerate state offset] :as opts}]
  (common/timed-go-step-animator
    (assoc opts
      :step-fn (shift-fn offset))))

(defn full-shift-animation [base-frame]
  (into []
        (take (count base-frame)
              (iterate (shift-fn 1) base-frame))))

(defmethod r/resolve-process
  :animator/shift [opts] (shift-animator opts))

(defn color-shift-animator [{:keys [framerate state color] :as opts}]
  (common/specter-animator
    (merge opts
           {:nav [sp/ALL ({:red 0 :green 1 :blue 2} color)]
            :transform-fn #(mod (inc %) 255)})))

(defmethod r/resolve-process
  :animator/color-shift [opts] (color-shift-animator opts))