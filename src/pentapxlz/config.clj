(ns pentapxlz.config
  (:require [aero.core :refer [read-config]]
            [pentapxlz.processes.atom-registry :as ar])
  (:import (java.io File)))

(defmethod aero.core/reader 'atom
  [_ _ value]
  (ar/resolve-atom value))

(defn read-configs-when-existing [files]
  (apply merge (for [^File file files]
                    (if (.exists (clojure.java.io/as-file file))
                        (read-config file)))))

(def homedir (System/getProperty "user.home"))

(defn reload-config []
  (read-configs-when-existing ["config.edn"
                               "/etc/pentapxlz/config.edn"
                               (str homedir "/.pentapxlz/config.edn")]))

(def config (reload-config))
