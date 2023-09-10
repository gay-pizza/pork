# pork

A work-in-progress programming language.

```pork
/* fibonacci sequence */
func fib(n) {
  if n < 2 {
    n
  } else {
    fib(n - 1) + fib(n - 2)
  }
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
