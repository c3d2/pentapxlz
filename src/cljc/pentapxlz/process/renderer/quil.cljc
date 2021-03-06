(ns pentapxlz.process.renderer.quil
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [pentapxlz.process.util.resolve :as r]
            [pentapxlz.colors :as c]
            [pentapxlz.state :as state]))

(def window-size 1000)
(def base-size 25)
(def pixel-per-row 50)

(defn- grid-koords->abs-koords
  [{:keys [grid-x grid-y] :as pixel}]
  (into pixel
        {:abs-x (* grid-x base-size)
         :abs-y (* grid-y base-size)}))

(defn- index->grid-koords [{:keys [index] :as pixel}]
  (into pixel
        {:grid-x (mod index pixel-per-row)
         :grid-y (quot index pixel-per-row)}))

(defn- draw-pixel [{:keys [abs-x abs-y pixel]}]
  (apply q/fill pixel)
  (q/rect abs-x abs-y base-size base-size))

(defn- pixels->pixel-maps [pixels]
  (map-indexed (fn [i p]
                 {:index i
                  :pixel p})
               pixels))

(defn- draw-frame [frame]
  (q/background 200)
  (doseq [pixel (pixels->pixel-maps frame)]
    (draw-pixel (grid-koords->abs-koords
                  (index->grid-koords pixel))))
  (q/text (str "FPS: " (q/current-frame-rate)) (- window-size 100) 20))

(defn- frame-renderer-draw-fn [frame]
  (draw-frame @frame))

;nothing todo as the frame-changes will be done by someone else
(defn- frame-renderer-update-fn [frame-atom]
  frame-atom)

(defn- opts->frame-setup-fn [{:keys [framerate state]}]
  (fn []
    (q/frame-rate framerate)
    (state/resolve-atom state)))

(defn- start-quil-frame-renderer [{:keys [framerate state] :as opts}]
  (let [sketch
        (q/sketch
          :title "Pixels!"
          :host "quil-view"
          :settings #(q/smooth 2)
          :setup (opts->frame-setup-fn opts)
          :update frame-renderer-update-fn
          :draw frame-renderer-draw-fn
          :middleware [m/fun-mode]
          :size [window-size window-size])]
    {:sketch sketch}))

(defn quil-frame-renderer [opts]
  (into opts
    {:start-fn (fn [this]
                 (merge this (start-quil-frame-renderer opts)))
     :stop-fn (fn [this]
                (.exit (:sketch this))
                (dissoc this :sketch))}))

(defmethod r/resolve-process
  :renderer/quil-frame [opts] (quil-frame-renderer opts))

;---------------- quil animation renderer -----------------------

(defn- opts->animation-setup-fn [{:keys [framerate state]}]
  (fn []
    (q/frame-rate framerate)
    {:current-frame 0
     :animation (state/resolve-atom state)}))

(defn- animation-renderer-update-fn [current]
  (update current :current-frame #(mod (inc %) (max (count @(:animation current)) 1))))

(defn- animation-renderer-draw-fn [{:keys [current-frame animation]}]
  (draw-frame (get @animation current-frame [])))

(defn- start-quil-animation-renderer [{:keys [framerate state] :as opts}]
  (let [sketch
        (q/sketch
          :title "Pixels!"
          :host "quil-view"
          :settings #(q/smooth 2)
          :setup (opts->animation-setup-fn opts)
          :update animation-renderer-update-fn
          :draw animation-renderer-draw-fn
          :middleware [m/fun-mode]
          :size [window-size window-size])]
    {:sketch sketch}))

(defn quil-animation-renderer [opts]
  (into opts
        {:start-fn (fn [this]
                     (merge this (start-quil-animation-renderer opts)))
         :stop-fn  (fn [this]
                     (.exit (:sketch this))
                     (dissoc this :sketch))}))

(defmethod r/resolve-process
  :renderer/quil-animation [opts] (quil-animation-renderer opts))