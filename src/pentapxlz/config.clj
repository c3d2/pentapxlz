(ns pentapxlz.config
  (:require [aero.core :refer [read-config]]
            [pentapxlz.state :as state])
  (:import (java.io File)))

(defmethod aero.core/reader 'atom
  [_ _ value]
  (state/resolve-atom value))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (when (some identity vs)
      (reduce #(rec-merge %1 %2) v vs))))

(defn read-configs-when-existing [files]
  (apply deep-merge
         (for [^File file files]
              (if (.exists (clojure.java.io/as-file file))
                  (read-config file)
                  {}))))

(def homedir (System/getProperty "user.home"))

(defn reload-config []
  (read-configs-when-existing ["config.edn"
                               "/etc/pentapxlz/config.edn"
                               (str homedir "/.pentapxlz/config.edn")]))

(def config (reload-config))
