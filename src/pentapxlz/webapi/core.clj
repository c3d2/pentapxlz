(ns pentapxlz.webapi.core
  (:require [aleph.http :as http]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [clojure.core.async :as a :refer [chan >! <!]]
            [ring.middleware.params :as params]
            [compojure.core :as compojure :refer [GET]]
            [compojure.route :as route]
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
  "curl 'http://localhost:8080/state?target=ledball1&timeout=100&ansicolor=true'"
  [{:keys [params]}]
  (let [target (keyword (get params "target" "ledbeere"))
        timeout (max 100 (Integer/parseInt (get params "timeout" "1000")))
        ansicolor (get params "ansicolor" nil)
        body (chan)]

    (a/go-loop []
      (let [pxlzState (get-in @pxlz [target :pxlzState])]
           (if ansicolor
               (>! body (str (char 27) "[2J"
                             (join " " (apply vector (map rgb->ansi pxlzState)))
                             (join " " (for [_ (range 80)] "")) "\n"))
               (>! body (str (apply vector pxlzState) "\n")))
           (<! (a/timeout timeout))
           (recur)))

    {:status 200
     :headers {"content-type" "text/event-stream"}
     :body (s/->source body)}))

(def handler
  (params/wrap-params
    (compojure/routes
      (GET "/state" [] streaming-state-handler)
      (route/not-found "No such page."))))

(defonce server (http/start-server handler {:host "0.0.0.0"
                                            :port 8080}))
