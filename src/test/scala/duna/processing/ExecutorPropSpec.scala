

package duna

import org.scalatest._
import prop._
import Utils._
import duna.processing.Executor

class ExecutorPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("Executor should exec a task in a pool.") {
    forAll("number") { (number: Int) =>

      val executor: Executor = Executor(number)
          
      val task = () => 6
      val future = executor.submit(task)

      (future.get) should be (6)

      executor.close()
    
    }

  }

}