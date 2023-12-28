package gay.pizza.pork.bir

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class IrSymbolGraphSerializer : KSerializer<IrSymbolGraph> {
  private val serializer = ListSerializer(IrSymbolGraphEdge.serializer())
  override val descriptor: SerialDescriptor = serializer.descriptor

  override fun deserialize(decoder: Decoder): IrSymbolGraph {
    val graph = IrSymbolGraph()
    graph.buildFromEdges(serializer.deserialize(decoder))
    return graph
  }

  override fun serialize(encoder: Encoder, value: IrSymbolGraph) {
    val edges = mutableListOf<IrSymbolGraphEdge>()
    value.forEachEdge { user, owner ->
      edges.add(IrSymbolGraphEdge(user, owner))
    }
    serializer.serialize(encoder, edges)
  }
}
