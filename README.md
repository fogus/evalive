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

### wtfn
```clojure

    (def callable-and (wtf clojure.core.and))
    
    (callable-and true 1 2 3 {} true 42)
    ;=> 42
```

Including
---------

### deps.edn

    me.fogus/evalive {:mvn/version "1.1.1"}

### Leiningen

Modify your [Leiningen](http://github.com/technomancy/leiningen) dependencies to include [evalive](http://fogus.me/fun/evalive/):

    :dependencies [[me.fogus/evalive "1.1.1"] ...]    

### Maven

Add the following to your `pom.xml` file:

    <dependency>
      <groupId>me.fogus</groupId>
      <artifactId>evalive</artifactId>
      <version>1.1.1</version>
    </dependency>

### References

- [debug-repl](https://github.com/GeorgeJahad/debug-repl)

## License

Copyright (C) 2011-2025 [Fogus](http://www.fogus.me)

Distributed under the Eclipse Public License, the same as Clojure.
