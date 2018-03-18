// # ProcessingTime
// ### Wrapper of processing time and a resulting value
package duna
package kernel

// The utility class for storing processing time of a Task.
// FIXME: Can I combine it with a Timer?
case class ProcessingTime[R](time: Long, result: R)