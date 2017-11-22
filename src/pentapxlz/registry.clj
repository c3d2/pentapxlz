(ns pentapxlz.registry
  (:require [taoensso.timbre :as t]))

;namespace to start and stop processes like renderers.
;to handle a process via this registry, the process needs to conform to the following conditions:
; * is a map
; * has a key :start-fn of process-map -> process-map
; * the new process map needs to have a key :stop-fn of process-map -> process-map

;the base registry
(defonce registry (atom {}))

(defn- throw-not-registered [key]
  (throw (ex-info (str key " is not registered.") {:registered-keys (keys @registry)})))

(defn register [key process-map]
  {:pre [(:start-fn process-map)]}
  (if (@registry key)
    (throw (ex-info (str key " is already registered.") {:registered-process process-map}))
    (swap! registry assoc key process-map)))

(defn start [key]
  (if-let [process (@registry key)]
    (if (::started process)
      (throw (ex-info (str "Process " key " already started.") {:strated-process process}))
      (let [started-process (assoc ((:start-fn process) process)
                              ::started true)]
        (t/debug "Started process %s" key)
        (if (not (:stop-fn started-process))
          (t/warnf "Process %s returned no stop-fn. It might be running but cannot be stopped." key))
        (swap! registry assoc key started-process)))
    (throw-not-registered key)))

(defn stop [key]
  (if-let [process (@registry key)]
    (if-let [stop-fn (:stop-fn process)]
      (let [stopped-process (dissoc (stop-fn process)
                                    ::started)]
        (t/debug "Stopped process %s" key)
        (swap! registry assoc key stopped-process))
      (throw (ex-info (str key " as no stop-fn. Cannot be stopped.") {:unstoppable-process process})))
    (throw-not-registered key)))

(defn unregister
  "Unregisters and potentially stops a process"
  [key]
  (if-let [process (@registry key)]
    (do
      (cond (and (::started process) (:stop-fn process))
            (do (t/warn "Registered process %s was started. Stopping it.")
                ((:stop-fn process) process))

            (::started process)
            (t/warn "Registered process %s was started but has no stop-fn."))
      (swap! registry dissoc key))
    (throw-not-registered key)))

(defn register-and-start [key process-map]
  (register key process-map)
  (start key))