(ns pentapxlz.processes.atom-registry)

(defonce atoms (atom {}))

(defn resolve-atom [kw]
  (if-let [a (@atoms kw)]
    a
    (let [a (atom [])]
      (swap! atoms assoc kw a)
      a)))