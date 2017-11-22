(ns pentapxlz.renderer.quil
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [pentapxlz.processes.resolve :as r]
            [pentapxlz.colors :as c]))

(def window-size 1000)
(def base-size 25)
(def pixel-per-row 50)

(defn grid-koords->abs-koords
  [{:keys [grid-x grid-y] :as pixel}]
  (into pixel
        {:abs-x (* grid-x base-size)
         :abs-y (* grid-y base-size)}))

(defn index->grid-koords [{:keys [index] :as pixel}]
  (into pixel
        {:grid-x (mod index pixel-per-row)
         :grid-y (quot index pixel-per-row)}))

(defn draw-pixel [{:keys [abs-x abs-y pixel]}]
  (apply q/fill (c/->rgb pixel))
  (q/rect abs-x abs-y base-size base-size))

(defn pixels->pixel-maps [pixels]
  (map-indexed (fn [i p]
                 {:index i
                  :pixel p})
               pixels))

(defn draw-frame [frame]
  (q/background 200)
  (doseq [pixel (pixels->pixel-maps frame)]
    (draw-pixel (grid-koords->abs-koords
                  (index->grid-koords pixel))))
  (q/text (str "FPS: " (q/current-frame-rate)) (- window-size 100) 20))

(defn frame-renderer-draw-fn [frame]
  (draw-frame @frame))

;nothing todo as the frame-changes will be done by someone else
(defn frame-renderer-update-fn [frame-atom]
  frame-atom)

(defn opts->setup-fn [{:keys [framerate frame-atom]}]
  (fn []
    (q/frame-rate framerate)
    frame-atom))

(defn- start-quil-frame-renderer [{:keys [framerate frame-atom] :as opts}]
  (let [sketch
        (q/sketch
          :title "Pixels!"
          :host "quil-view"
          :settings #(q/smooth 2)
          :setup (opts->setup-fn opts)
          :update frame-renderer-update-fn
          :draw frame-renderer-draw-fn
          :middleware [m/fun-mode]
          :size [window-size window-size])]
    {:sketch sketch}))

(defn quil-frame-renderer [opts]
  {:opts opts
   :start-fn (fn [this]
               (merge this (start-quil-frame-renderer opts)))
   :stop-fn (fn [this]
              (.exit (:sketch this))
              (dissoc this :sketch))})

(defmethod r/resolve-process
  :renderer/quil [opts] (quil-frame-renderer opts))