(ns evalive.core)

(defn- map-eo
  [f & colls]
  (apply map
         #(%1 %&)
         (cycle [first #(apply f %)])
         colls))

(def compile-time-lookup #(do `'~%))

;; # Public API

(defmacro lexical-context
  []
  (let [symbols (keys &env)]
    (zipmap (map (fn [sym] `(quote ~sym))
                 symbols)
            symbols)))

(defprotocol Evil
  (evil [this form]))

(extend-type java.util.Map
  Evil
  (evil [this form]
    (eval
     `(let [~@(mapcat (fn [[k v]] [k `'~v])
                      this)]
        ~form))))

(extend-type java.util.List
  Evil
  (evil [this form]
    (eval
     `(let [~@(map-eo compile-time-lookup this)]
        ~form))))

(extend-type (Class/forName "[Ljava.lang.Object;")
  Evil
  (evil [this form]
    (evil (seq this) form)))


(comment
  (evil '{a 100 b 2} '(* a b))
  (evil '[a 100 b 22] '(* a b))
  (evil (to-array '[a 200 b 5]) '(* a b))

  (eval 'x)
; blowed up

  (def x 1)
  (eval 'x)
;=> 1

  (binding [x 100]
    (eval 'x))
;=> 100

  (let [x 200]
    (eval 'x))

  (defn foo []
    y)
; will not compile

  (defmacro foo []
    'y)

  (macroexpand '(foo))
  (foo)
; blowed up

  (def y 42)
  (let [y 100000]
    (foo))

  (defn foo [] y)

  (let [y 100000]
    (foo))

  (ns foo)
  (eval 'x)
; blowed up

  (eval 'user/x)
; 1

  (binding [user/x 12]
    (eval 'user/x))
)
