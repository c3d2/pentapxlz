(ns pentapxlz.core
  (:gen-class)
  (:require [pentapxlz.process.animator.shift :as ashift]
            [pentapxlz.process.renderer.quil]
            [pentapxlz.process.renderer.ustripe]
            [pentapxlz.colors :as c]
            [pentapxlz.config :refer [config reload-config!]]
            [pentapxlz.frame-generator.constant]
            [pentapxlz.frame-generator.segments]
            [pentapxlz.frame-generator.spiral]
            [pentapxlz.frame-generator.cycled]
            [pentapxlz.frame-generator.stretched]
            [pentapxlz.process.util.registry :refer [start! stop! register unregister config!] :as p]
            [pentapxlz.process.util.resolve :refer [resolve-process]]
            [pentapxlz.state :as state]
            [pentapxlz.webapi.core :refer [server-start server-restart]]
            [pentapxlz.webapi.websockets.state-sync :refer [add-watch-state-sync]]))

(defn restart-processes!
  "Reloads the config and restarts all running and autostarted process"
  []
  ;todo fix bug that manually registered processes won't be registered again but tried do restart
  (let [started (into #{} (p/ls-started))]
    (p/unregister-all!)
    (let [config (reload-config!)
          processes (:processes config {})
          auto-start (:processes/auto-start config {})]
      (doseq [[k process-map] processes]
        (println k)
        (register k (resolve-process process-map)))
      (apply start! (into started auto-start)))))

(defn -main [& args]
  (server-start)
  (reload-config!)
  (state/init-states! (:states @config))
  (restart-processes!)
  (add-watch-state-sync :state/ledball1-frame))

