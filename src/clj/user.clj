(ns user
  (:require [pentapxlz.config :refer [config]]
            [pentapxlz.core :refer [-main]]
            [pentapxlz.state :refer [set-simple!]]
            [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            [pentapxlz.frame-generator.looped :refer [looped]]
            [pentapxlz.frame-generator.segments :refer [segments]]))
