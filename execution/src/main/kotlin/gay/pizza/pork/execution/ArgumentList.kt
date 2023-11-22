package gay.pizza.pork.execution

typealias ArgumentList = List<Any>

inline fun <reified T> ArgumentList.at(index: Int): T = this[index] as T
