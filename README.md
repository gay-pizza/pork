# pork

A small BBQ language.

Very WIP. Like VERY.

```pork
/* fibonacci sequence */
fib = { n in
  if n == 0
    then 0
  else if n == 1
    then 1
  else fib(n - 1) + fib(n - 2)
}

result = fib(20)
println(result)
```

## Usage

```
./gradlew -q run --args 'run examples/fib.pork'
```
