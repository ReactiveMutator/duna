package architect
package duna
package api

import duna.kernel.{Task, Callback}
import scala.collection.mutable.HashMap

class Rx[A](calculation: Rx[A] => A){ self =>
  
  @volatile private var value: A = calculation(self)
  @volatile private var completeon: Callback[A] = Callback(a => ())
  private val reactions: HashMap[Int, Rx[A]] = HashMap()

  def recalc: A = synchronized{
    
    val computed = calculation(self)

    value = computed

    completeon.run(computed)

    reactions.foreach{r => r._2.recalc}
    
    value

  }

  def onChange(cb: A => Unit): Boolean = {

    completeon = Callback(cb)
    true

  }

  def apply()(implicit rx: Rx[A]): A = {

    if(reactions.get(rx.hashCode) == None){
      reactions += rx.hashCode -> rx
    } 

    value

  }

  def now = {

    value
  }

}
object Rx{

  def apply[A](calculation: Rx[A] => A): Rx[A] = new Rx(calculation)

}