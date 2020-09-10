(ns clj-kondo.inspector
  (:require [clj-kondo.inspector.impl :as impl]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]))

(defn emit-types []
  (impl/emit-kondo-types))

(def cli-options
  [])

(defn -main [& args]
  (let [_ (:options (parse-opts args cli-options))]
    (impl/emit-types)
    (println "Add" (.getPath (io/file "configs" "inspector"))
             "to :config-paths in .clj-kondo/config.edn to activate.")))
