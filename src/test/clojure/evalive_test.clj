(ns evalive-test
  (:use [evalive.core :only (evil destro wtfn)] :reload-all)
  (:use [clojure.test]))

(println "\nTesting with Clojure" (clojure-version))

(deftest test-evil-avec-maps
  (are [L R] (= L R)

     [1 2] (evil '{a 1 b 2} '[a b])
       
     [1 2] [(evil '{a 1} 'a)
            (evil '{a 2} 'a)]

     [1 2] (evil (hash-map 'a 1 'b 2)
                 '[a b])
     
     [1 2] (evil (array-map 'a 1 'b 2)
                 '[a b])

     [1 2] (evil (sorted-map 'a 1 'b 2)
                 '[a b])

     [1 2] (evil (sorted-map-by #(compare %2 %) 'a 1 'b 2)
                 '[a b])

     [1 2] (evil (sorted-map-by #(compare % %2) 'a 1 'b 2)
                 '[a b])

     [1 2] (evil (java.util.HashMap. '{a 1 b 2})
                 '[a b])))

(deftest test-evil-avec-maps-and-destructuring-forms
  (are [L R] (= L R)

    [1 2] (evil '{[a b] [1 2]}
                '[a b])

    [1 2] (evil '{[a b] [2 1]}
                '[b a])

    [1 2] (evil '{[a b :as Z] [1 2]}
                'Z)))

(deftest test-evil-avec-maps-and-back-references
  (are [L R] (= L R)

    '[1 b] (evil '{[a b] [1 2]
                  c b}
                 '[a c])

    ;;[1 2] (evil '{[a b] [1 2]}
    ;;            (evil '{c b}
    ;;                  '[a c]))

    '[a b] (evil '{[a b] [1 2]
                  c b
                  d a}
                 '[d c])))

(deftest test-evil-avectors
  (are [L R] (= L R)

    [1 2] (evil '[a 1 b 2] '[a b])

    [1 2] [(evil '[a 1] 'a)
           (evil '[a 2] 'a)]

    [1 2] (evil (vector 'a 1 'b 2)
                '[a b])

    [1 2] (evil (into [] '(a 1 b 2))
                '[a b])

    [1 2] (evil (interleave '[a b] [1 2])
                '[a b])))

(deftest test-destro
  (are [L R] (= L R)
       
    [1 2] (evil
           (destro [a b [c d & e] :as Z]
                   [1 2 [3 4 5 6 7 8]])
           '[a b])))

(defmacro square [x]
  `(let [x# ~x] (* x# x#)))

(deftest test-wtfn
  (are [L R] (= L R)
       [0 1 4 9 16] (map (wtfn square) (range 5))
       true         ((wtfn clojure.core/and) true true 1 2 3 true)
       false        ((wtfn clojure.core/and) true true 1 false 3 true)))
