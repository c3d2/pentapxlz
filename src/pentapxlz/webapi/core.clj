(ns pentapxlz.webapi.core
  (:require [aleph.http :as http]
            [compojure.core :refer [routes]]
            [compojure.api.sweet :refer [#_GET api context]]
            [compojure.route :refer [#_resources not-found]]
            [pentapxlz.config :refer [config]]
            [pentapxlz.webapi.stream.state :refer [streaming-state-handler streaming-atom-state-handler]]
            [pentapxlz.webapi.set-state :refer [put-state-handler put-state-segments-handler]]
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
        (put-state-handler "/frames")
        (streaming-state-handler "/frames")
        (put-state-segments-handler "/segments")
        (streaming-atom-state-handler "/frames2")))

    (not-found (str "<div align=\"center\">"
                    "No such page, but you can use the" "<br/>"
                    "&lt;&lt;&lt; " "<a href=\"/api\">pentaPxlz API Documentation</a>" " /&gt;&gt;"
                    "</div>"))))

(defonce server (atom nil))

(defn server-start []
  (if @server
    (t/info "Server already running.")
    (reset! server
            (http/start-server (create-app)
                               {:raw-stream? false      ;; otherwise problems with PUT/POST
                                :host        (get-in config [:webserver :host])
                                :port        (get-in config [:webserver :port])}))))

(defn server-restart []
  (if-let [s @server]
    (.close s))
  (server-start))
