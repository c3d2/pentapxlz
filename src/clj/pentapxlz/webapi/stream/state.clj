(ns pentapxlz.webapi.stream.state
  (:require [compojure.api.sweet :refer [GET]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [manifold.stream :refer [->source]]
            [clojure.core.async :as a :refer [chan >! <! close! timeout go go-loop]]
            [clojure.string :refer [join]]
            [pentapxlz.colors :refer [colormapX+colormapY->colorX->colorY]]
            [pentapxlz.state :as state]
            [pentapxlz.colors :as c]))

(defn rgb->ansi [rgb]
  (let [ansicolor (+ (if-not (= 0 (nth rgb 0)) 1 0)
                     (if-not (= 0 (nth rgb 1)) 2 0)
                     (if-not (= 0 (nth rgb 2)) 4 0))]
       (str (char 27) "[48;5;" ansicolor "m"
            ansicolor
            (char 27) "[0m")))

(defn get-frame-state [target]
  @(state/resolve-atom (keyword "state" (name target))))

(defn streaming-frame-state-handler [path]
  (GET path []
    :summary "Request the current frames of a [target]"
    :description (str "Commandline usage:" "<br/>"
                      "<b>$</b> curl 'http://localhost:8080/api/frames?target=ledball1&streamevery=100&ansicolor=true'" "<br/>"
                      "<br/>"
                      "On Repl you can use:" "<br/>"
                      "<b>&gt;</b> (get-in @pentapxlz.pxlz-state/pxlz [:ledbeere :frames])")
    :query-params [{target :- (st/spec #{:ledbeere :ledball1} #_(set (keys @pxlz))  ;;TODO
                                       {:type :keyword
                                        :description "<b>target</b> whose state should be returned"})
                           (name (first (state/ls)))}
                   {streamevery :- (st/spec int? {:description "Resend the result <b>streamevery</b> ms (when set and > 0)"}) 0}
                   {ansicolor :- (st/spec boolean? {:description "Encode as ansi-escape-sequences (for usage on commandline)"}) false}
                   {rgbcolor :- (st/spec boolean? {:description "Return rgb, not hostcolors (implied by <b>ansicolor</b>)"}) true}
                   {reversed :- (st/spec boolean? {:description "Allows to reverse the order"}) false}
                   {separator :- (st/spec string? {:description "For usage with <b>ansicolor</b>: Separator between pixels"}) ""}
                   {padding :- (st/spec int? {:description "For usage with <b>ansicolor</b>: Add <b>padding</b> spaces at end of line with correct backgroundcolor"}) 200}]

    (let [streamevery_min 100
          reverseFn (if reversed reverse identity)
          body (chan)]
      (go-loop []
        (let [frame (reverseFn @(state/resolve-atom (keyword "state" (str (name target) "-frame"))))]
          (if ansicolor
            (>! body (str (char 27) "[2J"
                          (join separator (apply vector (->> frame
                                                             (map c/->rgb)
                                                             (map rgb->ansi))))
                          (join " " (for [_ (range padding)] "")) "\n"))
            (>! body (str (into [] frame) "\n")))
          (<! (timeout (max streamevery streamevery_min))))
        (if (> streamevery 0)
          (recur)
          (close! body)))

      {:status 200
       :headers {"content-type" "text/event-stream"}
       :body (->source body)})))
