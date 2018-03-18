
// # ComputedList 
// ### Stores Rx in it's dependent Vars

package duna
package kernel

import scala.util.{Try, Success, Failure}
import scala.collection.immutable.Map


// TODO: Rename it.
// Example:
//
// ```scala
// val a = Var(1)
// val b = Var(2)
// 
// val rx = Rx[Int]{implicit rx => a() + b()} 
// ```
// It is clear from the example, that a and b has Var type. Moreover, their's apply method includes an implicit Rx value:
//
// ```scala
// def apply()(implicit rx: Rx[A]): A 
// ```
// You, probably, know that Vars must signal to Rxs every time they change their value. To do it, they need to track every Rx, which depends on the Var.
// How can we do this? Eazy! We gather Rxs with Var's apply method and keep them in ComputedList.
// By the way, Rxs also has the list, because they can depend on other Rxs too.

case class ComputedList[F[_], A](){
  // The key is a hashcode and the value is Rx.
  // FIXME: Do I really need the @volatile?
  @volatile private var computedList: Map[Int, F[A]] = Map()

  // Send a signal to Rx, that Var has changed.
  def signal[B](add: F[A] => B): Seq[B] = {

    val sendedSignal = computedList.map{rx => add(rx._2)}.toSeq
    sendedSignal
  }

  // Add Rx to computedList.
  def add(fa: F[A]): Map[Int, F[A]] = {
    // First of all, check is the Rx is already in a Map.
    if(computedList.get(fa.hashCode) == None){
      
      val newComputedList = computedList + (fa.hashCode -> fa)

      computedList = newComputedList 

    } 
    computedList

  }
}