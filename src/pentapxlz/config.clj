(ns pentapxlz.config
  (:require [aero.core :refer [read-config]]))

(defn read-configs-when-existing [files]
  (apply merge (for [file files]
                    (if (.exists (clojure.java.io/as-file file))
                        (read-config file)))))

(def homedir (System/getProperty "user.home"))

(def config (read-configs-when-existing ["config.edn"
                                         "/etc/pentapxlz/config.edn"
                                         (str homedir "/.pentapxlz/config.edn")]))
