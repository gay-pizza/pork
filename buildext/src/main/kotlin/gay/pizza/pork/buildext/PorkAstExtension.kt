package gay.pizza.pork.buildext

import org.gradle.api.provider.Property

interface PorkAstExtension {
  val astCodegenType: Property<AstCodegenType>
}
