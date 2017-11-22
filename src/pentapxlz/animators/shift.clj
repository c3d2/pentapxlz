(ns pentapxlz.animators.shift
  (:require [pentapxlz.animators.common :as common]
            [pentapxlz.processes.resolve :as r]))

(defn shift-animator [{:keys [framerate state-atom offset] :as opts}]
  (common/timed-future-step-animator
    (assoc opts
      :step-fn (fn [s]
                 (take (count s)
                       (drop (mod offset (count s))
                             (concat s s)))))))

(defmethod r/resolve-process
  :animator/shift [opts] (shift-animator opts))