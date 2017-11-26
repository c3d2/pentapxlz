(ns pentapxlz.process.util.loop
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :as a]
            [pentapxlz.state]))

(defn timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(def exit ::exit)

;the macro in clj doesn't work for clojurescript (no idea why) so we need to reimplement the needed loops
(defn start-timed-go-step-animator [{:keys [framerate state step-fn]}]
  (let [control-channel (a/chan)
        state-atom (pentapxlz.state/resolve-atom state)
        timeout-duration (timeout-in-ms framerate)]
    (go-loop []
      (let [timeout-chan (a/timeout timeout-duration)]
        (swap! state-atom step-fn)
        (a/<! timeout-chan)
        (let [[command source] (a/alts! [control-channel] :default ::none)]
          (if (not= exit command)
            (recur)))))
    {:control-chan control-channel}))
