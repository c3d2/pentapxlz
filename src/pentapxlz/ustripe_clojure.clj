(ns pentapxlz.ustripe-clojure
  "A lib to communicate with ustriped from clojure.
   For ustriped see: https://github.com/astro/pile/tree/master/ustriped"
  (:gen-class)
  (:require [pentapxlz.pxlz-state :refer [pxlz]]
            [aleph.udp :as udp]
            [manifold.stream :refer [put! close!]]
            [clojure.pprint :refer [pprint]]))

(defn pxlz-send! [target]
  (let [client-socket @(udp/socket {})
        msg (->> (get-in @pxlz [target :frame])
                 (apply concat)
                 (map unchecked-byte))
        header [(byte (get-in @pxlz [target :ustripe :prio]))
                (byte 0x00)  ;; command
                (unchecked-byte (bit-shift-right (count msg) 8)) ;; 1st byte length
                (unchecked-byte (bit-and 0xff (count msg)))]      ;; 2nd byte length

        send-success? (put! client-socket
                            {:host (get-in @pxlz [target :ustripe :host])
                             :port (get-in @pxlz [target :ustripe :port])
                             :message (byte-array (concat header msg))})]
       (close! client-socket)
       @send-success?))

(defn pxlz-renderer! [targets timeout]
  (future (loop [] (doseq [target targets]
                          (pxlz-send! target))
                   (Thread/sleep timeout)
                   (recur))))
