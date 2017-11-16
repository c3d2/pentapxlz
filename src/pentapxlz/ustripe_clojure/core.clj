(ns pentapxlz.ustripe-clojure.core
  "A lib to communicate with ustriped from clojure.
   For ustriped see: https://github.com/astro/pile/tree/master/ustriped"
  (:gen-class)
  (:require [aleph.udp :as udp]
            [manifold.stream :refer [put! close!]]
            [clojure.pprint :refer [pprint]]))

(defn pxlz-send! [pxlz target]
  (let [client-socket @(udp/socket {})
        msg (->> (get-in @pxlz [target :pxlzState])
                 (apply concat)
                 (map unchecked-byte))
        header [(byte 0x00)  ;; prio
                (byte 0x00)  ;; command
                (unchecked-byte (bit-shift-right (count msg) 8)) ;; 1st byte length
                (unchecked-byte (bit-and 0xff (count msg)))      ;; 2nd byte length
               ]
        send-success? (put! client-socket
                            {:host (get-in @pxlz [target :ustripe :host])
                             :port (get-in @pxlz [target :ustripe :port])
                             :message (byte-array (concat header msg))})]
       (close! client-socket)
       @send-success?))

(defn pxlz-renderer! [pxlz targets timeout]
  (future (loop [] (doseq [target targets]
                          (pxlz-send! pxlz target))
                          (Thread/sleep timeout)
                          (recur))))
