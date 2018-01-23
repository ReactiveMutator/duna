package architect
package duna
package db

import java.util.concurrent.Callable
import processing.Worker
import duna.db.StateManager.{ Exec }
import duna.kernel.{Task, Callback}

class Rx[A](manager: StateManager, calculation: Rx[A] => A){ self =>
  
  @volatile private var value: A = calculation(self)
  //@volatile private var task: Task[A] = Task()
  @volatile private var completeon: Callback[A] = Callback(a => ())

  def recalc = {
    
    //task = manager.exec(Exec(() => { value = calculation(self); completeon.run(value); value}))
    value = calculation(self)
  }

  def onComplete(cb: A => Unit): Boolean = {

    completeon = Callback(cb)
    true

  }

  def now: A = {
   // task.waiting
    value

  }

}
object Rx{

  def apply[A](calculation: Rx[A] => A)(implicit manager: StateManager): Rx[A] = new Rx(manager, calculation)

}