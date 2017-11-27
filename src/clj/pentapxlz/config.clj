(ns pentapxlz.config
  (:require [aero.core :refer [read-config]])
  (:import (java.io File)))

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

(defn- load-config []
  (read-configs-when-existing ["config.edn"
                               "/etc/pentapxlz/config.edn"
                               (str homedir "/.pentapxlz/config.edn")]))

(defonce config (atom (load-config)))

(defn reload-config! []
  (let [c (load-config)]
    (reset! config c)))
