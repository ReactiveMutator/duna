
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{ Var, StateManager }

class VarFlatSpec extends FlatSpec with Matchers{

  "Read Var" should "return 4." in {
    
    implicit val stateManager = StateManager()
   
    val s = Var(4)
    s.now should be (4)  

    stateManager.stop()

  }

  "Read Var" should "return 2." in {
    
    implicit val stateManager = StateManager()
      
    val s = Var(1)

    s := 2  

    s.now should be (2) 
    
    stateManager.stop()

  }

  "When the Queue is longer then the Array, onComplete" should "return the last written value." in {
    
    implicit val stateManager = StateManager()

      val s = Var(1)

      for(i <- 0 to 100){

        s := i
  
      }

      s.onComplete{value => value should be (100)} 
    
    stateManager.stop()

  }

  "When the Queue is less then the Array, onComplete" should "return the last written value." in {
    
    implicit val stateManager = StateManager()

    val s = Var(1, 10)

    for(i <- 0 to 100){

      s := i

    }

    s.onComplete{value => value should be (100)} 
    
    stateManager.stop()

  }


  "When the Queue is more then the Array, onChange" should "pass through all values." in {
    
   implicit val stateManager = StateManager()

    var count = 0
    
    val s = Var(1, 1000)

    for(i <- 1 to 100){

      s := i

    }

    s.onChange{value => count += value}
    s.onComplete(a => count should be (5050))
    
    stateManager.stop()

  }

}