(ns pentapxlz.core
  (:require [pentapxlz.config :as config]
            [pentapxlz.process.util.registry :as p]
            [pentapxlz.process.util.resolve :as r]
            [pentapxlz.process.renderer.quil]
            [pentapxlz.frame-generator.constant]
            [pentapxlz.frame-generator.spiral]
            [pentapxlz.process.animator.shift]
            [pentapxlz.state :as state]
            [pentapxlz.webapi.websockets.sente]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn restart-processes!
    "Reloads the config and restarts all running and autostarted process"
    []
    (let [started (into #{} (p/ls-started))]
      (p/unregister-all!)
      (let [processes config/processes
            auto-start config/auto-start]
        (doseq [[k process-map] processes]
          (p/register k (r/resolve-process process-map)))
        (apply p/start! (into started auto-start)))))

(defn -main [& args]
  (state/init-states! config/init-states)
  (restart-processes!))
