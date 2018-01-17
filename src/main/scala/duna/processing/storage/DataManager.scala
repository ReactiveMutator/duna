package architect
package duna
package processing
package storage

import duna.processing.events.{ EventManager, Event, IEvent, NoEvent }

case class DataManager[A](){

  private val changeLog: ChangeLog[A] = ChangeLog()         

  // Returns a value which was valid at a sertain time
  def read: Option[A] = {

    changeLog.get 

  }

  // Returns a value which is valid now
  def write(event: A): A = {

    changeLog.update(event)
    event
  }
          

}