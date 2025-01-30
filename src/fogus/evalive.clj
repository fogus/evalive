; Copyright (c) Michael Fogus, 2010-2025. All rights reserved.  The use
; and distribution terms for this software are covered by the Eclipse
; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file COPYING the root of this
; distribution.  By using this software in any fashion, you are
; agreeing to be bound by the terms of this license.  You must not
; remove this notice, or any other, from this software.

(ns fogus.evalive
  "Various eval functions and macros."
  (:require [fogus.lexical.chocolate :as lexical]
            [fogus.evalive.protocol :as proto]))

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

(defn evil [ctx form]
  (proto/-evil ctx form))

;; ### lexical contexts defined in maps
(extend-type java.util.Map
  proto/Evil
  (-evil [this form]
    (eval
     `(let [~@(mapcat (fn [[k v]] [k `'~v])
                      this)]
        ~form))))

;; ### lexical contexts defined in sequentials (e.g. lists, vectors)
(extend-type java.util.List
  proto/Evil
  (-evil [this form]
    (eval
     `(let [~@(map-eo compile-time-lookup this)]
        ~form))))

;; ### lexical contexts defined in arrays
(extend-type (Class/forName "[Ljava.lang.Object;")
  proto/Evil
  (-evil [this form]
    (evil (seq this) form)))

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
;; based off the the awesome `->fn` macro by
;; the amazing [Alan Dipert](http://alan.dipert.org).
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

(defmacro destro
  "DEPRECATED - Use lexical-chocolate's impl instead.
  https://github.com/fogus/lexical-chocolate"
  [binds form]
  `(let [~binds ~form]
     (lexical/context)))

(comment
  (evil '{message "Hello", place "Cleveland"}
        '(println message place))

  ;; Hello Cleveland
  
  (destro [message place] ["Hello" "Cleveland"])
  ;;=> {vec__2438 [Hello Cleveland], message Hello, place Cleveland}
  
  (evil (destro [message place] ["Hello" "Cleveland"])
        '(println message place))

  ;; Hello Cleveland

  (letfn [(foo_internal [env x]
            (evil env x))]
    (foo_internal '{x 42} (quote x)))

  ;; TODO
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

  (def destruction (wtfn lexical/destro))

  (use 'clojure.pprint)

  (pprint
   (map #(apply destruction %)
        [['[h & t] [1 2 3 4 5]]
         ['[car cdr] [:first :rest]]
         ['[a b [c d & e] :as Z] [1 2 [3 4 5 6 7 8]]]]))
)
