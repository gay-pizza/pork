package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.FunctionDefinition

class FunctionContext(val slabContext: SlabContext, val node: FunctionDefinition) : CallableFunction {
  val name: String by lazy { "${slabContext.slab.location.commonFriendlyName} ${node.symbol.id}" }

  private fun resolveMaybeNative(): CallableFunction? = if (node.nativeFunctionDescriptor == null) {
    null
  } else {
    val native = node.nativeFunctionDescriptor!!
    val nativeFunctionProvider =
      slabContext.evaluator.nativeFunctionProvider(native.form.id)
    nativeFunctionProvider.provideNativeFunction(native.definitions.map { it.text }, node.arguments, slabContext)
  }

  private val nativeCached by lazy { resolveMaybeNative() }

  override fun call(arguments: ArgumentList, stack: CallStack): Any {
    if (nativeCached != null) {
      return nativeCached!!.call(arguments, stack)
    }

    val scope = slabContext.internalScope.fork(node.symbol.id)
    for ((index, spec) in node.arguments.withIndex()) {
      if (spec.multiple) {
        val list = arguments.subList(index, arguments.size - 1)
        scope.define(spec.symbol.id, list)
        break
      } else {
        scope.define(spec.symbol.id, arguments[index])
      }
    }

    if (node.block == null) {
      throw RuntimeException("Native or Block is required for FunctionDefinition")
    }

    val visitor = EvaluationVisitor(scope, stack)
    stack.push(this)
    val blockFunction = visitor.visitBlock(node.block!!)
    try {
      return blockFunction.call(true)
    } catch (e: PorkError) {
      throw e
    } catch (e: Exception) {
      val stackForError = stack.copy()
      throw PorkError(e, stackForError)
    } finally {
      scope.disown()
      stack.pop()
    }
  }
}
