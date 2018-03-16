package architect
package duna
package kernel

import scala.util.{Try, Success, Failure}
import scala.collection.immutable.Map

case class ComputedList[F[_], A](){

  @volatile private var computedList: Map[Int, F[A]] = Map()

  // Send a signal to F[A], that Var has changed
  def signal[B](add: F[A] => B): Seq[B] = {

    val sendedSignal = computedList.map{rx => add(rx._2)}.toSeq
    sendedSignal
  }

  // add F[A] 
  def add(fa: F[A]): Map[Int, F[A]] = {

    if(computedList.get(fa.hashCode) == None){
      
      val newComputedList = computedList + (fa.hashCode -> fa)

      computedList = newComputedList 

    } 
    computedList

  }
}