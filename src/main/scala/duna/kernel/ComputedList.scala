package architect
package duna
package kernel

import scala.util.{Try, Success, Failure}
import scala.collection.mutable.HashMap

case class ComputedList[F[_], A](){

  private val computedList: HashMap[Int, F[A]] = HashMap()

  // Send a signal to F[A], that Var has changed
  def signal[B](add: F[A] => B): Seq[B] = {

    computedList.map{rx => add(rx._2)}.toSeq

  }

  // add F[A] 
  def add(fa: F[A]): HashMap[Int, F[A]] = {

    if(computedList.get(fa.hashCode) == None){
      computedList += fa.hashCode -> fa
    } 
    computedList

  }
}