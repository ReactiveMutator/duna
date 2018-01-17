package architect
package duna
package db

import java.util.LinkedList

case class Obs[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](cb: A => Unit, val index: Int, 
                                                                                             val link: Var[A]){

  def delete(): Boolean = {

   // link.stopTrigger(index)
    true
  }                                                                                                

}
