(defproject clj-docset "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/java.jdbc "RELEASE"]
                 [org.xerial/sqlite-jdbc "3.19.3"]
                 [selmer "1.10.8"]
                 ]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]]}})
