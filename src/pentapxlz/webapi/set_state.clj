(ns pentapxlz.webapi.set-state
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [PUT]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.mappings.segments :as seg]))

(s/def ::color (s/int-in 0 256))
(s/def ::pixel (s/tuple ::color ::color ::color))
(s/def ::pxlzstate (s/coll-of ::pixel))

(defn put-state-handler [path]
  (PUT path []
    :query-params [target :- (st/spec #{:ledbeere :ledball1} #_(set (keys @pxlz))  ;;TODO
                                      {:type :keyword
                                       :description "<b>target</b> whose state should be updated"})
                   looped :- (st/spec boolean? {:description "In case <b>pxlzstate</b> is shorter than the length of <b>target</b>, should the <b>pxlzstate</b> be <b>looped</b>?"})]
    :body [pxlzstate (st/spec ::pxlzstate {:description "The new pxlzstate"})]
    :return boolean?
    (let [pxlzstate-looped (if looped
                               (seg/looped pxlzstate)
                               pxlzstate)]
         (set-rgbPxlz! pxlzstate-looped [target]))
    (ok true)))
