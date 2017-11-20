(ns pentapxlz.webapi.core
  (:require [aleph.http :as http]
            [compojure.core :refer [routes]]
            [compojure.api.sweet :refer [#_GET api context]]
            [compojure.route :refer [#_resources not-found]]
            [pentapxlz.config :refer [config]]
            [pentapxlz.webapi.stream.state :refer [streaming-state-handler]]
            [pentapxlz.webapi.set-state :refer [put-state-handler put-state-segments-handler]]))

(def app
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
        (put-state-handler "/pxlzstate")
        (streaming-state-handler "/pxlzstate")
        (put-state-segments-handler "/segments")))

    (not-found (str "<div align=\"center\">"
                    "No such page, but you can use the" "<br/>"
                    "&lt;&lt;&lt; " "<a href=\"/api\">pentaPxlz API Documentation</a>" " /&gt;&gt;"
                    "</div>"))))

(defn server-start []
  (http/start-server app {:raw-stream? false  ;; otherwise problems with PUT/POST
                          :host (get-in config [:webserver :host])
                          :port (get-in config [:webserver :port])}))

(defonce server (server-start))

(defn server-restart []
  (if-let [s (resolve 'server)]
    (.close server))
  (def server (server-start)))
