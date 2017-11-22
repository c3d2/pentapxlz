(ns pentapxlz.animators.shift)

(defn shift-animator [{:keys [framerate state-atom offset] :as opts}]
  (pentapxlz.animators.common/timed-future-step-animator
    (assoc opts
      :step-fn (fn [s]
                 (take (count s)
                       (drop (mod offset (count s))
                             (concat s s)))))))
