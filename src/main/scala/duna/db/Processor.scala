package architect
package duna
package db
 
import duna.processing.subscriptions.SubscriptionManager 
import duna.processing.events.EventManager 
import duna.processing.storage.DataManager
import duna.processing.{ TaskManager, NoTask, Task, ITask }
import duna.db.StateManager.{ Exec, EmptyMessage }
import scala.util.{ Try, Success, Failure }


class Processor[A](manager: StateManager, queueSize: Int){ self =>

  private val eventManager: EventManager[A] = EventManager(queueSize)
  private val dataManager: DataManager[A] = DataManager()
  private val taskManagerProcess: TaskManager[Boolean] = TaskManager()
  private val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  
  private def addTrigger(trigger: A => Unit): Boolean = {

    subscriptionManager.add(trigger)

  }

  private def runTriggers(value: A): Task[A] = {

    val subscriptionsExecutable: Exec[A] = Exec(() => {

      subscriptionManager.run(value)

    })

    manager.exec(subscriptionsExecutable)

  }

  private def updateInMemory(value: A): Task[A] = {
    
    val inMemoryExecutable: Exec[A] = Exec(() => { 

      dataManager.write(value)

    })

    manager.exec(inMemoryExecutable)

  }

  private def createExecuable: Exec[Boolean] = {

    Exec(() => { 

      val tasks = { data: A =>

        val inMemoryTask = updateInMemory(data)
        val triggersTask = runTriggers(data)
        inMemoryTask.waiting
        triggersTask.waiting
       
      }

      eventManager.next(tasks)

      true

    })

  }

  

  private def run: Boolean = {
    
    if(taskManagerProcess.isComplete || taskManagerProcess.noTask){

      val executable = createExecuable
      val task = manager.exec(executable)
      taskManagerProcess.set(task)

    }else{
      // taskManagerProcess will process new elements without our help
      false

    }
  }
  

  def write(value: => A): Boolean = {
    // enqueueing value is independent from processing, unless processing is much slower then enqueueing
    // in such cases, enqueueing must wait for a processing
    eventManager.put(value)
    
    // process elements after filling in the event queue
    run

  }


  def onComplete(cb: A => Unit): Boolean = {
    
    subscriptionManager.add(cb)


  }

  // Blocking method
  def read: Option[A] = {
    taskManagerProcess.waiting
    dataManager.read
  }

}

object Processor{

  def apply[A](manager: StateManager, init: => A, queueSize: Int): Processor[A] = {
    
      val newProcessor = new Processor[A](manager, queueSize)
      newProcessor.write(init) // enqueue initial value, so there is always exist some value, ready to consume
      newProcessor
  }

}