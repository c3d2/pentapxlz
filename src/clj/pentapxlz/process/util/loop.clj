(ns pentapxlz.process.util.loop
  (:require [clojure.core.async :as a :refer [go-loop]]
            [com.rpl.specter :as sp :include-macros true]
            [pentapxlz.state]))

(defn timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(def exit ::exit)

(defn- wrap-recur
  "Wraps a seq '(recur ...) with the time and command channel"
  [control-chan timeout-chan-sym recur-seq]
  `(do (a/<! ~timeout-chan-sym)
       (let [[command# source#] (a/alts! [~control-chan] :default ::none)]
         (if (not= ~exit command#)
           ~recur-seq))))

(defn- recur-seq? [elem]
  (and (list? elem)
       (= 'recur (first elem))))

(defn- transform-body
  [control-chan timeout-chan-sym body]
  (sp/transform (sp/codewalker recur-seq?)
                #(wrap-recur control-chan timeout-chan-sym %)
                body))

; This macros seems not to work in clojurescript, so we need to reimplement the version we need there.
(defmacro timed-stoppable-loop
  "Starts a go-loop that executes in framerate.
   Also reads on control-channel. Suppling ::exit to this channel stops the loop"
  [framerate control-channel bindings & body]
  (let [timeout-sym (gensym "timeout-chan")]
    `(let [timeout-duration# (timeout-in-ms ~framerate)]
       (go-loop ~bindings
         (let [~timeout-sym (a/timeout timeout-duration#)]
           (do
             ~@(transform-body control-channel timeout-sym body)))))))

(defn start-timed-go-step-animator [{:keys [framerate state step-fn]}]
  (let [control-channel (a/chan)
        state-atom (pentapxlz.state/resolve-atom state)]
    (timed-stoppable-loop framerate control-channel
                               []
                               (swap! state-atom step-fn)
                               (recur))
    {:control-chan control-channel}))