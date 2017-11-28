(ns user
  (:require [pentapxlz.config :refer [config]]
            [pentapxlz.core :refer [-main]]
            [pentapxlz.state :refer [set-simple!]]
            [pentapxlz.frame-generator.util.resolve :refer [resolve-generator]]
            [pentapxlz.frame-generator.constant]
            [pentapxlz.frame-generator.segments :refer [segments]]
            [pentapxlz.frame-generator.spiral]
            [pentapxlz.frame-generator.cycled]
            [pentapxlz.frame-generator.stretched :refer [stretched]]))
