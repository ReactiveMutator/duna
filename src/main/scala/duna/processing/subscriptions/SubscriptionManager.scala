package architect
package duna
package processing
package subscriptions


case class SubscriptionManager[A](){

  val triggers: Triggers[A] = Triggers()

  def run(value: A): A = {

    triggers.run(value)

    value
  }
  
  def add(value: A => Unit): Boolean = {

    triggers.add(value)

  }

}