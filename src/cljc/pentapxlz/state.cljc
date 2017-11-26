(ns pentapxlz.state
  (:require [pentapxlz.colors :as c]
            [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]))

(defonce atoms (atom {}))

(defn resolve-atom [kw]
  (if-let [a (@atoms kw)]
    a
    (let [a (atom [])]
      (swap! atoms assoc kw a)
      a)))

(defn init-states! [init-state-map]
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

(defn ls []
  (keys @atom))

(defn set! [kw new-state]
  (let [namespaced-kw (if (namespace kw)
                        kw
                        (keyword "state" (name kw)))]
    (reset! (resolve-atom namespaced-kw) new-state)))
