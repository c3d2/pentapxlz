(ns pentapxlz.animators.common
  (:require [clojure.core.async :refer [<! timeout]]
            [pentapxlz.processes.atom-registry :as ar]))

(defn- timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(defn- start-timed-future-step-animator [{:keys [framerate state step-fn]}]
  (let [timeout-duration (timeout-in-ms framerate)
        state-atom (ar/resolve-atom state)
        fut (future (loop []
                      (swap! state-atom step-fn)
                      (Thread/sleep timeout-duration)
                      (recur)))]
    {:future fut}))

(defn timed-future-step-animator [opts]
  {:opts opts
   :start-fn (fn [{:keys [opts] :as this}]
               (-> this
                   (merge (start-timed-future-step-animator opts))))
   :stop-fn (fn [{:keys [future] :as this}]
                (future-cancel future)
                (dissoc this :future))})

