(ns pentapxlz.core
  (:gen-class)
  (:require #_[pentapxlz.ustripe-clojure :refer [pxlz-renderer!]]
            #_[pentapxlz.mappings.segments :refer [segments looped set-example-spiral-latitude!]]
            #_[pentapxlz.mappings.spiral :refer [set-example-spiral-longitude-segments!]]
            #_[pentapxlz.process.animations.shift :refer [animate-shift!]]
            #_[pentapxlz.animations.spin :refer [animate-spin!]]
            [pentapxlz.process.animator.shift :as ashift]
            [pentapxlz.process.renderer.quil]
            [pentapxlz.process.renderer.ustripe]
            [pentapxlz.colors :as c]
            [pentapxlz.config :refer [config reload-config]]
            [pentapxlz.frame-generator.constant]
            [pentapxlz.frame-generator.spiral]
            [pentapxlz.process.util.registry :refer [start! stop! register unregister config!] :as p]
            [pentapxlz.process.util.resolve :refer [resolve-process]]
            [pentapxlz.state :as state]
            #_[pentapxlz.examples :as examples]
            [pentapxlz.webapi.core :refer [server-start server-restart]]))

#_
(defn -main
  [& args]
  (let [targets [:ledball1 :ledbeere]]
    ;(set-example-spiral-latitude! targets)
    (set-example-spiral-longitude-segments! targets)
    (def myRenderer (pxlz-renderer! targets 50))
    (def myAnimation-spin (animate-spin! 10000))
    (def myAnimation-shift (animate-shift! [:ledbeere] 200 1))
    (server-start)))

(defn restart-processes!
  "Reloads the config and restarts all running and autostarted processes"
  []
  (let [started (into #{} (p/ls-started))]
    (p/unregister-all!)
    (let [config (reload-config)
          processes (:processes config {})
          auto-start (:processes/auto-start config {})]
      (doseq [[k process-map] processes]
        (println k)
        (register k (resolve-process process-map)))
      (apply start! (into started auto-start)))))

(defn -main [& args]
  (state/init-states! (:states (reload-config)))
  (restart-processes!)
  (server-start))

