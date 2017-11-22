(ns pentapxlz.processes.registry
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

(defn register
  ([key->process-map]
   (doseq [[k process-map] key->process-map]
     (register k process-map)))
  ([key process-map]
   {:pre [(:start-fn process-map)]}
   (if (@registry key)
     (throw (ex-info (str key " is already registered.") {:registered-process process-map}))
     (do (swap! registry assoc key process-map)
         (t/infof "Registered process %s" key)))))

(defn- start!* [key]
  (if-let [process (@registry key)]
    (if (::started process)
      (throw (ex-info (str "Process " key " already started.") {:strated-process process}))
      (let [started-process (assoc ((:start-fn process) process)
                              ::started true)]
        (if (not (:stop-fn started-process))
          (t/warnf "Process %s returned no stop-fn. It might be running but cannot be stopped." key))
        (swap! registry assoc key started-process)
        (t/infof "Started process %s" key)))
    (throw-not-registered key)))

(defn start! [& keys]
  (doseq [k keys]
    (start!* k)))

(defn- stop!* [key]
  (if-let [process (@registry key)]
    (if-let [stop-fn (:stop-fn process)]
      (let [stopped-process (dissoc (stop-fn process)
                                    ::started)]
        (swap! registry assoc key stopped-process)
        (t/infof "Stopped process %s" key))
      (throw (ex-info (str key " as no stop-fn. Cannot be stopped.") {:unstoppable-process process})))
    (throw-not-registered key)))

(defn stop! [& keys]
  (doseq [k keys]
    (stop!* k)))

(defn- unregister*
  "Unregisters and potentially stops a process"
  [key]
  (if-let [process (@registry key)]
    (do
      (cond (and (::started process) (:stop-fn process))
            (do (t/warnf "Registered process %s was started. Stopping it." key)
                (stop! key))
            (::started process)
            (t/warnf "Registered process %s was started but has no stop-fn." key))
      (swap! registry dissoc key)
      (t/infof "Unregistered process %s" key))
    (throw-not-registered key)))

(defn unregister [& keys]
  (doseq [k keys]
    (unregister* k)))

(defn register-and-start [key process-map]
  (register key process-map)
  (start! key))

(defn stop-all! []
  (doseq [k (keys @registry)]
    (if (::started (@registry k))
      (stop! k))))

(defn start-all! []
  (doseq [k (keys @registry)]
    (if (not (::started (@registry k)))
      (start! k))))

(defn restart-all! []
  (stop-all!)
  (start-all!))

(defn unregister-all! []
  (doseq [k (keys @registry)]
    (unregister k)))

(defn restart! [& keys]
  (apply stop! keys)
  (apply start! keys))

(defn ls []
  (keys @registry))

(defn ls-started []
  (map first (filter (fn [[k v]] (::started v))
                     @registry)))
