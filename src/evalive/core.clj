(ns evalive.core)

(defn eval-map [ctx expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v])
                    ctx)]
      ~expr)))

(comment
  (eval-map '{a 10} '(* a a)))

