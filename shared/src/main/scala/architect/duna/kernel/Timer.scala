package architect
package duna
package kernel

case class Timer(){

  def elapsedTime[R](block: =>  R): ProcessingTime[R] = {  
      val t0 = System.nanoTime()
      val result = block    
      val t1 = System.nanoTime()

      ProcessingTime((t1 - t0), result)
      
  }

}
