(ns clj-docset.core
  (:require [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [selmer.parser :refer [render]]))

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
(s/def ::record (s/keys :req-un [:record/name :record/type :record/prefix :record/body]))

(s/def :docset/id string?)
(s/def :docset/name string?)
(s/def :docset/family string?)
(s/def :docset/base-dir string?)
(s/def :docset/records (s/+ ::record))
(s/def ::docset (s/keys :req-un [:docset/id :docset/name :docset/family]
                        :opt-un [:docset/base-dir :docset/records]))

(defn docset [m]
  (merge {:base-dir "."} m))

(defn contents-dir [docset]
  (io/file (:base-dir docset)
           (str (:name docset) ".docset")
           "Contents"))

(defn resource-dir [docset]
  (io/file (contents-dir docset) "Resources"))

(defn document-dir [docset]
  (io/file (resource-dir ds) "Documents"))

(defn info-plist [docset]
  (println "aa")
  (render
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
  
(def db {
         :subprotocol "sqlite"
         :subname "/tmp/foo.db"
         })

(def _ds_ (docset {:name "sample" :id "a" :family "a" :records [ {:name "b" :type "Type" :prefix "" :body ""} ]}))
(s/valid? ::docset _ds_)
(contents-dir _ds_)


(jdbc/execute! db (jdbc/create-table-ddl "foo" [[:id :int "not null"]]))
