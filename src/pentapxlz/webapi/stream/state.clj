(ns pentapxlz.webapi.stream.state
  (:require [manifold.stream :refer [->source]]
            [clojure.core.async :as a :refer [chan >! <! close! timeout go go-loop]]
            [pentapxlz.pxlz-state :refer [pxlz]]
            [clojure.string :refer [join]]))

(defn rgb->ansi [rgb]
  (let [ansicolor (+ (if-not (= 0 (nth rgb 0)) 1 0)
                     (if-not (= 0 (nth rgb 1)) 2 0)
                     (if-not (= 0 (nth rgb 2)) 4 0))]
       (str (char 27) "[48;5;" ansicolor "m"
            ansicolor
            (char 27) "[0m")))

(defn streaming-state-handler
  "curl 'http://localhost:8080/state?target=ledball1&streamevery=100&ansicolor=true'"
  [{:keys [params]}]
  (let [target (keyword (get params :target "ledbeere"))
        streamevery (Integer/parseInt (get params :streamevery "0"))
        stream_min 100
        ansicolor (= (get params :ansicolor) "true")
        rgbcolor (= (get params :rgbcolor (get params :ansicolor)) "true")
        separator (get params :separator "")
        padding (Integer/parseInt (get params :padding "200"))
        reverseFn (if (= (get params :reversed) "true")
                       reverse
                       identity)
        body (chan)]

    (go-loop []
      (let [pxlzState-hostcolors (-> (get-in @pxlz [target :pxlzState])
                                     reverseFn)
            pxlzState (if rgbcolor
                          (map #(apply (get-in @pxlz [target :colors-inverse]) %) pxlzState-hostcolors)
                          pxlzState-hostcolors)]
           (if ansicolor
               (>! body (str (char 27) "[2J"
                             (join separator (apply vector (map rgb->ansi pxlzState)))
                             (join " " (for [_ (range padding)] "")) "\n"))
               (>! body (str (apply vector pxlzState) "\n")))
           (<! (timeout (max streamevery stream_min))))
      (if (> streamevery 0)
          (recur)
          (close! body)))

    {:status 200
     :headers {"content-type" "text/event-stream"}
     :body (->source body)}))
