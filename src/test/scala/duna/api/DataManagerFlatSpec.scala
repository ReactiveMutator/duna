
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{DataManager }

class DataManagerFlatSpec extends FlatSpec with Matchers{

  "DataManager" should "read None if it is empty." in {
    
      val dataManager: DataManager[Int, Int] = DataManager()
      
      dataManager.read should be (None)
    
    

  }

}