
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.db.{ Var, StateManager }
/**
class VarPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  property("Read Var should always return the last written value.") {
    forAll("queueSize", "arraySize") { (queueSize: Int, arraySize: Int) =>

      implicit val stateManager = StateManager()
      val size =  if(arraySize > 0 ){
                    arraySize
                  }else{
                    1
                  }

      val s = Var(0, queueSize)

      for(i <- 0 to size){

        s := i

      }

      s() should be (Some(size)) 
      stateManager.stop()
    
    }

  }

}*/