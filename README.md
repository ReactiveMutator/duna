

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