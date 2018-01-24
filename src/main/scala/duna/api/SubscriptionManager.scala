package architect
package duna
package api

import scala.collection.immutable.SortedMap
import duna.kernel.Callback

case class SubscriptionManager[A](){ self => 
  
  @volatile private var triggers: SortedMap[Int, Obs[A]] = SortedMap()

  def hasTriggers: Boolean = {

    triggers.nonEmpty

  }

  def run(value: A): Boolean = {
           
    triggers.foreach(tr => tr._2.run(value))

    true

  }

  def remove(observer: Obs[A]): Boolean = {

    val newTriggers = triggers - observer.hashCode

    triggers = newTriggers

    true

  }

  def trigger(cb: Callback[A]): Obs[A] = {

    val obs: Obs[A] = Obs(cb, self)
    
    triggers = triggers + (obs.hashCode -> obs)

    obs

  }

  def deleteAll: Boolean = {

    val newTriggers = triggers.drop(triggers.size)

    triggers = newTriggers

    true

  }


}