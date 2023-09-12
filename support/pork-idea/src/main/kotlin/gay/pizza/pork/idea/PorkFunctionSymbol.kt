package gay.pizza.pork.idea

import com.intellij.model.Pointer
import com.intellij.model.Symbol

@Suppress("UnstableApiUsage")
data class PorkFunctionSymbol(val id: String) : Symbol {
  override fun createPointer(): Pointer<out Symbol> {
    return Pointer { this }
  }
}
