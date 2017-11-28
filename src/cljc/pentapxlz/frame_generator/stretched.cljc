(ns pentapxlz.frame-generator.stretched
  (:require [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            [pentapxlz.frame-generator.segments :refer [segments]]
            [pentapxlz.colors :refer [->rgb]]))

(defn- gcd 
  "greatest common divisor"
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))
 
(defn- lcm
  "least common multiple"
  [a b]
  (/ (* a b)
     (gcd a b)))

(defn- avg
  "integer average"
  [& args]
  (quot (apply + args)
        (count args)))

(def blenders {:colormix #(->> (map ->rgb %)
                               (apply mapv avg))
               :majority :notImplemented
               :consecutiveMajority :notImplemented})

(defn stretched
  "returns a vector of the requested length and the items of s"
 ([length s]
  (stretched length s nil))
 ([length s blenderFn]
  (let [blenderFnDefault (:colormix blenders)
        count_s (count s)
        lcm* (lcm length count_s)
        lcm-multiplier (/ lcm* count_s)
        lcm-divisor (/ lcm* length)
        lcm-stretched (apply concat (map #(segments [[lcm-multiplier %]]) s))
        stretched-tuples (partition lcm-divisor lcm-stretched)]
       (map (or blenderFn blenderFnDefault) stretched-tuples))))

(defn stretched-generator
  "stretches the result of the generator specified by the first item of chain"
  [{:keys [chain length blenderFn] :as args}]
  (as-> [:type (first chain) :chain (rest chain)] x
        (apply assoc args x)
        (resolve-generator x)
        (stretched length x blenderFn)))

(defmethod resolve-generator
  :generator/stretched [opts] (stretched-generator opts))
