(ns pentapxlz.mappings.figlet
 #_ (:require [clj-figlet.core :refer [load-flf render]]
            [pentapxlz.pxlz-state :refer [pxlz set-rgbPxlz!]]
            [pentapxlz.mappings.segments :refer [segments #_looped zipvector]]
            [pentapxlz.colors :refer :all]
            [clojure.pprint :refer [pprint]]))
#_#_#_#_#_#_
(def flf (load-flf "resources/figlet/roman.flf"))

(defn cs:rgbs+default->c->rgb
  "Takes a map from characters (string) to rgb values + a default rgb
   Returns a function from character to rgb value (when c is one of cs)"
  [cs:rgbs defaultRgb]
  (fn [c] (or (->> (for [[cs rgb] cs:rgbs]
                     (if (re-find (re-pattern (str c)) cs)
                         rgb))
                   (filter identity)
                   first)
              defaultRgb)))

(defn ascii->rgb [ascii cs:rgbs rgb-else]
  (map #((cs:rgbs+default->c->rgb cs:rgbs rgb-else) %) ascii))

(defn ascii2d->rgb [ascii2d cs:rgbs rgb-else]
  (map #(ascii->rgb % cs:rgbs rgb-else) ascii2d))

(defn render-text->rgb2d [text cs:rgbs rgb-else]
  (-> (render flf text)
      (ascii2d->rgb cs:rgbs rgb-else)))

(defn set-example-figlet-ccc! [degrees skiplines]
  (let [target :ledball1
        rgb2d (render-text->rgb2d "<<<" {"Pb" red "dY" yellow " " [0 8 0]} [0 1 1])
        ;height (count rgb2d)
        width (count (first rgb2d))
        ;(println (str width "x"height))
        screen (get-in @pxlz [target :geometry :spiral])]
    (set-rgbPxlz! (concat (segments [[(reduce + (take skiplines screen)) [0 0 255]]])
                          (apply concat (for [[row-width rgbLine] (zipvector (drop skiplines screen) rgb2d)]
                                             (let [padding (- row-width width)
                                                   padding-right (quot (* padding degrees) 360)]
                                                  (concat (segments [[(- padding padding-right) [0 128 0]]])
                                                          rgbLine
                                                          (segments [[padding-right [0 128 0]]]))))))
                  [:ledball1])))
