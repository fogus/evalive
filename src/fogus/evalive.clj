;; by Fogus
;;
;; <http://github.com/fogus/evalive>

; Copyright (c) Michael Fogus, 2010-2025. All rights reserved.  The use
; and distribution terms for this software are covered by the Eclipse
; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file COPYING the root of this
; distribution.  By using this software in any fashion, you are
; agreeing to be bound by the terms of this license.  You must not
; remove this notice, or any other, from this software.

(ns fogus.evalive
  "Various eval functions and macros."
  (:require [fogus.lexical.chocolate :as lexical]))

;; ![evalive](http://images.fogus.me/logos/evalive.png "0x14 eyes")

(defn- map-eo
  "Map-every-other.  Works like `map` except only on every other element."
  [f & colls]
  (apply map
         #(%1 %&)
         (cycle [first #(apply f %)])
         colls))

(def ^{:private true
       :doc "Looks up the value of a symbol at the time of compilation and quotes it.  This will be useful later on."}
  compile-time-lookup
  #(do `'~%))

(def ^:dynamic *maximum-evil*
  "This defines if macros should provide maximum access."
  false)

;; # Public API

(defprotocol Evil
  "Defines the public interface to evilive's \"contextual eval\"&reg; facilities.  In a nutshell,
   contextual eval refers to perform an eval that refers to lexical bindings in addition to namespace
   bindings.  You see, the core `eval` function, like and function, is not privy to the lexical context
   in which it is run and is therefore of limited scope in its usefulness.  However, evalive enhances
   the stock `eval` by building a lexical context into the form under evaluation from various structures."
  (evil [this form]))

;; ### lexical contexts defined in maps
(extend-type java.util.Map
  Evil
  (evil [this form]
    (eval
     `(let [~@(mapcat (fn [[k v]] [k `'~v])
                      this)]
        ~form))))

;; ### lexical contexts defined in sequentials (e.g. lists, vectors)
(extend-type java.util.List
  Evil
  (evil [this form]
    (eval
     `(let [~@(map-eo compile-time-lookup this)]
        ~form))))

;; ### lexical contexts defined in arrays
(extend-type (Class/forName "[Ljava.lang.Object;")
  Evil
  (evil [this form]
    (evil (seq this) form)))

(defmacro destro
  "Provides a simple way to obtain a map of the lexical context based on the
   result of a destructuring operation.  That is, the typical call to the
   `destructure` function will operate along the lines of:

    (destructure '[[_ _ x _ _] [1 2 3 4 5]])
    
    ;=> [V [1 2 3 4 5]
         _ (nth V 0 nil)
         _ (nth V 1 nil)
         x (nth V 2 nil)
         _ (nth V 3 nil)
         _ (nth V 4 nil)]

   whereby the form returned contains the operations needed to pull apart (i.e. destructure)
   the data structure under examination.  However, `destro` will instead resolve the values
   of the destructuring operation, including any intermediate bindings, as below:

    (destro [a b [c d & e] :as Z]
            [1 2 [3 4 5 6 7 8]])

    ;=> {vec__2330 [1 2 [3 4 5 6 7 8]],
         a 1,
         b 2,
         vec__2331 [3 4 5 6 7 8],
         c 3,
         d 4,
         e (5 6 7 8),
         Z [1 2 [3 4 5 6 7 8]]}

   This will also operate as expected within a lexical context:

    (let [c [1 2]]
      (destro [a b] c))

    ;=> {c [1 2],
         vec__2336 [1 2],
         a 1,
         b 2}"
  [binds form]
  `(let [~binds ~form]
     (lexical/context)))

;;
;; **Invoke a macro like a function - if you dare!**
;; <pre>
;;                   ______
;;                .-"      "-.
;;               /            \
;;   _          |              |          _
;;  ( \         |,  .-.  .-.  ,|         / )
;;   > "=._     | )(__/  \__)( |     _.=" <
;;  (_/"=._"=._ |/     /\     \| _.="_.="\_)
;;         "=._ (_     ^^     _)"_.="
;;             "=\__|IIIIII|__/="
;;            _.="| \IIIIII/ |"=._
;;  _     _.="_.="\          /"=._"=._     _
;; ( \_.="_.="     `--------`     "=._"=._/ )
;;  > _.="                            "=._ <
;; (_/   jgs                              \_)
;;
;; </pre>
;;
;; based off the the awesome `->fn` by
;; the awesome [Alan Dipert](http://alan.dipert.org).
;;

(defmacro wtfn
  "Takes a macro name and returns a function that
  invokes the macro as if it were a function."
  [macro-name]
  `(fn [& args#]
     (evil {}
      (-> @#'~macro-name
          (vary-meta dissoc :macro)
          (apply nil nil args#)))))         ;nils are env and form

(comment
  (evil '{message "Hello", place "Cleveland"}
        '(println message place))

  ; Hello Cleveland
  
  (destro [message place] ["Hello" "Cleveland"])
  ;=> {vec__2438 [Hello Cleveland], message Hello, place Cleveland}
  
  (evil (destro [message place] ["Hello" "Cleveland"])
        '(println message place))

  ; Hello Cleveland

  (letfn [(foo_internal [env x]
            (evil env x))]
    (foo_internal '{x 42} (quote x)))

  (deffexpr foo [x]
    (evil ))

  (DEFUN IF (CONDITION T-VAL NIL-VAL)
       (COND (CONDITION (EVAL T-VAL))
             (T (EVAL NIL-VAL))))

  (defn IF [condition T F]
    (cond (evil (lexical/context)
                condition)
          (evil (lexical/context)
                T)
          :default
          (evil (lexical/context)
                F)))

  (IF 'true 1 2)
  (IF 'false 1 2)
  (IF '(< 2 3) '(+ 1 2 3) :foo)
  (IF '(> 2 3) '(+ 1 2 3) '(keyword x))
  (let [x "bar"]
    (IF '(> 2 3) '(+ 1 2 3) '(keyword x)))

  (def destruction (wtfn destro))

  (use 'clojure.pprint)

  (pprint
   (map #(apply destruction %)
        [['[h & t] [1 2 3 4 5]]
         ['[car cdr] [:first :rest]]
         ['[a b [c d & e] :as Z] [1 2 [3 4 5 6 7 8]]]]))
)
