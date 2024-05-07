package at.hannibal2.skyhanni.data.model

import at.hannibal2.skyhanni.config.ConfigManager.Companion.registerTypeAdapter
import at.hannibal2.skyhanni.utils.LorenzUtils.round
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.fromJson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import java.util.PriorityQueue

@JvmInline
value class Graph(
    @Expose
    val graph: List<GraphNode>,
) : List<GraphNode> {
    override val size
        get() = graph.size

    override fun contains(element: GraphNode) = graph.contains(element)

    override fun containsAll(elements: Collection<GraphNode>) = graph.containsAll(elements)

    override fun get(index: Int) = graph.get(index)

    override fun isEmpty() = graph.isEmpty()

    override fun indexOf(element: GraphNode) = graph.indexOf(element)

    override fun iterator(): Iterator<GraphNode> = graph.iterator()
    override fun listIterator() = graph.listIterator()

    override fun listIterator(index: Int) = graph.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = graph.subList(fromIndex, toIndex)

    override fun lastIndexOf(element: GraphNode) = graph.lastIndexOf(element)

}

class GraphNode(val id: Int, val position: LorenzVec, val name: String? = null) {

    /** Keys are the neighbours and value the edge weight (e.g. Distance) */
    lateinit var neighbours: Map<GraphNode, Double>

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphNode

        if (id != other.id) return false

        return true
    }
}

fun Graph.findShortestPathAsGraph(start: GraphNode, end: GraphNode): Graph {
    val distances = mutableMapOf<GraphNode, Double>()
    val previous = mutableMapOf<GraphNode, GraphNode>()
    val visited = mutableSetOf<GraphNode>()
    val queue = PriorityQueue<GraphNode>(compareBy { distances.getOrDefault(it, Double.MAX_VALUE) })

    distances[start] = 0.0
    queue.add(start)

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current == end) break

        visited.add(current)

        current.neighbours.forEach { (neighbour, weight) ->
            if (neighbour !in visited) {
                val newDistance = distances.getValue(current) + weight
                if (newDistance < distances.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    distances[neighbour] = newDistance
                    previous[neighbour] = current
                    queue.add(neighbour)
                }
            }
        }
    }

    return Graph(buildList {
        var current = end
        while (current != start) {
            add(current)
            current = previous[current] ?: return Graph(emptyList())
        }
        add(start)
    }.reversed())
}

fun Graph.findShortestPath(start: GraphNode, end: GraphNode): List<LorenzVec> =
    this.findShortestPathAsGraph(start, end).toPositionsList()

fun Graph.toPositionsList() = this.map { it.position }

private val localeGson = GsonBuilder().setPrettyPrinting()
    /* ConfigManager.createBaseGsonBuilder() */.registerTypeAdapter<Graph>({ out, value ->
        out.beginObject()
        value.forEach {
            out.name(it.id.toString()).beginObject()
            out.name("Position").value(with(it.position) { "$x:$y:$z" })
            if (it.name != null) {
                out.name("Name").value(it.name)
            }
            out.name("Neighbours")
            out.beginObject()
            it.neighbours.forEach { (node, weight) ->
                val id = node.id.toString()
                out.name(id).value(weight.round(2))
            }
            out.endObject()
            out.endObject()
        }
        out.endObject()
    }, { reader ->
        reader.beginObject()
        val list = mutableListOf<GraphNode>()
        val neigbourMap = mutableMapOf<GraphNode, List<Pair<Int, Double>>>()
        while (reader.hasNext()) {
            val id = reader.nextName().toInt()
            reader.beginObject()
            var position: LorenzVec? = null
            var name: String? = null
            var neighbors = mutableListOf<Pair<Int, Double>>()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "Position" -> {
                        position = reader.nextString().split(":").let { parts ->
                            LorenzVec(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
                        }
                    }

                    "Neighbours" -> {
                        reader.beginObject()
                        while (reader.hasNext()) {
                            val nId = reader.nextName().toInt()
                            val distance = reader.nextDouble()
                            neighbors.add(nId to distance)
                        }
                        reader.endObject()
                    }

                    "Name" -> {
                        name = reader.nextString()
                    }

                }
            }
            val node = GraphNode(id, position!!, name)
            list.add(node)
            neigbourMap[node] = neighbors
            reader.endObject()
        }
        neigbourMap.forEach { (node, edge) ->
            node.neighbours = edge.associate { (id, distance) ->
                list.first { it.id == id } to distance
            }
        }
        reader.endObject()
        Graph(list)
    }).create()

fun Graph.toJson(): String = localeGson.toJson(this)

fun graphFromJson(json: String): Graph = localeGson.fromJson<Graph>(json)
fun graphFromJson(json: JsonElement): Graph = localeGson.fromJson<Graph>(json)