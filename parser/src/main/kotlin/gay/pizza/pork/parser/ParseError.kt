package gay.pizza.pork.parser

open class ParseError(val error: String) : RuntimeException(error)
