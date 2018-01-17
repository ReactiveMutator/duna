[![Codacy Badge](https://api.codacy.com/project/badge/Grade/51678cdeee544edb9d7219ca20b3d214)](https://www.codacy.com/app/garrynsk/duna?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=garrynsk/duna&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/51678cdeee544edb9d7219ca20b3d214)](https://www.codacy.com/app/garrynsk/duna?utm_source=github.com&utm_medium=referral&utm_content=garrynsk/duna&utm_campaign=Badge_Coverage) [![Build Status](https://travis-ci.org/garrynsk/duna.svg?branch=master)](https://travis-ci.org/garrynsk/duna)

# DUNA

A very raw library for concurrency in scala. 

It consists of two main elements:

* val s = Var(smth)
* val rx = Rx{s() + d()}

Semantics was borrowed from Li Haoyi's [scala.rx](https://github.com/lihaoyi/scala.rx). So it has some connections with FRP. But only a little. Internally, the library is mainly mutable,
nevertheless, it is still thread safe.

```scala
// stateManager distribute threads between Vars
  implicit val stateManager = StateManager()

  def mutator() = {
    val s = Var(0, 7)   // first number - initial value
                        // second number - a volume of an internal queue. Var's performans heavy depends on this number.

    for(i <- 1 to 5){

      s := i // Mutates Var value. Mutations of every Var happen sequentially.

    }

    s() // Returns last value.
  }
    
  mutator()

  stateManager.stop() // Stops threadpool which works under the hood
  ```