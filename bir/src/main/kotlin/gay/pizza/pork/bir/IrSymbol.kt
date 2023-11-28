package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSymbol(val id: UInt, val tag: IrSymbolTag, val name: String? = null) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {}

  override fun equals(other: Any?): Boolean {
    if (other !is IrSymbol) return false
    return other.id == id && other.tag == tag
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + tag.hashCode()
    return result
  }

  val friendlyName: String
    get() = if (name != null) {
      "$id $tag $name"
    } else "$id $tag"
}
