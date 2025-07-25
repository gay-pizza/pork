package gay.pizza.pork.idea

import com.intellij.lang.PsiBuilder
import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.ast.gen.NodeType
import gay.pizza.pork.parser.ParseError
import gay.pizza.pork.parser.ParserNodeAttribution
import gay.pizza.pork.tokenizer.ExpectedTokenError

class PsiBuilderMarkAttribution(val builder: PsiBuilder) : ParserNodeAttribution() {
  override fun <T : Node> produce(type: NodeType, block: () -> T): T {
    val marker = builder.mark()
    val result = try {
      val item = super.produce(type, block)
      marker.done(PorkElementTypes.elementTypeFor(item.type))
      item
    } catch (e: PsiBuilderTokenSource.BadCharacterError) {
      marker.error("Invalid character")
      while (!builder.eof()) {
        builder.advanceLexer()
      }
      throw PorkParser.ExitParser()
    } catch (e: ExpectedTokenError) {
      marker.error("${e.message}")
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
      marker.done(PorkElementTypes.FailedToParse)
      throw e
    }
    return result
  }
}
