(ns pentapxlz.process.animator.common
  #?(:cljs (:require-macros [cljs.core.async :refer [go]]))
  (:require #?(:clj [clojure.core.async :refer [go <! timeout] :as a]
               :cljs [cljs.core.async :refer [<! timeout] :as a :include-macros true])
            [pentapxlz.state :as state]
            [pentapxlz.process.util.loop :as loop]
            [com.rpl.specter :as sp :include-macros true]))

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

(defn timed-go-step-animator [opts]
  (into opts
        {:start-fn (fn [this]
                     (-> this
                         (merge (loop/start-timed-go-step-animator opts))))
         :stop-fn (fn [{:keys [control-chan] :as this}]
                    (go (a/>! control-chan loop/exit))
                    (dissoc this :control-chan))}))

(defn specter-animator [{:keys [framerate state nav transform-fn] :as opts}]
  (timed-go-step-animator
    (assoc opts
      :step-fn #(sp/transform nav transform-fn %))))


