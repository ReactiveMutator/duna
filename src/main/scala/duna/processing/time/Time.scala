package architect
package duna
package processing 
package time

case class Time(value: Long = System.nanoTime(), timeFrame: Long = 0) {self =>

  def <(other: Time): Boolean = 
    self.value < other.value

  def compare(other: Time) = 
    self.value compare other.value

}

case class Interval(start: Time, end: Time){self =>

  def contain(time: Time): Boolean = 
    start.value < time.value && time.value > end.value


  def compare(other: Interval) = 
    start compare other.start

}
object IntervalOrdering extends Ordering[Interval] {

  def compare(a:Interval, b:Interval) = a compare b

}