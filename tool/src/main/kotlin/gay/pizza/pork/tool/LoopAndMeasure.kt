package gay.pizza.pork.tool

import kotlin.system.measureNanoTime

fun maybeLoopAndMeasure(loop: Boolean, measure: Boolean, block: () -> Unit) {
  fun withMaybeMeasurement() {
    if (measure) {
      val nanos = measureNanoTime(block)
      val millis = nanos / 1000000.0
      System.err.println("time taken: $millis ms (${nanos} ns)")
    } else {
      block()
    }
  }

  if (loop) {
    while (true) {
      withMaybeMeasurement()
    }
  } else {
    withMaybeMeasurement()
  }
}
