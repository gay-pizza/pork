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
  }
}
