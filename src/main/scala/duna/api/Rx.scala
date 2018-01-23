package architect
package duna
package api

import java.util.concurrent.Callable
import processing.Worker
import duna.api.StateManager.{ Exec }
import duna.kernel.{Task, Callback}

class Rx[A](manager: StateManager, calculation: Rx[A] => A){ self =>
  
  @volatile private var value: A = calculation(self)
  @volatile private var completeon: Callback[A] = Callback(a => ())

  def recalc = {
    
    val computed = calculation(self)

    completeon.run(computed)

    value = calculation(self)
    
    value

  }

  def onChange(cb: A => Unit): Boolean = {

    completeon = Callback(cb)
    true

  }

  def now: A = {

    value

  }

}
object Rx{

  def apply[A](calculation: Rx[A] => A)(implicit manager: StateManager): Rx[A] = new Rx(manager, calculation)

}