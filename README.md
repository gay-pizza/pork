# pork

A work-in-progress programming language.

```pork
/* fibonacci sequence */
func fib(n) {
  if n == 0
    then 0
  else if n == 1
    then 1
  else fib(n - 1) + fib(n - 2)
}

func main() {
  let result = fib(20)
  println(result)
}
```

## Usage

```
./gradlew -q tool:run --args 'run ../examples/fib.pork'
```
