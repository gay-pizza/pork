package gay.pizza.pork.idea

import com.intellij.lang.PsiBuilder
import gay.pizza.pork.ast.Node
import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.parser.ParseError
import gay.pizza.pork.parser.ParserNodeAttribution
import java.util.IdentityHashMap

class PsiBuilderMarkAttribution(val builder: PsiBuilder) : ParserNodeAttribution() {
  private val map = IdentityHashMap<Node, Node>()

  override fun <T : Node> guarded(type: NodeType?, block: () -> T): T {
    val marker = builder.mark()
    val result = try {
      val item = super.guarded(type, block)
      marker.done(PorkElementTypes.elementTypeFor(item.type))
      item
    } catch (e: PsiBuilderTokenSource.BadCharacterError) {
      marker.error("Invalid character")
      while (!builder.eof()) {
        builder.advanceLexer()
      }
      throw PorkParser.ExitParser()
    } catch (e: ParseError) {
      marker.error(e.error)
      while (!builder.eof()) {
        builder.advanceLexer()
      }
      throw PorkParser.ExitParser()
    } catch (e: PorkParser.ExitParser) {
      if (e.error != null) {
        marker.error(e.error)
      } else {
        marker.done(PorkElementTypes.FailedToParse)
      }
      throw PorkParser.ExitParser()
    }
    if (map[result] != null) {
      marker.drop()
    }
    map[result] = result
    return result
  }
}
