

package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{ Var, StateManager }
/**
class VarPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  property("OnComplete should return the last written value.") {
    forAll("queueSize", "arraySize") { (queueSize: Int, size: Int) =>

      implicit val stateManager = StateManager()

      val s = Var(0, size)

      for(i <- 0 to s.cacheSize){

        s := i

      }

      s.onComplete{value => value should be (s.cacheSize)} 
      stateManager.stop()
    
    }

  }

}*/