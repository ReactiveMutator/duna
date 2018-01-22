
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.db.{ Var, StateManager }

class VarFlatSpec extends FlatSpec with Matchers{

  "Read Var" should "return Some(4)." in {
    
    implicit val stateManager = StateManager()
   
    val s = Var(4, 2)
    s() should be (Some(4))  

    stateManager.stop()

  }

  "Read Var" should "return Some(2)." in {
    
    implicit val stateManager = StateManager()
    
    
    val s = Var(1, 2)

    s := 2  

    s() should be (Some(2)) 
    
    stateManager.stop()

  }

  "When the Queue is longer then the Array, read Var" should "return Some(6). " in {
    
    implicit val stateManager = StateManager()
    
    for(i <- 0 to 100){
      val s = Var(1, 8)

      s := 2 
      s := 3 
      s := 4 
      s := 5 
      s := 6 

      s() should be (Some(6)) 
    }

    stateManager.stop()

  }

}