package com.yeito.tpoprogra.service;

import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GrafoService {


    private final com.yeito.tpoprogra.repository.CiudadRepository CiudadRepository;
    private final com.yeito.tpoprogra.repository.RutaRepository RutaRepository;
    private final com.yeito.tpoprogra.service.NeoService NeoService;
    private final Map<String, Map<String, Double>> grafo;


    public GrafoService(CiudadRepository CiudadRepository , RutaRepository RutaRepository, NeoService NeoService) {
        this.CiudadRepository = CiudadRepository;
        this.RutaRepository = RutaRepository;
        this.NeoService = NeoService;
        this.grafo =  NeoService.importarGrafo();

    }


    public  void recargarGrafo() {
        grafo.clear();
        grafo.putAll(NeoService.importarGrafo());
        System.out.println("♻️ Grafo recargado desde Neo4j (" + grafo.size() + " ciudades)");
    }


    // --- BFS (Breadth-First Search) ---
    public List<String> bfs(String inicio) {
        if (!grafo.containsKey(inicio)) {
            throw new IllegalArgumentException("La ciudad '" + inicio + "' no existe en el grafo.");
        }

        List<String> recorrido = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();

        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            recorrido.add(actual);

            Map<String, Double> vecinos = grafo.getOrDefault(actual, new HashMap<>());
            for (String vecino : vecinos.keySet()) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }

        return recorrido;
    }

    // --- DFS (Depth-First Search) ---
    public List<String> dfs(String inicio) {
        if (!grafo.containsKey(inicio)) {
            throw new IllegalArgumentException("La ciudad '" + inicio + "' no existe en el grafo.");
        }

        List<String> recorrido = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        dfsRecursivo(inicio, visitados, recorrido);
        return recorrido;
    }

    private void dfsRecursivo(String actual, Set<String> visitados, List<String> recorrido) {
        visitados.add(actual);
        recorrido.add(actual);

        Map<String, Double> vecinos = grafo.getOrDefault(actual, new HashMap<>());
        for (String vecino : vecinos.keySet()) {
            if (!visitados.contains(vecino)) {
                dfsRecursivo(vecino, visitados, recorrido);
            }
        }
    }

    // --- Mostrar resumen del grafo ---
    public  String resumenGrafo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo cargado:\n");
        for (String ciudad : grafo.keySet()) {
            sb.append(" - ").append(ciudad).append(" conecta con: ").append(grafo.get(ciudad).keySet()).append("\n");
        }
        return sb.toString();
    }

    public Map<String, Object> dijkstra(String origen, String destino) {
        Map<String, Map<String, Double>> grafo = this.grafo;

        if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) {
            throw new IllegalArgumentException("Una o ambas ciudades no existen en el grafo.");
        }

        // Inicialización
        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        PriorityQueue<String> cola = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        for (String ciudad : grafo.keySet()) {
            distancias.put(ciudad, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);
        cola.add(origen);

        // Dijkstra
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (!visitados.add(actual)) continue;

            if (actual.equals(destino)) break; // llegamos al destino

            Map<String, Double> vecinos = grafo.getOrDefault(actual, new HashMap<>());
            for (var entry : vecinos.entrySet()) {
                String vecino = entry.getKey();
                double peso = entry.getValue();
                double nuevaDistancia = distancias.get(actual) + peso;

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }

        // Reconstruir el camino
        List<String> camino = new ArrayList<>();
        String paso = destino;
        while (paso != null) {
            camino.add(paso);
            paso = predecesores.get(paso);
        }
        Collections.reverse(camino);

        // Si no hay ruta
        if (distancias.get(destino) == Double.POSITIVE_INFINITY) {
            return Map.of(
                    "ruta", List.of("No hay ruta entre " + origen + " y " + destino),
                    "distanciaTotal", "Inalcanzable"
            );
        }

        // Retornar ruta + distancia total
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("origen", origen);
        resultado.put("destino", destino);
        resultado.put("ruta", camino);
        resultado.put("distanciaTotal", distancias.get(destino));

        return resultado;
    }

    public Map<String, Object> prim(String inicio) {
        Map<String, Map<String, Double>> grafo = this.grafo;

        if (!grafo.containsKey(inicio)) {
            throw new IllegalArgumentException("La ciudad inicial no existe en el grafo.");
        }

        Set<String> visitados = new HashSet<>();
        List<String[]> aristas = new ArrayList<>();
        double pesoTotal = 0.0;

        // Cola de prioridad de aristas (origen, destino, peso)
        PriorityQueue<String[]> cola = new PriorityQueue<>(Comparator.comparingDouble(a -> Double.parseDouble(a[2])));

        visitados.add(inicio);
        for (var entry : grafo.get(inicio).entrySet()) {
            cola.add(new String[]{inicio, entry.getKey(), entry.getValue().toString()});
        }

        while (!cola.isEmpty() && visitados.size() < grafo.size()) {
            String[] arista = cola.poll();
            String origen = arista[0];
            String destino = arista[1];
            double peso = Double.parseDouble(arista[2]);

            if (visitados.contains(destino)) continue;

            visitados.add(destino);
            aristas.add(arista);
            pesoTotal += peso;

            for (var entry : grafo.get(destino).entrySet()) {
                if (!visitados.contains(entry.getKey())) {
                    cola.add(new String[]{destino, entry.getKey(), entry.getValue().toString()});
                }
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("nodosVisitados", visitados);
        resultado.put("aristas", aristas);
        resultado.put("pesoTotal", pesoTotal);
        return resultado;
    }

    public Map<String, Object> kruskal() {
        Map<String, Map<String, Double>> grafo = this.grafo;
        List<Edge> aristas = new ArrayList<>();

        // Recolectar todas las aristas (una sola vez por par)
        for (var origen : grafo.keySet()) {
            for (var entry : grafo.get(origen).entrySet()) {
                if (origen.compareTo(entry.getKey()) < 0) {
                    aristas.add(new Edge(origen, entry.getKey(), entry.getValue()));
                }
            }
        }

        // Ordenar por peso
        aristas.sort(Comparator.comparingDouble(a -> a.peso));

        // Estructura Union-Find para evitar ciclos
        Map<String, String> padre = new HashMap<>();
        for (String nodo : grafo.keySet()) padre.put(nodo, nodo);

        List<Edge> mst = new ArrayList<>();
        double pesoTotal = 0.0;

        for (Edge e : aristas) {
            String raizA = find(padre, e.origen);
            String raizB = find(padre, e.destino);
            if (!raizA.equals(raizB)) {
                mst.add(e);
                pesoTotal += e.peso;
                padre.put(raizA, raizB);
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("aristas", mst);
        resultado.put("pesoTotal", pesoTotal);
        return resultado;
    }

    private String find(Map<String, String> padre, String nodo) {
        if (!padre.get(nodo).equals(nodo)) {
            padre.put(nodo, find(padre, padre.get(nodo)));
        }
        return padre.get(nodo);
    }

    private static class Edge {
        String origen;
        String destino;
        double peso;
        Edge(String o, String d, double p) { this.origen = o; this.destino = d; this.peso = p; }

        @Override
        public String toString() {
            return "[" + origen + " - " + destino + " (" + peso + " km)]";
        }
    }
}