(ns evalive.test.gen
  (:use [evalive.core :only (lexical-context evil destro wtfn)] :reload-all)
  (:use clojure.test.generative)
  (:require [clojure.test.generative.generators :as gen]))

(defn integer
  "Distribution of integers biased towards the small, but
   including all longs."
  []
  (gen/one-of #(gen/uniform -1 32) gen/byte gen/short gen/int gen/long))

(defn longable?
  [n]
  (try
    (long n)
    true
    (catch Exception _)))

(defspec integer-commutative-laws
  (partial map identity)
  [^{:tag `integer} a ^{:tag `integer} b]
  (if (longable? (+' a b))
    (assert (= (+ a b) (+ b a)
               (+' a b) (+' b a)
               (unchecked-add a b) (unchecked-add b a)))
    (assert (= (+' a b) (+' b a))))
  (if (longable? (*' a b))
    (assert (= (* a b) (* b a)
               (*' a b) (*' b a)
               (unchecked-multiply a b) (unchecked-multiply b a)))
    (assert (= (*' a b) (*' b a)))))

