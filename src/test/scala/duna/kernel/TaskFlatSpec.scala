package architect
package duna

import org.scalatest._
import prop._
import duna.kernel.Task 
import duna.processing.Executor
class TaskPropSpec extends FlatSpec with Matchers {
  
  "When Task is running, isRunning" should "return true" in {
      
      val executor: Executor = Executor()
          
      val job = () => Thread.sleep(1000)
      val future = executor.submit(job)
      val task = Task(future)

      task.isRunning should be (true)

      executor.close()
  }

  "When Task is null, isRunning" should "return false" in {
      
      val task = Task()

      task.isRunning should be (false)

  }

  "After 'waiting' the Task, isComplete" should "return true" in {
      
      val executor: Executor = Executor()
          
      val job = () => 6
      val future = executor.submit(job)
      val task = Task(future)

      task.waiting
      task.isComplete should be (true)

      executor.close()
  }

  "If Task is null, isComplete" should "return false" in {
      
      val task = Task()

      task.isComplete should be (false)

  }

  "If cancel the Task, task.isRunning" should "return false" in {
      
      val executor: Executor = Executor()
          
      val job = () =>  Thread.sleep(5000)
      val future = executor.submit(job)
      val task = Task(future)

      task.cancel
      task.isRunning should be (false)

      executor.close()
  }

  "If cancel the null Task, task.isRunning" should "return false" in {
      
      val task = Task()

      task.isComplete should be (false)

  }

  "Get method" should "return Failure if a future was equal null " in {
      

      val task = Task()

      task.cancel
      task.isRunning should be (false)

  }

  "Get method" should "return Failure if a future was None " in {
      

      val task = Task()

      task.cancel
      task.isRunning should be (false)

  }

}