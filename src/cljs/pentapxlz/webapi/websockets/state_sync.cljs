(ns pentapxlz.webapi.websockets.state-sync
  (:require [pentapxlz.state]))

(defn handle-state-sync! [{:keys [state-atom-name new-state]}]
  (pentapxlz.state/set! state-atom-name new-state))
