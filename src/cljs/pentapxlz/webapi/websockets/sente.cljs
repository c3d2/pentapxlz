(ns pentapxlz.webapi.websockets.sente
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! >! put! chan]]
            [taoensso.sente :refer [make-channel-socket! start-client-chsk-router!]]
            [pentapxlz.webapi.websockets.state-sync :refer [handle-state-sync!]]
            [cljs.pprint :refer [pprint]]))

(let [{:keys [chsk ch-recv send-fn state]}
      (make-channel-socket! "/chsk" ; Note the same path as before
      {:type :auto})]
     (def chsk       chsk)
     (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
     (def chsk-send! send-fn) ; ChannelSocket's send API fn
     (def chsk-state state))  ; Watchable, read-only atom

(defn event-msg-handler [{:as ev-msg :keys [id ?data event]}]
  (cond
    (= (first event) :chsk/state)
      '(println :chsk/state)
    (= (first event) :chsk/handshake)
      '(println :chsk/handshake)
    (= event [:chsk/recv [:chsk/ws-ping]])
      '(println :chsk/ws-ping)

    (and (= (first event) :chsk/recv)
         (= (first ?data) :pentapxlz.webapi.websockets.state-sync/sync))
      (handle-state-sync! (second ?data))

    (= (first event) :chsk/recv)
      (pprint {:?data ?data})
    :else
      {:unknown-ev-msg (pprint ev-msg)}))

(start-client-chsk-router! ch-chsk event-msg-handler)
