(ns pentapxlz.webapi.set-state
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [PUT]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [pentapxlz.frame-generator.segments :as segm]
            [pentapxlz.state :as state :refer [set-simple!]]))

(s/def ::color (s/int-in 0 256))
(s/def ::pixel (s/or :color-tuple (s/tuple ::color ::color ::color)
                     #_#_:color-keyword (st/spec #{:red :green :blue :yellow} {:type :keyword})))  ;; TODO
(s/def ::frame (s/coll-of ::pixel))

(s/def ::target (st/spec #{:state/ledball1-frame :state/ledbeere-frame} {:type :keyword}))  ;; TODO

(s/def ::segment (s/cat :nr pos-int? :pixel ::pixel))
(s/def ::segments (s/coll-of ::segment))

(s/def ::target+frame (s/keys :req-un [::target ::frame]))
(s/def ::target+segments (s/keys :req-un [::target ::segments]))

(s/fdef set-frame
  :args (s/cat :params ::target+frame))

(defn
  ^{:methods {:post {:parameters {:body-params ::target+frame}}}}
  set-frame
  "Set the current frame of a target"
  [{:keys [target frame]}]
  (let [r (set-simple! target frame)]
       {:return :ok
        :result (str "set frame of length: " (count r))}))

(s/fdef set-frame-segments
  :args (s/cat :params ::target+segments))

(defn
  ^{:methods {:post {:parameters {:body-params ::target+segments}}}}
  set-frame-segments
  "Set the current frame via segments"
  [{:keys [target segments]}]
  (let [r (set-simple! target (segm/segments segments))]
       {:return :ok
        :result (str "set frame of length: " (count r))}))
