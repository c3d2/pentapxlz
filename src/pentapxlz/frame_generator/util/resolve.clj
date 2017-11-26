(ns pentapxlz.frame-generator.util.resolve)

(defmulti resolve-generator
          "Resolves a given generator map.
           Takes a configuration map and (including the :type) and returns the generated frame."
          :type)