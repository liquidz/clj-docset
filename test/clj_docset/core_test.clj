(ns clj-docset.core-test
  (:require [clojure.test :refer :all]
            [clj-docset.core :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            ))

(deftest docset-test
  (is (= {:base-dir "." :id "a" :name "b" :family "c"}
         (docset :id "a" :name "b" :family "c")))
  (is (= {:base-dir "/" :id "a" :name "b" :family "c"}
         (docset :base-dir "/" :id "a" :name "b" :family "c"))))
