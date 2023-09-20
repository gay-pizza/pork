package gay.pizza.pork.buildext.ast

import gay.pizza.pork.buildext.codegen.KotlinClass
import gay.pizza.pork.buildext.codegen.KotlinFunction
import gay.pizza.pork.buildext.codegen.KotlinParameter
import gay.pizza.pork.buildext.codegen.KotlinWriter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

class AstPorkIdeaCodegen(pkg: String, outputDirectory: Path, world: AstWorld) :
  AstCodegenShared(pkg, outputDirectory, world) {
  companion object {
    fun run(pkg: String, world: AstWorld, outputDirectory: Path) {
      if (!outputDirectory.exists()) {
        outputDirectory.createDirectories()
      }
      val codegen = AstPorkIdeaCodegen(pkg, outputDirectory, world)
      codegen.runUntilCompletion()
    }
  }

  override suspend fun generate(): Unit = coroutineScope {
    deleteAllContents()
    launch { writePorkElement() }
    launch { writeNamedElement() }
    for (type in world.typeRegistry.types) {
      launch { writePsiElement(type) }
    }
    launch { writeElementFactory() }
  }

  fun writePorkElement() {
    val baseClass = KotlinClass(
      pkg,
      "PorkElement",
      isAbstract = true,
      constructorParameters = mutableMapOf(
        "node" to "ASTNode"
      ),
      inherits = mutableListOf(
        "ASTWrapperPsiElement(node)"
      ),
      imports = mutableListOf(
        "com.intellij.extapi.psi.ASTWrapperPsiElement",
        "com.intellij.lang.ASTNode"
      )
    )

    write("PorkElement.kt", KotlinWriter(baseClass))
  }

  fun writePsiElement(type: AstType) {
    val role = world.typeRegistry.roleOfType(type)

    if (role != AstTypeRole.AstNode) {
      return
    }

    val baseType =
      if (type.namedElementValue != null) "PorkNamedElement" else "PorkElement"

    val kotlinClass = KotlinClass(
      pkg,
      "${type.name}Element",
      constructorParameters = mutableMapOf(
        "node" to "ASTNode"
      ),
      inherits = mutableListOf("${baseType}(node)"),
      imports = mutableListOf(
        "com.intellij.lang.ASTNode"
      )
    )

    if (baseType == "PorkNamedElement") {
      kotlinClass.imports.add(0, "com.intellij.psi.PsiElement")
      kotlinClass.imports.add("gay.pizza.pork.ast.NodeType")
      kotlinClass.imports.add("gay.pizza.pork.idea.PorkElementTypes")
      val getNameFunction = KotlinFunction(
        "getName",
        overridden = true,
        returnType = "String?",
        isImmediateExpression = true
      )
      getNameFunction.body.add("node.findChildByType(PorkElementTypes.elementTypeFor(NodeType.${type.name}))?.text")
      kotlinClass.functions.add(getNameFunction)

      val setNameFunction = KotlinFunction(
        "setName",
        overridden = true,
        returnType = "PsiElement",
        isImmediateExpression = true,
        parameters = mutableListOf(
          KotlinParameter("name", "String")
        )
      )
      setNameFunction.body.add("this")
      kotlinClass.functions.add(setNameFunction)
    }

    write("${type.name}Element.kt", KotlinWriter(kotlinClass))
  }

  fun writeElementFactory() {
    val kotlinClass = KotlinClass(
      pkg,
      "PorkElementFactory",
      isObject = true,
      imports = mutableListOf(
        "com.intellij.extapi.psi.ASTWrapperPsiElement",
        "com.intellij.lang.ASTNode",
        "com.intellij.psi.PsiElement",
        "gay.pizza.pork.ast.NodeType",
        "gay.pizza.pork.idea.PorkElementTypes"
      )
    )

    val createFunction = KotlinFunction(
      "create",
      returnType = "PsiElement",
      parameters = mutableListOf(
        KotlinParameter("node", "ASTNode")
      ),
      isImmediateExpression = true
    )

    createFunction.body.add("when (PorkElementTypes.nodeTypeFor(node.elementType)) {")
    for (type in world.typeRegistry.types) {
      val role = world.typeRegistry.roleOfType(type)

      if (role != AstTypeRole.AstNode) {
        continue
      }

      createFunction.body.add("  NodeType.${type.name} -> ${type.name}Element(node)")
    }
    createFunction.body.add("  else -> ASTWrapperPsiElement(node)")
    createFunction.body.add("}")
    kotlinClass.functions.add(createFunction)

    write("PorkElementFactory.kt", KotlinWriter(kotlinClass))
  }

  fun writeNamedElement() {
    val kotlinClass = KotlinClass(
      pkg,
      "PorkNamedElement",
      isAbstract = true,
      constructorParameters = mutableMapOf(
        "node" to "ASTNode"
      ),
      inherits = mutableListOf(
        "PorkElement(node)",
        "PsiNamedElement"
      ),
      imports = mutableListOf(
        "com.intellij.lang.ASTNode",
        "com.intellij.psi.PsiNamedElement"
      )
    )

    write("PorkNamedElement.kt", KotlinWriter(kotlinClass))
  }
}
