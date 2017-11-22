(ns pentapxlz.colors
  (:import (clojure.lang IPersistentVector Keyword)
           (java.util Map)))

(def black   [0x00 0x00 0x00])
(def navy    [0x00 0x00 0x80])
(def blue    [0x00 0x00 0xff])
(def green   [0x00 0x80 0x00])
(def teal    [0x00 0x80 0x80])
(def lime    [0x00 0xff 0x00])
(def aqua    [0x00 0xff 0xff])
(def maroon  [0x80 0x00 0x00])
(def purple  [0x80 0x00 0x80])
(def olive   [0x80 0x80 0x00])
(def gray    [0x80 0x80 0x80])
(def silver  [0xc0 0x0c 0xc0])
(def red     [0xff 0x00 0x00])
(def fuchsia [0xff 0x00 0xff])
(def yellow  [0xff 0xff 0x00])
(def white   [0xff 0xff 0xff])

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
  IPersistentVector
  (->rgb [this] this)
  Map
  (->rgb [this] [(:r this) (:g this) (:b this)])
  Keyword
  (->rgb [this] (cmap this))
  Long
  (->rgb [this]
    [(quot this (* 256 256))
     (mod (quot this 256) 256)
     (mod this 256)]))
