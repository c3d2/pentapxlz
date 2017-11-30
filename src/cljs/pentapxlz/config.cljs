(ns pentapxlz.config)

(def debug?
  ^boolean goog.DEBUG)

(def init-states {:state/ledball1-frame {:type :generator/spiral
                                         :geometry [48 59 69 73 75 71 65 56 46 36 26 20]}})

(def processes {:renderer/quil-frame
                {:type      :renderer/quil-frame
                 :framerate 20
                 :state     :state/ledball1-frame}
                :animator/shift-ball
                {:type      :animator/shift
                 :state     :state/ledball1-frame
                 :offset    1
                 :framerate 1}})

(def auto-start [:renderer/quil-frame #_:animator/shift-ball])

(def config (atom {:states {:state/ledball1-frame {:layout {:nrPxlz 255}}}}))

