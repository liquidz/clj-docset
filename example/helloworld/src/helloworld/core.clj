(ns helloworld.core
  (:require [clj-docset.core :as d]))

(def records
  [(d/record :name "foo" :body "bar")
   (d/record :name "bar" :body "baz")])

(def docset
  (d/docset :id "foo" :name "bar" :family "baz" :records records))

(defn -main []
  (d/generate docset))
