(ns pentapxlz.colors
  #?(:clj
     (:import (clojure.lang IPersistentVector Keyword)
              (java.util Map))))

(def cmap
  {:black   [0x00 0x00 0x00]
   :navy    [0x00 0x00 0x80]
   :blue    [0x00 0x00 0xff]
   :green   [0x00 0x80 0x00]
   :teal    [0x00 0x80 0x80]
   :lime    [0x00 0xff 0x00]
   :aqua    [0x00 0xff 0xff]
   :maroon  [0x80 0x00 0x00]
   :purple  [0x80 0x00 0x80]
   :olive   [0x80 0x80 0x00]
   :gray    [0x80 0x80 0x80]
   :silver  [0xc0 0x0c 0xc0]
   :red     [0xff 0x00 0x00]
   :fuchsia [0xff 0x00 0xff]
   :yellow  [0xff 0xff 0x00]
   :white   [0xff 0xff 0xff]})

(defn colormapX+colormapY->colorX->colorY [colormapX colormapY]
  (let [colorDictY (zipmap colormapY (range 3))
        idxs (into [] (map colorDictY colormapX))]
       (fn [colorX] (mapv colorX idxs))))

(defn normalize-hysteresis
  "Normalize all items in case sum>maximum.
   Maximum should be > 1"
  [items maximum]
  (let [maximumsum (* 3 maximum)
        sum (apply + items)]
       (if (<= sum maximumsum)
           items
           (map #(-> %
                     (* (min sum maximumsum))
                     (/ sum)
                     int)
                items))))

(defprotocol ColorPixel
  (->rgb [this]))

(extend-protocol ColorPixel
  #?(:cljs IVector
     :clj  IPersistentVector)
  (->rgb [this] this)
  #?(:cljs IMap
     :clj  Map)
  (->rgb [this] [(:r this) (:g this) (:b this)])
  Keyword
  (->rgb [this] (cmap this))
  #?(:cljs js/Number
     :clj Long)
  (->rgb [this]
    [(quot this (* 256 256))
     (mod (quot this 256) 256)
     (mod this 256)]))
