package gay.pizza.pork.parse

interface PeekableSource<T> {
  val currentIndex: Int
  fun back()
  fun next(): T
  fun peek(): T
}
