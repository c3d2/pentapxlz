(ns pentapxlz.webapi.set-state
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [PUT]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [pentapxlz.mappings.segments :as seg]
            [pentapxlz.state :as state :refer [set-simple!]]))

(s/def ::color (s/int-in 0 256))
(s/def ::pixel (s/or :color-tuple (s/tuple ::color ::color ::color)
                     :color-keyword (st/spec #{:red :green :blue :yellow} {:type :keyword})))  ;; TODO
(s/def ::frame (s/coll-of ::pixel))

(s/def ::target (st/spec #{:state/ledball1-frame :state/ledbeere-frame} {:type :keyword}))  ;; TODO

(s/def ::segment (s/cat :nr int? :pixel ::pixel))
(s/def ::segments (s/coll-of ::segment))

(s/def ::target+frame (s/keys :req-un [::target ::frame]))

(s/fdef set-frame
  :args (s/cat :params ::target+frame))

(defn
  ^{:methods {:post {:parameters {;:query-params ::target
                                  :body-params ::target+frame}}}}
  set-frame
  "Set the current frame of a [target]"
  [{:keys [target frame]}]
  {:return :ok
   :result (set-simple! target frame)})

(defn put-state-handler [path]
  (PUT path []
    :summary "Set the current frame of a [target]"
    :description (str "For 1 black and 1 white pixel use:<br/>"
                      "[[255 255 255][0 0 0]]<br/>"
                      "<br/>"
                      "On Repl you can use:" "<br/>"
                      "<b>&gt;</b> (set-rgbPxlz! (looped [black white]) [:ledbeere])")
    :query-params [target :- (st/spec #{:ledbeere :ledball1} #_(set (keys @pxlz))  ;;TODO
                                      {:type :keyword
                                       :description "<b>target</b> whose state should be updated"})
                   looped :- (st/spec boolean? {:description "In case <b>frame</b> is shorter than the length of <b>target</b>, should the <b>frame</b> be <b>looped</b>?"})]
    :body [frame (st/spec ::frame {:description "The new frame"})]
    :return boolean?
    (let [frame-looped (if looped
                               (seg/looped frame)
                               frame)]
      (state/set! target frame-looped))
    (ok true)))

(defn put-state-segments-handler [path]
  (PUT path []
    :summary "Set the current frame of a [target] using segments"
    :description (str "For 1 blue followed by 10 yellow use:<br/>"
                      "<b>[[1 [0 0 255]] [10 [255 255 0]]]</b><br/>"
                      "<br/>"
                      "On Repl you can use:" "<br/>"
                      "<b>&gt;</b> (set-rgbPxlz! (looped (segments [[1 blue] [10 yellow]])) [:ledbeere])")
    :query-params [target :- (st/spec #{:ledbeere :ledball1} #_(set (keys @pxlz)) ;;TODO
                                      {:type        :keyword
                                       :description "<b>target</b> whose state should be updated"})
                   looped :- (st/spec boolean? {:description "In case <b>frame</b> is shorter than the length of <b>target</b>, should the <b>frame</b> be <b>looped</b>?"})]
    :body [nr+colors (st/spec ::segments {:description "A vector of segments (vector of nr and color)"})]
    :return boolean?
    (let [nr+colors-looped (if looped
                             (seg/looped nr+colors)
                             nr+colors)]
      (state/set! target (seg/segments nr+colors-looped)))
    (ok true)))
