(ns pentapxlz.process.animator.common
  (:require [clojure.core.async :refer [<! timeout]]
            [pentapxlz.state :as state]
            [com.rpl.specter :as sp]))

(defn- timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(defn- start-timed-future-step-animator [{:keys [framerate state step-fn]}]
  (let [timeout-duration (timeout-in-ms framerate)
        state-atom (state/resolve-atom state)
        fut (future (loop []
                      (swap! state-atom step-fn)
                      (Thread/sleep timeout-duration)
                      (recur)))]
    {:future fut}))

(defn timed-future-step-animator [opts]
  (into opts
        {:start-fn (fn [this]
                     (-> this
                         (merge (start-timed-future-step-animator opts))))
         :stop-fn  (fn [{:keys [future] :as this}]
                     (future-cancel future)
                     (dissoc this :future))}))

(defn specter-animator [{:keys [framerate state nav transform-fn] :as opts}]
  (timed-future-step-animator
    (assoc opts
      :step-fn #(sp/transform nav transform-fn %))))


