; Copyright (c) Michael Fogus, 2010-2025. All rights reserved.  The use
; and distribution terms for this software are covered by the Eclipse
; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file COPYING the root of this
; distribution.  By using this software in any fashion, you are
; agreeing to be bound by the terms of this license.  You must not
; remove this notice, or any other, from this software.

(ns fogus.evalive.protocol)

(defprotocol Evil
  "Defines the interface to evilive's \"contextual eval\"&reg; facilities.  In a nutshell,
   contextual eval refers to perform an eval that refers to lexical bindings in addition to namespace
   bindings.  You see, Clojure's core `eval` function is not privy to the lexical context
   in which it is run and is therefore of limited scope in its usefulness.  However, evalive enhances
   the stock `eval` by building a lexical context into the form under evaluation from various structures."
  (-evil [this form]))
