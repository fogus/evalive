(defproject evalive "1.1.0"
  :description "Various eval functions and macros for Clojure"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [fogus/lexical-chocolate "0.0.1"]]
  :plugins [[lein-swank "1.4.4"]
            [lein-marginalia "0.8.0"]
            [lein-generative "0.1.4.0"]]
  :generative-path "test/")
