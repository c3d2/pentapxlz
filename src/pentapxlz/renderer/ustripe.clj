(ns pentapxlz.renderer.ustripe
  (:require [aleph.udp :as udp]
            [manifold.stream :as mf]
            [clojure.core.async :refer [go-loop timeout <! >!] :as async]
            [pentapxlz.colors :as c]
            [pentapxlz.processes.resolve :as r]))

(defn- concat-pixels-transducer [xf]
  (fn
    ([] (xf))
    ([result] (xf result))
    ([result [r g b]]
     (-> result
         (xf r) (xf g) (xf b)))))

(defn frame->bytes [bright-max color-map pixel-count frame]
  (into []
    (comp
      (take pixel-count)
      (map c/->rgb)
      (map (c/colormapX+colormapY->colorX->colorY [:r :g :b] color-map))
      (map #(c/normalize-hysteresis % bright-max))
      concat-pixels-transducer
      (map unchecked-byte))
    (concat frame (repeat [0 0 0]))))

(defn- build-message [prio bright-max color-map pixel-count frame]
  (let [msg (frame->bytes bright-max color-map pixel-count frame)
        header [(byte prio)
                (byte 0x00)  ;; command
                (unchecked-byte (bit-shift-right (count msg) 8)) ;; 1st byte length
                (unchecked-byte (bit-and 0xff (count msg)))]]  ;; 2nd byte length
       (byte-array (concat header msg))))

(defn- send-message! [host port socket message]
  (let [send-success? (mf/put! socket
                            {:host host
                             :port port
                             :message message})]
    @send-success?))

(defn- timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(defn- start-ustripe-renderer
  [{:keys [host port prio framerate frame-atom bright-max color-map pixel-count]}]
  (let [timeout-duration (timeout-in-ms framerate)
        message-builder (partial build-message prio bright-max color-map)
        socket @(udp/socket {})
        control-chan (async/chan)
        result-chan
        (go-loop []
          (let [timeout-chan (timeout timeout-duration)
                message (message-builder @frame-atom)]
            (send-message! host port socket message)
            (<! timeout-chan)
            (let [[command source] (async/alts! [control-chan] :default ::none)]
              (if (not= ::exit command)
                (recur)))))]
    {:socket socket
     :result-chan result-chan
     :control-chan control-chan}))

(defn- stop-ustripe-renderer!
  [{:keys [socket control-chan]}]
  (async/put! control-chan ::exit)
  (async/close! control-chan)
  (mf/close! socket))

(defn ustripe-frame-renderer [opts]
  {:opts opts
   :start-fn (fn [{:keys [opts] :as this}]
               (-> this
                   (merge (start-ustripe-renderer opts))
                   (assoc :stop-fn
                          (fn [this]
                            (stop-ustripe-renderer! this)
                            (dissoc this :stop-fn)))))})

(defmethod r/resolve-process
  :renderer/ustripe-frame [opts] (ustripe-frame-renderer opts))