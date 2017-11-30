(ns pentapxlz.webapi.core
  (:require [aleph.http :as http]
            [compojure.core :refer [routes]]
            [compojure.api.sweet :refer [#_GET api context]]
            [compojure.route :refer [#_resources not-found]]
            [ring.middleware.resource :refer [wrap-resource]]
            [pentapxlz.config :refer [config reload-config!]]
            [pentapxlz.webapi.websockets.sente :refer [sente-app]]
            [pentapxlz.webapi.stream.state :refer [streaming-frame-state-handler]]
            [fn2api.web.compojure :refer [->context]]
            [pentapxlz.webapi.set-state :refer [set-frame set-frame-segments]]
            [taoensso.timbre :as t]))

(defn create-app []
  (routes
    (api
      {:swagger
        {:ui "/api"
         :spec "/api/swagger.json"
         :data {:info {:title "pentaPxlz API"
                       :description "[P]retty e[X]change of [L]ed[z] … [A]nd other [P]ixel [I]nformation"}
                :tags [{:name "api" :description "not stable now"}]}}}

      (context "/api" []
        :tags ["api"]
        :coercion :spec

        (streaming-frame-state-handler "/frames")
        (->context (var set-frame) "/frames")
        (->context (var set-frame) "/frame-segments")))

    sente-app

    (not-found (str "<div align=\"center\">"
                    "No such page, but you can use the" "<br/>"
                    "&lt;&lt;&lt; " "<a href=\"/api\">pentaPxlz API Documentation</a>" " /&gt;&gt;"
                    "</div>"))))

(defonce server (atom nil))

(defn server-start []
  (reload-config!)
  (if @server
    (t/info "Server already running.")
    (reset! server
            (http/start-server (wrap-resource (create-app) "public")
                               {:raw-stream? false      ;; otherwise problems with PUT/POST
                                :host        (get-in @config [:webserver :host])
                                :port        (get-in @config [:webserver :port])}))))

(defn server-restart []
  (if-let [s @server]
    (reset! server (.close s)))
  (server-start))
