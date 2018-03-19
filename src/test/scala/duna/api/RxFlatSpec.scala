

package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{ Rx, Var, StateManager }

class RxFlatSpec extends FlatSpec with Matchers{
 // TODO: fix the test Make it meaningful
  "Rx sum" should "always be equal to certain values." in {
    implicit val stateManager = StateManager()

    for(i <- 0 to 100){
      val a = Var(1); val b = Var(2)
      val c = Rx[Int]{implicit rx =>  a() + b() }
      var result = true

      c.onChange(v => 
        if(v == 6 || v == 7 || v == 9 || v == 12){ result should be (true)}else{result = false; result should be (true)}
      )
      
      a := 4 //6

      b := 3 //7

      b := 5 //9

      a := 7 //11
    }
    

    stateManager.stop()
  }

      

  }