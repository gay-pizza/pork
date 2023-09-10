package gay.pizza.pork.buildext.ast

import kotlin.io.path.Path

object RunCodegenIde {
  @JvmStatic
  fun main(args: Array<String>) {
    val world = AstWorld.read(Path("src/main/ast/pork.yml"))
    AstCodegen.run(
      pkg = "gay.pizza.pork.ast",
      world = world,
      outputDirectory = Path("src/main/kotlin/gay/pizza/pork/ast")
    )
  }
}
