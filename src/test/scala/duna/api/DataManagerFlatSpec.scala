

package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.DataManager

class DataManagerFlatSpec extends FlatSpec with Matchers{

  "DataManager" should "alway has a value." in {
    
      val dataManager: DataManager[Int, Int] = DataManager(0, 0)
      
      dataManager.read should be (0)
    
    

  }

}