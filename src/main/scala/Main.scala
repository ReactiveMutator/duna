package architect
package duna


//import db.Relation
import db.Var
import db.StateManager
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def main(args: Array[String]): Unit = {

    def time[R](block: => R): R = {  
      val t0 = System.nanoTime()
      val result = block    // call-by-name
      val t1 = System.nanoTime()
      println("Elapsed time: " + (t1 - t0).toFloat/1000000000 + "s")
      result
  }


/**  
  val runtime = Runtime.getRuntime();
  val usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
  println("Used Memory before " + usedMemoryBefore/1000000 + "Mb");

implicit val stateManager = StateManager()

  def mutator() = {
    val s = Var(0, 2)

    for(i <- 1 to 30){
      s := i 

    }

    println(s())

  }
    
  time(mutator())


  def fib(n: Int): Int = {

   val first = Var(0)
   val second = Var(1)
   val count = Var(0)

   while(count().get < n){
      val sum = first().get + second().get
      first := second().get
      second := sum
      count := count().get + 1
   }

   return first().get
}
  time(println("concurrently = " + fib(4)))

  stateManager.stop()

// working code here
val usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
println("Memory increased:" + (usedMemoryAfter-usedMemoryBefore)/1000000 +"Mb");


val runtime2 = Runtime.getRuntime();
val usedMemoryBefore2 = runtime.totalMemory() - runtime.freeMemory();
println("Used Memory before " + usedMemoryBefore2/1000000 + "Mb");


def fib3(n: Int): Int = {
   def fib_tail(n: Int, a: Int, b: Int): Int = n match {
      case 0 => a
      case _ => fib_tail(n - 1, b, a + b)
   }
   return fib_tail(n, 0 , 1)
}
 time(println("simple = " + fib3(4)))

// working code here
val usedMemoryAfter2 = runtime.totalMemory() - runtime.freeMemory();
println("Memory increased:" + (usedMemoryAfter2-usedMemoryBefore2)/1000000 +"Mb");


  val runtime = Runtime.getRuntime();
  val usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
  println("Used Memory before   Future" + usedMemoryBefore/1000000 + "Mb");


  def mutator2() = {
      var s = Future{Thread.sleep(1000); 0}
      var n = Future{Thread.sleep(1000); 0}
      var m = Future{Thread.sleep(1000); 0}
      var k = Future{Thread.sleep(1000); 0}
      var l = Future{Thread.sleep(1000); 0}
    
        for(i <- 0 to 2){
          s = Future{Thread.sleep(1000); i }
          n = Future{Thread.sleep(1000); i }
          m = Future{Thread.sleep(1000); i }
          k = Future{Thread.sleep(1000); i }
        }
       // s.trigger(println)
      s.onComplete{a => n.onComplete{b => println(a.get + b.get)}}


    }
    time(mutator2())

// working code here
val usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
println("Memory increased Future:" + (usedMemoryAfter-usedMemoryBefore)/1000000 +"Mb");

  val runtime2 = Runtime.getRuntime();
  val usedMemoryBefore2 = runtime2.totalMemory() - runtime2.freeMemory();
  println("Used Memory before" + usedMemoryBefore2/1000000 + "Mb");
  implicit val stateManager = StateManager()

  def mutator3(): Var[Int] = {
      val s = Var({Thread.sleep(1000); 0}, 2)
      val n = Var({Thread.sleep(1000); 0}, 2)

    
        for(i <- 0 to 2){
          s := {Thread.sleep(1000); i }
          n := {Thread.sleep(1000); i }

        }
       // s.trigger(println)
      s

    }
    val k = time(mutator3())
    println(k())
    stateManager.stop()
// working code here
  val usedMemoryAfter2 = runtime2.totalMemory() - runtime2.freeMemory();
  println("Memory increased:" + (usedMemoryAfter2-usedMemoryBefore2)/1000000 +"Mb");

*/

import duna.db.Rx
implicit val stateManager = StateManager()

  val s = Var(4)
  val k = Var(9)

  val rx = Rx[Int]{implicit self => s() + k()}
  s := 2
  rx.onChange{println}
  stateManager.stop()

  }
}
