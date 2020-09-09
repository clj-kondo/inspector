# Clj-kondo inspector

Experimental tool to turn [clojure.spec.alpha](https://github.com/clojure/spec.alpha) specs into [clj-kondo](https://github.com/borkdude/clj-kondo/) [type annotations](https://github.com/borkdude/clj-kondo/blob/master/doc/types.md) for linting.

Very alpha, breaking changes will happen. Not ready for serious usage. Contributions welcome.

## Usage

``` clojure
(require '[clj-kondo.inspector :as i])
(require '[your.specs])
(i/emit-types)
```

Add `"configs/inspector"` to your `:config-paths` in `config.edn`. Requires clj-kondo 2020.09.09 or newer.

## Example

From `test/clj_kondo/inspector/test_specs.clj`:

``` clojure
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
```

## License

Copyright © 2020 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
