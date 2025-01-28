# evalive

various `eval` functions for Clojure

[documentation](http://fogus.me/fun/evalive/)

![evalive](http://images.fogus.me/logos/evalive.png "0x14 eyes")

Examples
--------
```clojure

    (require '[fogus.evalive :refer (evil destro wtfn)])
```

### evil
```clojure

    (evil '{message "Hello", place "Cleveland"}
          '(println message place))
    
    ; Hello Cleveland
```

### destro
```clojure

    (destro [message place] ["Hello" "Cleveland"])
    ;=> {vec__2438 [Hello Cleveland], message Hello, place Cleveland}
```

### evil destro
```clojure

    (evil (destro [message place] ["Hello" "Cleveland"])
          '(println message place))
    
    ; Hello Cleveland
```

### wtfn
```clojure

    (def callable-and (wtf clojure.core.and))
    
    (callable-and true 1 2 3 {} true 42)
    ;=> 42
```

### wtfn destro

```clojure

	(def destruction (wtfn destro))

    (map #(apply destruction %)
           [['[h & t] [1 2 3 4 5]]
            ['[car cdr] [:first :rest]]
            ['[a b [c d & e] :as Z] [1 2 [3 4 5 6 7 8]]]])
    
    ;=> ({vec__2220 [1 2 3 4 5], h 1, t (2 3 4 5)}
         {vec__2223 [:first :rest], car :first, cdr :rest}
         {vec__2226 [1 2 [3 4 5 6 7 8]],
          a 1,
          b 2,
          vec__2227 [3 4 5 6 7 8],
          c 3,
          d 4,
          e (5 6 7 8),
          Z [1 2 [3 4 5 6 7 8]]})
```

Including
---------

### deps.edn

    me.fogus/evalive {:mvn/version "1.1.0"}

### Leiningen

Modify your [Leiningen](http://github.com/technomancy/leiningen) dependencies to include [evalive](http://fogus.me/fun/evalive/):

    :dependencies [[me.fogus/evalive "1.1.0"] ...]    

### Maven

Add the following to your `pom.xml` file:

    <dependency>
      <groupId>me.fogus</groupId>
      <artifactId>evalive</artifactId>
      <version>1.1.0</version>
    </dependency>

### References

- [debug-repl](https://github.com/GeorgeJahad/debug-repl)

## License

Copyright (C) 2011-2025 [Fogus](http://www.fogus.me)

Distributed under the Eclipse Public License, the same as Clojure.
