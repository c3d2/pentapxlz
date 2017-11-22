(ns pentapxlz.processes.resolve)

(defmulti resolve-process
  "Resolves a given process map (renderer or animator)"
  :type)