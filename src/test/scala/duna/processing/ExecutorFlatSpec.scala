package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.processing.Executor

class ExecutorFlatSpec extends FlatSpec with Matchers{

  "IsShutdown" should "return true if a pool was shutted down." in {
      val executor: Executor = Executor()
      
      executor.close()

      (executor.isShutdown) should be (true)

  }
}