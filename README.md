# clj-docset

Docset generator for clojure.

## Usage

```clojure
(ns foo.core
  (:require [clj-docset.core :as d]))

(def records
  [(d/record :name "foo" :body "bar")
   (d/record :name "bar" :body "baz")])

(def docset
  (d/docset :id "foo" :name "bar" :family "baz" :records records))

(d/generate docset)
```


## License

Copyright © 2017 Masashi Iizuka

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
