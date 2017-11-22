(ns pentapxlz.renderer.ustripe
  (:require [aleph.udp :as udp]
            [manifold.stream :as mf]
            [clojure.core.async :refer [go-loop timeout <! >!] :as async]))

(defn- concat-pixels [pixels]
  (reduce (fn [result [r g b]]
            (conj result r g b))
          []
          pixels))

(defn- build-message [prio frame]
  (let [msg (->> frame
                 (concat-pixels)
                 (map unchecked-byte))
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

(defn- start-ustrip-renderer
  [{:keys [host port prio framerate frame-atom]}]
  (let [timeout-duration (timeout-in-ms framerate)
        socket @(udp/socket {})
        control-chan (async/chan)
        result-chan
        (go-loop []
          (let [timeout-chan (timeout timeout-duration)
                message (build-message prio @frame-atom)]
            (send-message! host port socket message)
            (<! timeout-chan)
            (let [[command source] (async/alts! [control-chan] :default ::none)]
              (if (not= ::exit command)
                (recur)))))]
    {:socket socket
     :result-chan result-chan
     :control-chan control-chan}))

(defn- stop-ustrip-renderer!
  [{:keys [socket control-chan]}]
  (async/put! control-chan ::exit)
  (async/close! control-chan)
  (mf/close! socket))

(defn ustripe-renderer [opts]
  {:opts opts
   :start-fn (fn [{:keys [opts] :as this}]
               (-> this
                   (merge (start-ustrip-renderer opts))
                   (assoc :stop-fn
                          (fn [this]
                            (stop-ustrip-renderer! this)
                            (dissoc this :stop-fn)))))})
