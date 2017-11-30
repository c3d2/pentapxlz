(ns pentapxlz.webapi.websockets.sente
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.aleph :refer (get-sch-adapter)]
            [ring.middleware.defaults]
            [compojure.core :refer [defroutes GET POST]]))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter) {})]

     (def ring-ajax-post                ajax-post-fn)
     (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
     (def ch-chsk                       ch-recv)         ; ChannelSocket's receive channel
     (def chsk-send!                    send-fn)         ; ChannelSocket's send API fn
     (def connected-uids                connected-uids)) ; Watchable, read-only atom

(defroutes sente-routes
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))

(def sente-app
  (-> sente-routes
      (ring.middleware.defaults/wrap-defaults ring.middleware.defaults/site-defaults)))

