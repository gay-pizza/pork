package gay.pizza.pork.parse

interface PeekableSource<T> {
  val currentIndex: Int
  fun next(): T
  fun peek(): T
}
