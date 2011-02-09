# evalive

various `eval` functions for Clojure

[documentation](http://fogus.me/fun/evalive/)

![evalive](http://images.fogus.me/logos/evalive.png "0x14 eyes")

Examples
--------

    (use '[evalive.core :only (evil destro)])

### evil

    (evil '{message "Hello", place "Cleveland"}
          '(println message place))
    
    ; Hello Cleveland

### destro

    (destro [message place] ["Hello" "Cleveland"])
    ;=> {vec__2438 [Hello Cleveland], message Hello, place Cleveland}

### evil destro
  
    (evil (destro [message place] ["Hello" "Cleveland"])
          '(println message place))
    
    ; Hello Cleveland

Getting
-------

### Leiningen

Modify your [Leiningen](http://github.com/technomancy/leiningen) dependencies to include [evalive](http://fogus.me/fun/evalive/):

    :dependencies [[evalive "1.0.0"] ...]    

### Maven

Add the following to your `pom.xml` file:

    <dependency>
      <groupId>evalive</groupId>
      <artifactId>evalive</artifactId>
      <version>1.0.0</version>
    </dependency>

## License

Copyright (C) 2011 Fogus

Distributed under the Eclipse Public License, the same as Clojure.
