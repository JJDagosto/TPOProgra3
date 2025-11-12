package com.yeito.tpoprogra.service;

import com.yeito.tpoprogra.model.Camion;
import com.yeito.tpoprogra.model.Paquete;
import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.repository.PaqueteRepository;
import com.yeito.tpoprogra.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

@Service
public class GrafoService {


    private final com.yeito.tpoprogra.repository.CiudadRepository CiudadRepository;
    private final com.yeito.tpoprogra.repository.RutaRepository RutaRepository;
    private final com.yeito.tpoprogra.repository.PaqueteRepository PaqueteRepository;
    private final com.yeito.tpoprogra.service.NeoService NeoService;
    private final Map<String, Map<String, Double>> grafo;


    public GrafoService(CiudadRepository CiudadRepository, RutaRepository RutaRepository, NeoService NeoService, PaqueteRepository PaqueteRepository) {
        this.CiudadRepository = CiudadRepository;
        this.RutaRepository = RutaRepository;
        this.PaqueteRepository = PaqueteRepository;
        this.NeoService = NeoService;
        this.grafo = NeoService.importarGrafo();

    }


    public void recargarGrafo() {
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
    public String resumenGrafo() {
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
        // 1️⃣ Cargar el grafo actual
        Map<String, Map<String, Double>> grafo = this.grafo;

        // 2️⃣ Crear lista de aristas (usando la información del grafo)
        List<Map<String, Object>> aristas = new ArrayList<>();
        for (String origen : grafo.keySet()) {
            for (Map.Entry<String, Double> entry : grafo.get(origen).entrySet()) {
                String destino = entry.getKey();
                double peso = entry.getValue();
                if (origen.compareTo(destino) < 0) { // evitar duplicados
                    aristas.add(Map.of(
                            "origen", origen,
                            "destino", destino,
                            "peso", peso
                    ));
                }
            }
        }

        // 3️⃣ Ordenar por peso (distancia)
        aristas.sort(Comparator.comparingDouble(a -> (Double) a.get("peso")));

        // 4️⃣ Inicializar estructura Union-Find
        Map<String, String> padre = new HashMap<>();
        for (String nodo : grafo.keySet()) padre.put(nodo, nodo);

        // 5️⃣ Variables de salida
        List<Map<String, Object>> mst = new ArrayList<>();
        double pesoTotal = 0.0;

        // 6️⃣ Calcular Kruskal
        for (Map<String, Object> a : aristas) {
            String origen = (String) a.get("origen");
            String destino = (String) a.get("destino");
            double peso = (Double) a.get("peso");

            String raizA = find(padre, origen);
            String raizB = find(padre, destino);

            if (!raizA.equals(raizB)) {
                padre.put(raizA, raizB);
                mst.add(a);
                pesoTotal += peso;

                // 🌀 Aquí podrías "emitir" el progreso al front (WebSocket o SSE)
                // por ejemplo: enviar el JSON parcial con origen, destino, peso
            }
        }

        // 7️⃣ Resultado final
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("aristas", mst);
        resultado.put("pesoTotal", pesoTotal);
        resultado.put("nodosVisitados", grafo.keySet());
        return resultado;
    }

    private String find(Map<String, String> padre, String nodo) {
        if (!padre.get(nodo).equals(nodo)) {
            padre.put(nodo, find(padre, padre.get(nodo)));
        }
        return padre.get(nodo);
    }

    //Quicksort
    public void quickSortPaquetes(List<Paquete> paquetes, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionPaquetes(paquetes, low, high);
            quickSortPaquetes(paquetes, low, pivotIndex - 1);
            quickSortPaquetes(paquetes, pivotIndex + 1, high);
        }
    }

    private int partitionPaquetes(List<Paquete> paquetes, int low, int high) {
        double pivot = paquetes.get(high).getPeso(); // usamos el peso del último como pivote
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (paquetes.get(j).getPeso() <= pivot) {
                i++;
                Collections.swap(paquetes, i, j);
            }
        }

