# pork

A small BBQ language.

Very WIP. Like VERY.

```pork
main = {
  three = 3
  two = 2
  calculateSimple = {
    (50 + three) * two
  }
  calculateComplex = {
    three + two + 50
  }
  calculateSimpleResult = calculateSimple()
  calculateComplexResult = calculateComplex()

  list = [10, 20, 30]
  trueValue = true
  falseValue = false

  [
    calculateSimpleResult,
    calculateComplexResult,
    list,
    trueValue,
    falseValue
  ]
}
```
