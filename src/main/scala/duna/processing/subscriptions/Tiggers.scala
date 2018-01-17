package architect
package duna
package processing
package subscriptions

case class Triggers[A](){

  @volatile private var callbackOnCompleteList: List[A => Unit] = List()

  def run(value: A): Boolean = {

    callbackOnCompleteList.foreach(function => function(value))

    true

  }

  def add(cb: A => Unit): Boolean = {

    callbackOnCompleteList = cb::callbackOnCompleteList
    true

  }


}
