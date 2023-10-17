package gay.pizza.pork.tokenizer

interface PeekableSource<T> {
  val currentIndex: Int
  fun next(): T
  fun peek(): T
}
