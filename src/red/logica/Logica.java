package red.logica;

import net.datastructures.*;
import red.modelo.Conexion;
import red.modelo.Equipo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Clase encargada de la lógica de negocio de la red de computadoras.
 * Maneja el grafo principal y la ejecución de algoritmos de análisis de red.
 */
public class Logica {

    private Graph<Equipo, Conexion> red;
    private HashMap<String, Vertex<Equipo>> vertices;

    /**
     * Constructor que inicializa el grafo principal y carga los datos de equipos y conexiones.
     *
     * @param equipos Mapa ordenado de equipos donde la clave es el ID y el valor es el objeto Equipo.
     * @param conexiones Lista de objetos Conexion que definen las aristas del grafo.
     * Complejidad Temporal: O(V + E), donde V es el número de equipos y E el número de conexiones.
     */
    public Logica(TreeMap<String, Equipo> equipos, List<Conexion> conexiones) {
        red = new AdjacencyMapGraph<>(false);
        vertices = new HashMap<>();

        for (Equipo equipo : equipos.values()) {
            Vertex<Equipo> v = red.insertVertex(equipo);
            vertices.put(equipo.getId(), v);
        }

        for (Conexion con : conexiones) {
            Vertex<Equipo> v1 = vertices.get(con.getSource().getId());
            Vertex<Equipo> v2 = vertices.get(con.getTarget().getId());

            if (v1 != null && v2 != null) {
                if (red.getEdge(v1, v2) == null) {
                    red.insertEdge(v1, v2, con);
                }
            }
        }
    }

    /**
     * Verifica si un equipo con una dirección IP específica existe y se encuentra activo en la red.
     *
     * @param ip Dirección IP del equipo a verificar.
     * @return true si el equipo existe y su estado es activo, false en caso contrario.
     * Complejidad Temporal: O(V), ya que en el peor de los casos se recorren todos los vértices del grafo.
     */
    public boolean ping(String ip) {
        for (Vertex<Equipo> v : red.vertices()) {
            if (v.getElement().getIpAddress().equals(ip)) {
                return v.getElement().isStatus();
            }
        }
        return false;
    }

    /**
     * Calcula el camino óptimo (menor latencia) entre dos equipos utilizando el algoritmo de Dijkstra.
     * Se consideran únicamente los nodos y conexiones que están activos.
     *
     * @param idOrigen Identificador del equipo de origen.
     * @param idDestino Identificador del equipo de destino.
     * @return Una lista posicional de vértices que representa la ruta desde el origen hasta el destino.
     * @throws IllegalArgumentException Si alguno de los equipos no existe o no está activo en el grafo adaptado.
     * Complejidad Temporal: O(E log V), dominada por la ejecución del algoritmo de Dijkstra.
     */
    public PositionalList<Vertex<Equipo>> traceroute(String idOrigen, String idDestino) {

        PositionalList<Vertex<Equipo>> camino;

        Graph<Equipo, Integer> grafoActivo = crearGrafoActivo();

        Vertex<Equipo> origenNode = null;
        Vertex<Equipo> destinoNode = null;

        for (Vertex<Equipo> v : grafoActivo.vertices()) {
            String id = v.getElement().getId();
            if (id.equals(idOrigen)) {
                origenNode = v;
            }
            if (id.equals(idDestino)) {
                destinoNode = v;
            }
        }

        if (origenNode == null || destinoNode == null) {
            throw new IllegalArgumentException("Uno o ambos equipos no se encuentran activos o no existen en la red.");
        }

        camino = GraphAlgorithms.shortestPathList(grafoActivo, origenNode, destinoNode);

        return camino;
    }

    /**
     * Calcula el Árbol de Expansión Mínima (MST) de la red activa basándose en la latencia de las conexiones.
     * Utiliza el algoritmo de Prim-Jarnik o Kruskal según la implementación subyacente.
     *
     * @return Una lista de cadenas de texto formateadas describiendo las conexiones del MST y sus latencias.
     * Complejidad Temporal: O(E log E) o O(E log V), dependiendo de la implementación específica del algoritmo de MST.
     */
    public List<String> MST() {
        Graph<Equipo, Integer> grafoActivo = crearGrafoActivo();
        PositionalList<Edge<Integer>> mstEdges = GraphAlgorithms.MST(grafoActivo);

        List<String> resultado = new ArrayList<>();
        for (Edge<Integer> e : mstEdges) {
            Vertex<Equipo>[] endpoints = grafoActivo.endVertices(e);
            String linea = endpoints[0].getElement().getId() + " <--> " +
                    endpoints[1].getElement().getId() + " [Latencia: " + e.getElement() + " ms]\n";
            resultado.add(linea);
        }
        return resultado;
    }

    /**
     * Crea y retorna una copia del grafo original que incluye únicamente los equipos y conexiones activos.
     * Las aristas del nuevo grafo utilizan la latencia (Integer) como peso para los algoritmos.
     *
     * @return Un grafo no dirigido con los elementos activos de la red.
     * Complejidad Temporal: O(V + E), requerida para recorrer y filtrar todos los vértices y aristas del grafo original.
     */
    private Graph<Equipo, Integer> crearGrafoActivo() {
        Graph<Equipo, Integer> grafoActivo = new AdjacencyMapGraph<>(false);
        HashMap<String, Vertex<Equipo>> mapaActivos = new HashMap<>();

        for (Vertex<Equipo> v : red.vertices()) {
            if (v.getElement().isStatus()) {
                Vertex<Equipo> nuevoV = grafoActivo.insertVertex(v.getElement());
                mapaActivos.put(v.getElement().getId(), nuevoV);
            }
        }

        for (Edge<Conexion> e : red.edges()) {
            Conexion c = e.getElement();
            if (c.isStatus()) {
                Vertex<Equipo> v1 = mapaActivos.get(c.getSource().getId());
                Vertex<Equipo> v2 = mapaActivos.get(c.getTarget().getId());

                if (v1 != null && v2 != null) {
                    grafoActivo.insertEdge(v1, v2, c.getLatencia());
                }
            }
        }
        return grafoActivo;
    }
}