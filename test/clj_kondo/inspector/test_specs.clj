(ns clj-kondo.inspector.test-specs
  (:require [clj-kondo.inspector :as i]
            [clojure.spec.alpha :as s]))

(defn foo [x y]
  (+ x y))

(s/def ::x string?)
(s/def ::y ::x)

(s/fdef foo :args (s/cat :x int? :y ::y))

(comment (foo 1 :foo)) ;; Expected string, received keyword

(defn bar [m]
  m)

(s/def ::z string?)

(s/fdef bar :args (s/cat :m (s/keys :req-un [::x ::y]
                                    :opt-un [::z])))

(comment (bar {:x "foo"}) ;; Missing required key :y
         (bar {:x 1 :y "foo"}) ;; Expected string, received positive integer
         (bar {:x "foo" :y "foo" :z 1}) ;; Expected string, received positive integer
         )

;;;; Scratch

(comment
  (i/emit-types)
  )
