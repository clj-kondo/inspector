(ns clj-kondo.inspector.impl
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.spec.alpha :as s]))

(def fn->k
  {'clojure.core/int? :int
   'clojure.core/string? :string})

(declare resolve-spec)

(defn cat* [& forms]
  (let [specs (take-nth 2 (rest forms))
        resolved (map resolve-spec specs)]
    resolved))

(defn unqualify [k]
  (keyword (name k)))

(defn keys* [& {:keys [:req-un :opt-un]}]
  (let [required (map resolve-spec req-un)
        optional (map resolve-spec opt-un)]
    {:op :keys
     :req (zipmap (map unqualify req-un) required)
     :opt (zipmap (map unqualify opt-un) optional)}))

(defn dispatch [[op & args]]
  (case op
    clojure.spec.alpha/cat (apply cat* args)
    clojure.spec.alpha/keys (apply keys* args)
    nil))

(defn resolve-spec [x]
  (cond (qualified-keyword? x)
        (resolve-spec (s/form (s/spec x)))
        (seq? x) (dispatch x)
        (symbol? x) (fn->k x)
        :else x))

(defn emit-types []
  (let [cfg-file (io/file ".clj-kondo" "configs" "inspector" "config.edn")
        config
        (reduce (fn [acc sym]
                  (let [sym-ns (symbol (namespace sym))
                        sym-name (symbol (name sym))
                        [_fspec _:args args-spec] (s/form (s/get-spec sym))]
                    (if (seq? args-spec)
                      (let [arg-types (resolve-spec args-spec)]
                        (if-not (every? nil? arg-types)
                          (let [arity (count arg-types)]
                            (assoc-in acc [:linters :type-mismatch :namespaces
                                           sym-ns sym-name :arities arity :args] arg-types))
                          acc))
                      acc)))
                {}
                (filter symbol? (keys (s/registry))))]
    ;; (pprint/pprint config)
    (io/make-parents cfg-file)
    (spit cfg-file config)
    config))

;;;; Scratch

(comment
  (emit-types)
  )
