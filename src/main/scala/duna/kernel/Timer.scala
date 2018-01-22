package architect
package duna
package kernel

case class Timer(){

  def elapsedTime[R](block: => R): (R, Float) = {  
      val t0 = System.nanoTime()
      val result = block    
      val t1 = System.nanoTime()

      (result, (t1 - t0).toFloat)
      
  }

}

