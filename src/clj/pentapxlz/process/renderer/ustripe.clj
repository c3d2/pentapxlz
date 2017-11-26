(ns pentapxlz.process.renderer.ustripe
  "renderer that uses the ustripe to render frame or animation.
   For ustriped see: https://github.com/astro/pile/tree/master/ustriped"
  (:require [aleph.udp :as udp]
            [manifold.stream :as mf]
            [clojure.core.async :refer [go-loop timeout <! >!] :as async]
            [pentapxlz.colors :as c]
            [pentapxlz.process.util.resolve :as r]
            [pentapxlz.state :as state]))

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

(defn- start-frame-loop [host port socket
                         control-chan message-builder timeout-duration
                         frame-atom]
  (go-loop []
    (let [timeout-chan (timeout timeout-duration)
          message (message-builder @frame-atom)]
      (send-message! host port socket message)
      (<! timeout-chan)
      (let [[command source] (async/alts! [control-chan] :default ::none)]
        (if (not= ::exit command)
          (recur))))))


(defn- timeout-in-ms [framerate]
  (long (max (quot 1000 framerate) 1)))

(defn- build-ustripe-renderer-fn [loop-fn]
  (fn
    [{:keys [device prio framerate state]}]
    (let [{:keys [colors brightMax ustripe layout]} device
          {:keys [host port]} ustripe
          {:keys [nrPxlz]} layout
          timeout-duration (timeout-in-ms framerate)
          message-builder (partial build-message prio brightMax colors nrPxlz)
          frame-atom (state/resolve-atom state)
          socket @(udp/socket {})
          control-chan (async/chan)
          result-chan (loop-fn host port socket control-chan message-builder timeout-duration frame-atom)]
      {:socket       socket
       :result-chan  result-chan
       :control-chan control-chan})))

(def start-ustripe-frame-renderer
  (build-ustripe-renderer-fn start-frame-loop))

(defn- stop-ustripe-renderer!
  [{:keys [socket control-chan]}]
  (async/put! control-chan ::exit)
  (async/close! control-chan)
  (mf/close! socket))

(defn ustripe-frame-renderer [opts]
  (into opts
        {:start-fn (fn [this]
                     (-> this
                         (merge (start-ustripe-frame-renderer this))
                         (assoc :stop-fn
                                (fn [this]
                                  (stop-ustripe-renderer! this)
                                  (dissoc this :stop-fn)))))}))

(defmethod r/resolve-process
  :renderer/ustripe-frame [opts] (ustripe-frame-renderer opts))

;;------------ ustripe-animation-renderer
(defn- start-animation-loop [host port socket
                             control-chan message-builder timeout-duration
                             animation-atom]
  (go-loop [current-index 0]
    (let [timeout-chan (timeout timeout-duration)
          message (message-builder (get @animation-atom current-index []))]
      (send-message! host port socket message)
      (<! timeout-chan)
      (let [[command source] (async/alts! [control-chan] :default ::none)]
        (if (not= ::exit command)
          (recur (mod (inc current-index) (max (count @animation-atom) 1))))))))

(def start-ustripe-animation-renderer
  (build-ustripe-renderer-fn start-animation-loop))

(defn ustripe-animation-renderer [opts]
  (into opts
        {:start-fn (fn [this]
                     (-> this
                         (merge (start-ustripe-animation-renderer this))
                         (assoc :stop-fn
                                (fn [this]
                                  (stop-ustripe-renderer! this)
                                  (dissoc this :stop-fn)))))}))

(defmethod r/resolve-process
  :renderer/ustripe-animation [opts] (ustripe-animation-renderer opts))