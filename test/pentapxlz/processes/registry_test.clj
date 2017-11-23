(ns pentapxlz.processes.registry-test
  (:require [clojure.test :refer :all]
            [pentapxlz.processes.registry :refer :all])
  (:import (clojure.lang ExceptionInfo)))

(def test-process
  {:start-fn #(update % :state inc)
   :state 0
   :stop-fn #(update % :state dec)})

(deftest registry-lifecycle
  (with-redefs [pentapxlz.processes.registry/registry (atom {})]
    (testing "throws errors if not registered"
      (is (thrown? ExceptionInfo (start! ::test)) "Cannot start unregistered process")
      (is (thrown? ExceptionInfo (stop! ::test)) "Cannot stop unregistered process"))
    (testing "register and starting"
      (register ::test test-process)
      (is (zero? (get-in @registry [::test :state])) "Registering adds process-map to registry")
      (start! ::test)
      (is (= 1 (get-in @registry [::test :state])) "Starting calls start-fn and replaced process-map by result"))
    (testing "reregister not possible (only with other keyword)"
      (is (thrown? ExceptionInfo (register ::test test-process)) "Cannot reregister under same key")
      (register ::test2 test-process)
      (is (@registry ::test2) "Register under other keyword is possible"))
    (testing "restart not possible by start"
      (is (thrown? ExceptionInfo (start! ::test)) "Cannot start started process"))
    (testing "stopping, starting"
      (stop! ::test)
      (is (zero? (get-in @registry [::test :state])) "Stopping calls stop-fn and replaces process-map by result")
      (start! ::test)
      (is (= 1 (get-in @registry [::test :state])) "Start after stop works as expected"))
    (testing "unregister"
      (unregister ::test)
      (is (not (@registry ::test)) "Unregister removes the process")
      (is (thrown? ExceptionInfo (unregister ::test3)) "Cannot unregister not registered process"))))



