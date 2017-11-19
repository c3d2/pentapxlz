(ns pentapxlz.webapi.core
  (:require [aleph.http :as http]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer [ok #_not-found]]
            [compojure.core :refer [routes]]
            [compojure.api.sweet :refer [GET api context describe]]
            [compojure.route :refer [#_resources not-found]]
            [pentapxlz.webapi.stream.state :refer [streaming-state-handler]]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]))

(def app
  (routes

    (api
      {:swagger
        {:ui "/api"
         :spec "/api/swagger.json"
         :data {:info {:title "pentaPxlz API"
                       :description "[P]retty e[X]change of [L]ed[z] … [A]nd other [P]ixel [I]nformation"}
                :tags [{:name "api", :description "not stable now"}]}}}

      (context "/api" []
        :tags ["api"]
        :coercion :spec  ;; TODO: coercion+descriptions
  
        (GET "/pxlzstate" []
          :summary "Request the current pxlzstate of a [target]"
          :query-params [{target :- string? "ledbeere"}
                         {streamevery :- spec/int? 0}
                         {ansicolor :- boolean? false}
                         {rgbcolor :- boolean? false}
                         {reversed :- boolean? false}
                         {separator :- string? ""}
                         {padding :- spec/int? 200}]
          streaming-state-handler)))

    (not-found (str "<div align=\"center\">"
                    "No such page, but you can use the" "<br/>"
                    "&lt;&lt;&lt; " "<a href=\"/api\">pentaPxlz API Documentation</a>" " /&gt;&gt;"
                    "</div>"))))

(defn server-start []
  (http/start-server app {:raw-stream? true
                          :host "0.0.0.0"
                          :port 8080}))

(defonce server (server-start))

(defn server-restart []
  (if-let [s (resolve 'server)]
    (.close server))
  (def server (server-start)))
