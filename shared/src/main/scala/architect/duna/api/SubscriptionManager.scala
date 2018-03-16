package architect
package duna
package api

import duna.kernel.Callback 
import scala.util.{Try, Success, Failure}

case class SubscriptionManager[A](){ self => 
  
  @volatile private var triggers: Seq[Obs[A]] = Seq()

  override def toString: String = {
    triggers.toString
  }

  def hasTriggers: Boolean = {

    triggers.nonEmpty

  }

  def run(value: A): Seq[Try[A]] = {
    
    triggers.map{obs => obs.run(value)}

  }

  def remove(observer: Obs[A]): Boolean = {

    val newTriggers = triggers.filter{ value => value.hashCode != observer.hashCode } 
    
    triggers = newTriggers

    true

  }

  def trigger(cb: Callback[A]): Obs[A] = {

    val obs: Obs[A] = Obs(cb, self)
    
    triggers = triggers ++  Seq(obs)

    obs

  }

  def deleteAll: Boolean = {

    val newTriggers = triggers.drop(triggers.size)

    triggers = newTriggers

    true

  }


}