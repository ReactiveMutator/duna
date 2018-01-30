
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{ Rx, Var, StateManager }

class RxFlatSpec extends FlatSpec with Matchers{

  "Rx counter" should "return 28." in {
    
    implicit val stateManager = StateManager()
   
    val s = Var(4)
    val b = Var(1)
    val rx = Rx[Int]{implicit rx => s() + b()}

    var counter = 0    
    
    rx.onChange{value => counter = value; counter should be (value)}
    
    for(i <- 0 to 5){
        
        s := i

    }

    stateManager.stop()

    

  }
  }