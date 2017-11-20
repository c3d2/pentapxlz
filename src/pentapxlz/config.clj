(ns pentapxlz.config
  (:require [aero.core :refer [read-config]])
  (:import (java.io File)))

(defn read-configs-when-existing [files]
  (apply merge (for [^File file files]
                    (if (.exists (clojure.java.io/as-file file))
                        (read-config file)))))

(def homedir (System/getProperty "user.home"))

(def config (read-configs-when-existing ["config.edn"
                                         "/etc/pentapxlz/config.edn"
                                         (str homedir "/.pentapxlz/config.edn")]))
