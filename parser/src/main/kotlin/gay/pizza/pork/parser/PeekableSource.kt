package gay.pizza.pork.parser

interface PeekableSource<T> {
  val currentIndex: Int
  fun next(): T
  fun peek(): T
}
