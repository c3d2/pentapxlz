(ns pentapxlz.logic
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer :all]
            [clojure.core.logic.fd :as fd]))

(defn- neighbaro [[a b]]
  (fresh [c]
    (fd/in c (fd/interval 0 1))
    (fd/- b a c)))

(defn- color-seq [start end length smooth]
  (if (< end start)
    (reverse (color-seq end start length smooth))
    (first
      (let [lseq (repeatedly length lvar)
            pairs (partition 2 1 lseq)]
        (run 1 [q]
             (== q lseq)
             (== end (last lseq))
             (everyg #(fd/in % (fd/interval start end)) lseq)
             (everyg #(== (nth lseq (quot (* % length) smooth))
                          (+ (quot (* % (- end start)) smooth)
                             start)) (range smooth))
             (everyg (fn [[a b]] (fd/<= a b)) pairs)
             (everyg neighbaro pairs))))))

(defn- pixel-seq [[r1 g1 b1] [r2 g2 b2] length smooth]
  (into [] (map vector
                (color-seq r1 r2 length smooth)
                (color-seq g1 g2 length smooth)
                (color-seq b1 b2 length smooth))))

(defn smooth-transition [start-frame end-frame length smooth]
  (into []
        (apply (partial map vector)
               (into [] (map #(pixel-seq %1 %2 length smooth) start-frame end-frame)))))
