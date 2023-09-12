package gay.pizza.pork.idea

import com.intellij.lang.PsiBuilder
import gay.pizza.pork.ast.Node
import gay.pizza.pork.parser.ParseError
import gay.pizza.pork.parser.ParserNodeAttribution

class PsiBuilderMarkAttribution(val builder: PsiBuilder) : ParserNodeAttribution() {
  override fun <T : Node> guarded(block: () -> T): T {
    val marker = builder.mark()
    try {
      val item = super.guarded(block)
      marker.done(PorkElementTypes.elementTypeFor(item.type))
      return item
    } catch (e: PsiBuilderTokenSource.BadCharacterError) {
      marker.error("Bad character.")
      while (!builder.eof()) {
        builder.advanceLexer()
      }
      throw PorkParser.ExitParser(e.error)
    } catch (e: ParseError) {
      while (!builder.eof()) {
        builder.advanceLexer()
      }
      marker.error(e.error)
      throw PorkParser.ExitParser(e.error)
    } catch (e: PorkParser.ExitParser) {
      marker.error(e.error)
      throw e
    }
  }
}
