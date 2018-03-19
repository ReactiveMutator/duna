// # Timer
// ### Measures elapsed time of a Task

package duna
package kernel

// Simple utility class. I use here system's nanoTime to measure difference between start and end of a Task.
case class Timer(){

  def elapsedTime[R](block: =>  R): ProcessingTime[R] = {  
      val t0 = System.nanoTime()
      val result = block    
      val t1 = System.nanoTime()

      ProcessingTime((t1 - t0), result)
      
  }

}