        Collections.swap(paquetes, i + 1, high);
        return i + 1;
    }

    //Knapsack (programacion dinamica)
    public List<Paquete> cargarCamionOptimo(Camion camion) {
        // Obtenés todos los paquetes desde la BD
        List<Paquete> paquetes = NeoService.getPaquetes();

        // Aplicás el algoritmo de mochila
        List<Paquete> seleccionados = resolverKnapsack(paquetes, camion.getCapacidad());

        // Cargás los paquetes seleccionados al camión
        for (Paquete p : seleccionados) {
            camion.agregarPaquete(p);
        }

        return seleccionados;
    }

    private List<Paquete> resolverKnapsack(List<Paquete> paquetes, double capacidad) {
        int n = paquetes.size();
        double[][] dp = new double[n + 1][(int) (capacidad + 1)];

        for (int i = 1; i <= n; i++) {
            double peso = paquetes.get(i - 1).getPeso();
            for (int w = 0; w <= capacidad; w++) {
                if (peso <= w)
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][(int) (w - peso)] + peso);
                else
                    dp[i][w] = dp[i - 1][w];
            }
        }

        List<Paquete> seleccionados = new ArrayList<>();
        double w = capacidad;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][(int) w] != dp[i - 1][(int) w]) {
                Paquete p = paquetes.get(i - 1);
                seleccionados.add(p);
                w -= p.getPeso();
            }
        }

        return seleccionados;
    }

    //Backtracking

    public List<List<String>> encontrarTodasLasRutas(String inicio, String destino) {
        List<List<String>> rutas = new ArrayList<>();
        List<String> rutaActual = new ArrayList<>();
        Set<String> visitados = new HashSet<>();

        backtrack(inicio, destino, visitados, rutaActual, rutas);

        return rutas;
    }

    private void backtrack(String actual, String destino, Set<String> visitados, List<String> rutaActual, List<List<String>> rutas) {
        visitados.add(actual);
        rutaActual.add(actual);

        if (actual.equals(destino)) {
            rutas.add(new ArrayList<>(rutaActual));
        } else {
            Map<String, Map<String, Double>> grafo = this.grafo;
            if (grafo.containsKey(actual)) {
                for (String vecino : grafo.get(actual).keySet()) {
                    if (!visitados.contains(vecino)) {
                        backtrack(vecino, destino, visitados, rutaActual, rutas);
                    }
                }
            }
        }

        rutaActual.remove(rutaActual.size() - 1);
        visitados.remove(actual);
    }

    //branch & bound

    public class ResultadoRuta {
        public List<String> mejorRuta;
        public int ciudadesVisitadas;
        public double distanciaTotal;

        public ResultadoRuta(List<String> mejorRuta, int ciudadesVisitadas, double distanciaTotal) {
            this.mejorRuta = mejorRuta;
            this.ciudadesVisitadas = ciudadesVisitadas;
            this.distanciaTotal = distanciaTotal;
        }
    }

    public ResultadoRuta optimizarCiudades(String inicio, double maxDistancia) {
        Map<String, Map<String, Double>> grafo = this.grafo;
        Set<String> visitadas = new HashSet<>();
        List<String> rutaActual = new ArrayList<>();
        ResultadoRuta mejor = new ResultadoRuta(new ArrayList<>(), 0, 0);

        branchAndBound(grafo, inicio, visitadas, rutaActual, 0, maxDistancia, mejor);
        return mejor;
    }

    private void branchAndBound(Map<String, Map<String, Double>> grafo, String actual,
                                Set<String> visitadas, List<String> rutaActual,
                                double distanciaActual, double maxDistancia, ResultadoRuta mejor) {

        visitadas.add(actual);
        rutaActual.add(actual);

        // Actualizar mejor solución
        if (rutaActual.size() > mejor.ciudadesVisitadas) {
            mejor.mejorRuta = new ArrayList<>(rutaActual);
            mejor.ciudadesVisitadas = rutaActual.size();
            mejor.distanciaTotal = distanciaActual;
        }

        // Recorrer vecinos
        if (grafo.containsKey(actual)) {
            for (var entry : grafo.get(actual).entrySet()) {
                String vecino = entry.getKey();
                double dist = entry.getValue();

                // Si todavía no lo visité y no me paso de distancia, pruebo esa rama
                if (!visitadas.contains(vecino) && distanciaActual + dist <= maxDistancia) {

                    // Bound: si ya no puedo mejorar, corto
                    int ciudadesPosibles = grafo.size() - visitadas.size();
                    if (rutaActual.size() + ciudadesPosibles <= mejor.ciudadesVisitadas)
                        continue;

                    branchAndBound(grafo, vecino, visitadas, rutaActual, distanciaActual + dist, maxDistancia, mejor);
                }
            }
        }

        rutaActual.remove(rutaActual.size() - 1);
        visitadas.remove(actual);
    }


}