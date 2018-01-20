package architect
package duna
/**
import org.scalatest._
import prop._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }
import duna.processing.{TaskManager, Worker, Task }
import scala.util.{ Either, Left, Right }
import java.util.concurrent.{Future, Executors, ExecutorService} 

class TaskManagerPropSpec extends FlatSpec with Matchers {
  
 "When TaskManager was set a working thread, it " should "say isRunning == true; noTask == false; isComplete == false" in {
        val pool: ExecutorService = Executors.newFixedThreadPool(1)
        val worker = Worker[Int](() => {Thread.sleep(1000); 2})
        val taskManager = TaskManager[Int]()
        val task = Task(pool.submit(worker))
        taskManager.set(task)
       
        if(!pool.isShutdown) {
          
          assert(taskManager.isRunning == true)
          assert(taskManager.noTask == false)
          assert(taskManager.isComplete == false)
          pool.shutdown

        }else{
          fail("How on the Eath the pool can be shutted down???")
        }

  }

  "TaskManager " should " not have working threads after initializing. It should say noTask == true; isRunning == false; isComplete == false" in {

        val taskManager = TaskManager[Int]()

        assert(taskManager.noTask == true)
        assert(taskManager.isRunning == false)
        assert(taskManager.isComplete == false)
  }


  "TaskManager " should " have complete task after task completeon. It should say noTask == false; isRunning == false; isComplete == true" in {

        val pool: ExecutorService = Executors.newFixedThreadPool(1)
        val worker = Worker[Int](() => 2)
        val taskManager = TaskManager[Int]()
        val task = Task(pool.submit(worker))
        taskManager.set(task)
        
        if(!pool.isShutdown) {
          Thread.sleep(1000)
          assert(taskManager.isRunning == false)
          assert(taskManager.noTask == false)
          assert(taskManager.isComplete == true)
          pool.shutdown

        }else{
          fail("How on the Eath the pool can be shutted down???")
        }

  }
  
}*/