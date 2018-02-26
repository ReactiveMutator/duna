[![Codacy Badge](https://api.codacy.com/project/badge/Grade/09b67f8d9e0d474d9fd3e9c1dae1c00d)](https://www.codacy.com/app/garrynsk/duna?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=garrynsk/duna&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/09b67f8d9e0d474d9fd3e9c1dae1c00d)](https://www.codacy.com/app/garrynsk/duna?utm_source=github.com&utm_medium=referral&utm_content=garrynsk/duna&utm_campaign=Badge_Coverage) [![Build Status](https://travis-ci.org/garrynsk/duna.svg?branch=master)](https://travis-ci.org/garrynsk/duna)

# DUNA

A very raw library for concurrency in scala.

It consists of two main components:

* `val s = Var(smth)`
* `val rx = Rx{ s() + d() }`

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

  s.now // Returns the last value.
}

mutator()

stateManager.stop() // Stops threadpool which works under the hood
```

## Fibonacci example

Multithreading is not a cure-all for performance problems. But in case of long lasting tasks it can improve a situation significantly.
Below are simple examples based on Fibonacci sequence. Measures were taken on 4 core AMD A8-4500M APU, 16GB DDR3, Arch Linux 1.4, 64-bit.
As expected, the most performant code is the most straitforward: a mutable approach. But when time comes to latency, multithreading thrives. So that, a thread pool consisted of 3 Vars outperforms mutable variables and the fourth example is three times faster than third.

### No latency
##### 1 Example
```scala
// Naive mutable approach

  def fib(n: Int): Int = {

    var first = 0
    var second = 1
    var count = 0

    while(count < n){
      
        val sum = first + second
        first =  second
        second = sum
        count = count + 1
    }

    first
  }

  fib(8)
```
###### Results:
> * Elapsed time: 5.561E-5s
> * Memory increased: 0 Mb

##### 2 Example
```scala
// Concurrent approach
  import duna.api.{ Var, StateManager }

  
  implicit val stateManager = StateManager()

  def fib(n: Int): Int = {

    val first = Var(0)
    val second = Var(1)
    val count = Var(0)

    while(count.now < n){
        val secondGet = second.now
        val countGet = count.now
        val sum = first.now + secondGet
        first := secondGet
        second := sum
        count := countGet + 1
    }

    first.now
  }

  println(fib(8))
  
```
###### Results:
> * Elapsed time: 0.05673288s
> * Memory increased: 4 Mb


### Latancy
##### 3 Example
```scala
// Naive mutable approach

  def fib(n: Int): Int = {

    var first = 0
    var second = 1
    var count = 0

    while(count < n){
      
        val sum = first + second
        first = {Thread.sleep(1000); second}
        second = {Thread.sleep(1000); sum}
        count = {Thread.sleep(1000); count + 1}
    }

    first
  }

  println(fib(8))
```
###### Results:
> * Elapsed time: 24.005672s
> * Memory increased: 0 Mb

##### 4 Example
```scala
// Concurrent approach
  import duna.api.{ Var, StateManager }

  implicit val stateManager = StateManager()

  def fib(n: Int): Int = {

    val first = Var(0)
    val second = Var(1)
    val count = Var(0)

    while(count.now < n){
        val secondGet = second.now
        val countGet = count.now
        val sum = first.now + secondGet
        first := {Thread.sleep(1000); secondGet}
        second := {Thread.sleep(1000); sum}
        count := {Thread.sleep(1000); countGet + 1}
    }

    first.now
  }


  println(fib(8))

  stateManager.stop()
```
###### Results:
> * Elapsed time: 8.06258s
> * Memory increased: 4 Mb

## Fibonacchi with Rx

```scala
import duna.api.{ Rx, Var, StateManager }

implicit val stateManager = StateManager()

    val first = Var(0)
    val second = Var(1)
    val counter = Var(0)

    val rx = Rx[Int]{implicit rx => 
    
      val c = counter.now
      if(c < 20){
        counter := { Thread.sleep(1000); c + 1 }
        val s = second.now
        first := { Thread.sleep(1000); s }
        second := { Thread.sleep(1000); first() + s }
      } 
    second.now
    }  

    first := 1
  stateManager.stop()
  ```
  ###### Results:

> * Elapsed time: 18.091383s
> * Memory increased: 5 Mb
```
  21
  34
  55
  89
  144
  233
  377
  610
  987
  1597
  2584
  4181
  6765
  10946
  ```

## A power of two

```scala

import duna.api.{ Rx, Var, StateManager }
  
  implicit val stateManager = StateManager()

    val first = Var(0)
    val second = Var(1)
    var counter = 0

    second.onChange{value =>
      if(counter < 20){
        counter += 1 
        first := value
      } 
    }
    first.onChange{value =>
      val sec = second.now
      second := value + sec
    }
    first.onChange(println)
    first := 1

  stateManager.stop()
  ```
  ###### Results:
 ``` 
  1
  2
  4
  8
  16
  32
  64
  128
  256
  512
  1024
  2048
  4096
  8192
  16384
  32768
  65536
  131072
  262144
  524288
  1048576
 ```
