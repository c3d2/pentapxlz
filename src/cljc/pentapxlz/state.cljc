(ns pentapxlz.state
  (:require [pentapxlz.colors :as c]
            [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            [pentapxlz.config :refer [config]]))

(defonce atoms (atom {}))

(defn resolve-atom [kw]
  (if-let [a (@atoms kw)]
    a
    (let [a (atom [])]
      (swap! atoms assoc kw a)
      a)))

(defn ls []
  (keys @atoms))

(defn set! [kw new-state]
  (let [namespaced-kw (if (namespace kw)  ;; TODO shouldn't this belong to resolve-atom?
                        kw
                        (keyword "state" (name kw)))]
    (reset! (resolve-atom namespaced-kw) new-state)))

(defn set-simple!
  "like set! but ensures rgb-tuples and correct length (when needed, black is padded)"
  [kw new-state]
  (let [nrPxlz (get-in @config [:states kw :layout :nrPxlz])
        new-save-state (->> (take nrPxlz (concat (take nrPxlz new-state)
                                                 (resolve-generator {:type :generator/constant :color :black :length nrPxlz})))
                            (map pentapxlz.colors/->rgb))]
       (pentapxlz.state/set! kw new-save-state)))

(defn init-states! [init-state-map]
  (doseq [k (keys init-state-map)]
    (let [a (resolve-atom k)
          v (init-state-map k)]
      (->>
        (cond (map? v) (resolve-generator v)
              (vector? v) v
              :else [])
        (set-simple! k)))))

;; still needed for *-animation
#_(defn init-states! [init-state-map]
  (doseq [k (keys init-state-map)]
    (let [a (resolve-atom k)
          v (init-state-map k)]
      (->>
        (cond (map? v) (resolve-generator v)
              (vector? v) v
              :else [])
        (map c/->rgb)
        (into [])
        (reset! a)))))
