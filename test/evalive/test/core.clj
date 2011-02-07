(ns evalive.test.core
  (:use [evalive.core :only (lexical-context evil destro)] :reload-all)
  (:use [clojure.test]))

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

(deftest test-lexical-context
  (are [L R] (= L R)
       
       {}     (lexical-context)
       
       '{a 1} (let [a 1]
                (lexical-context))
       
       '{a 1
         b 2} (let [a 1
                    b 2]
                (lexical-context))

        '{a 1
          b 2
          c 3} (let [a 1
                     b 2
                     c 3]
                 (lexical-context))
          
        '{a 1
          b 2} (let [a 1]
                 (let [b 2]
                   (lexical-context)))

         '{a 1
           b 2
           c 3} (let [a 1]
                  (let [b 2]
                    (let [c 3]
                      (lexical-context))))))

(deftest test-destro
  (are [L R] (= L R)
       
    [1 2] (evil
           (destro [a b [c d & e] :as Z]
                   [1 2 [3 4 5 6 7 8]])
           '[a b])))