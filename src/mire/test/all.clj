(ns mire.test.all
  (:use [mire.test commands])
  (:use [mire.test rooms])
  (:use [mire.test util])
  (:use [clojure.contrib test-is]))

(run-tests 'mire.test.commands)
(run-tests 'mire.test.rooms)
(run-tests 'mire.test.util)