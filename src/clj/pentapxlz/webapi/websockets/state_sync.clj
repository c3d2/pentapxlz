(ns pentapxlz.webapi.websockets.state-sync
  (:require [pentapxlz.webapi.websockets.sente :refer [chsk-send! connected-uids]]
            [pentapxlz.state :refer [resolve-atom]]
            [clojure.core.async :as async :refer [go]]))

(defn broadcast-state-sync! [state-atom-name]
  (let [uids (:any @connected-uids)]
       (go (doseq [uid uids]
                  (chsk-send! uid [::sync {:state-atom-name state-atom-name
                                           :new-state @(resolve-atom state-atom-name) }])))))

(defn add-watch-state-sync [state-atom-name]
  (add-watch (resolve-atom state-atom-name)
             :sync1
             (fn [_ _ _ _]
             (broadcast-state-sync! state-atom-name))))
