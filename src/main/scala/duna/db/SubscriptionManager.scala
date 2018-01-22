package architect
package duna
package db

import duna.kernel.{ Callback, Index }

case class SubscriptionManager[A](){ self => 
  
  @volatile private var triggers: List[Obs[A]] = List()
  @volatile private var completeon: Callback[A] = Callback(a => ())

  def hasTriggers: Boolean = {

    triggers.nonEmpty

  }

  def setCompleteon(cb: Callback[A]): Boolean = {

    completeon = cb
    true

  }

  def getCompleteon: Callback[A] = {

    val newCompleteon = completeon
    newCompleteon
  }

  def run(value: A): Boolean = {
           
    triggers.foreach(_.run(value))

    true

  }

  def remove(index: Index[Int]): Boolean = {

    val newTriggers = triggers.drop(index.value)

    triggers = newTriggers

    true

  }

  def add(cb: Callback[A]): Obs[A] = {

    val obs: Obs[A] = Obs(cb, Index(triggers.size), self)

    triggers = obs::triggers

    obs

  }

  def deleteAll: Boolean = {

    val newTriggers = triggers.drop(triggers.size)

    triggers = newTriggers

    true

  }


}