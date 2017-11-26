(ns pentapxlz.process.util.resolve)

(defmulti resolve-process
  "Resolves a given process map (renderer or animator).
   Takes a configuration map and (including the :type) and returns the fully populated process map."
  :type)