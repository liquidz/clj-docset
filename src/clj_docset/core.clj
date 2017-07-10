(ns clj-docset.core
  (:require
    [clojure.java.io    :as io]
    [clojure.java.jdbc  :as jdbc]
    [clojure.string     :as str]
    [selmer.parser      :as selmer]
    [clojure.spec.alpha :as s]))

(s/def :record/name string?)
(s/def :record/type
  #{"Annotation" "Attribute" "Binding" "Builtin" "Callback" "Category"
    "Class" "Command" "Component" "Constant" "Constructor" "Define"
    "Delegate" "Diagram" "Directive" "Element" "Entry" "Enum" "Environment"
    "Error" "Event" "Exception" "Extension" "Field" "File" "Filter"
    "Framework" "Function" "Global" "Guide" "Hook" "Instance" "Instruction"
    "Interface" "Keyword" "Library" "Literal" "Macro" "Method" "Mixin"
    "Modifier" "Module" "Namespace" "Notation" "Object" "Operator" "Option"
    "Package" "Parameter" "Plugin" "Procedure" "Property" "Protocol"
    "Provider" "Provisioner" "Query" "Record" "Resource" "Sample" "Section"
    "Service" "Setting" "Shortcut" "Statement" "Struct" "Style" "Subroutine"
    "Tag" "Test" "Trait" "Type" "Union" "Value" "Variable" "Word"})
(s/def :record/prefix string?)
(s/def :record/body string?)
(s/def :record/ext string?)
(s/def ::record (s/keys :req-un [:record/name :record/type :record/prefix :record/ext :record/body]))

(s/def :docset/id string?)
(s/def :docset/name string?)
(s/def :docset/family string?)
(s/def :docset/base-dir string?)
(s/def :docset/records (s/+ ::record))
(s/def ::docset (s/keys :req-un [:docset/id :docset/name :docset/family]
                        :opt-un [:docset/base-dir :docset/records]))

(defn docset
  "Generate docset map.
  ex. (docset :FIXME)
  "
  [& {:as m}]
  {:post [(s/valid? ::docset %)]}
  (merge {:base-dir "."} m))

(defn record
  "Generate record map.
  `:name` and `:body` keys are required.

  ex. (record :name \"foo\" :body \"bar\")
      => {:name \"foo\" :type \"Guide\" :prefix \"none\" :ext \"html\" :body \"bar\"}
  "
  [& {:as m}]
  {:post [(s/valid? ::record %)]}
  (merge {:ext "html" :prefix "none" :type "Guide"} m))

(defn contents-dir [docset]
  (io/file (:base-dir docset)
           (str (:name docset) ".docset")
           "Contents"))

(defn resource-dir [docset]
  (io/file (contents-dir docset) "Resources"))

(defn document-dir [docset]
  (io/file (resource-dir docset) "Documents"))

(defn info-plist [docset]
  (selmer/render
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
    <!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">
    <plist version=\"1.0\">
    <dict>
    <key>CFBundleIdentifier</key>   <string>{{id}}</string>
    <key>CFBundleName</key>         <string>{{name}}</string>
    <key>DocSetPlatformFamily</key> <string>{{family}}</string>
    <key>isDashDocset</key>         <true/>
    <key>isJavaScriptEnabled</key>  <true/>
    </dict>
    </plist>"
    docset))

(def ^:private search-index-schema
  (jdbc/create-table-ddl
    "searchIndex"
    [[:id :integer "primary key" "autoincrement"]
     [:name :text]
     [:type :text]
     [:path :text]]))

(defn record->filename
  [record]
  (->> [:prefix :type :name :ext]
       (map #(% record))
       (str/join ".")))

(defn records->rows [docset]
  (map #(identity {:name (:name %)
                   :type (:type %)
                   :path (record->filename %)})
       (:records docset)))

(defn generate
  "Generate docset from `docset` map."
  [docset]
  (.mkdirs (document-dir docset))
  ;; Info.plist
  (spit (io/file (contents-dir docset) "Info.plist")
        (info-plist docset))
  ;; Record files
  (doseq [r (:records docset)]
    (spit (io/file (document-dir docset) (record->filename r))
          (:body r)))
  ;; docSet.dsidx
  (let [f (io/file (resource-dir docset) "docSet.dsidx")
        db {:subprotocol "sqlite" :subname f}]
    (when (.exists f)
      (.delete f))
    (jdbc/execute! db search-index-schema)
    (jdbc/insert-multi! db "searchIndex" (records->rows docset))))
