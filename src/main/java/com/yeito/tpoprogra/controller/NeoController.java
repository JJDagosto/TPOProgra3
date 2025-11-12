
package com.yeito.tpoprogra.controller;


import com.yeito.tpoprogra.model.Camion;
import com.yeito.tpoprogra.model.Ciudad;
import com.yeito.tpoprogra.model.Paquete;
import com.yeito.tpoprogra.repository.CamionRepository;
import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.repository.PaqueteRepository;
import com.yeito.tpoprogra.service.NeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yeito.tpoprogra.service.GrafoService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/neo")
public class NeoController {



    @Autowired
    private NeoService neoService;
    @Autowired
    private GrafoService grafoService;
    @Autowired
    private PaqueteRepository paqueteRepository;
    @Autowired
    private CamionRepository camionRepository;
    @Autowired
    private CiudadRepository ciudadRepository;


    // --- CIUDADES ---

    @GetMapping("/ciudad/get")
    public List<Ciudad> getCiudad() {
        return ciudadRepository.findAll();
    }

    @GetMapping("/ciudad/get/nombres")
    public List<String> getCiudadNombres() {
        return ciudadRepository.findAll().stream().map(Ciudad::getNombre).collect(Collectors.toList());
    }

    @GetMapping("/camion/list")
    public List<Map<String, Object>> getCamiones() {
        List<Camion> camiones = camionRepository.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Camion camion : camiones) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", camion.getId());
            item.put("texto", camion.toString());
            resultado.add(item);
        }
        return resultado;
    }

    @PostMapping("/ciudad")
    public String crearCiudad(@RequestParam String nombre) {
        neoService.crearCiudad(nombre);
        return "✅ Ciudad creada: " + nombre;
    }

    @PutMapping("/ciudad")
    public String editarCiudad(@RequestParam String nombre, @RequestParam String newnombre) {
        neoService.editarCiudad(neoService.obtenerIdPorNombre(nombre), newnombre);
        return "✏️ Ciudad actualizada: " + nombre;
    }

    @DeleteMapping("/ciudad")
    public String eliminarCiudad(@RequestParam String nombre) {
        neoService.eliminarCiudad(neoService.obtenerIdPorNombre(nombre));
        return "🗑️ Ciudad eliminada: " + nombre;
    }

    // --- RUTAS ---

    @PostMapping("/ruta")
    public String crearRuta(@RequestParam String origen, @RequestParam String destino, @RequestParam double distancia) {
        long idorigen = neoService.obtenerIdPorNombre(origen);
        long iddestino = neoService.obtenerIdPorNombre(destino);

        neoService.crearRuta(idorigen, iddestino, distancia);
        neoService.crearRuta(iddestino, idorigen, distancia);

        return "✅ Rutas creada entre " + origen + " y " + destino;
    }

    @PutMapping("/ruta")
    public String editarRuta(@RequestParam Long destino, @RequestParam double distancia) {
        neoService.editarRuta(destino, distancia);
        return "✏️ Ruta actualizada entre "; // + origen + " y " + destino;
    }

    @DeleteMapping("/ruta")
    public String eliminarRuta(@RequestParam String origen, @RequestParam String destino) {
        neoService.eliminarRuta(neoService.obtenerIdRuta(origen, destino));
        neoService.eliminarRuta(neoService.obtenerIdRuta(destino, origen));
        return "🗑️ Ruta eliminada entre "; //+ origen + " y " + destino;
    }

    @GetMapping ("/ciudad/id")
    public Long obtenerIdCiudad(@RequestParam String nombre) {
        return neoService.obtenerIdPorNombre(nombre);
    }

    @GetMapping("/ruta/new")
    public String newRuta(@RequestParam String Origen, @RequestParam String Destino, @RequestParam double Distancia) {

        Long IDDestino = neoService.obtenerIdPorNombre(Destino);
        Long IDorigen = neoService.obtenerIdPorNombre(Origen);
        neoService.crearRuta(IDDestino, IDorigen, Distancia);
        neoService.crearRuta(IDorigen, IDDestino, Distancia);
        return "Ruta Creada correctamente";
    }

    @GetMapping ("/hola")
    public String hola() {
        return "Hola";
    }


    @GetMapping("/grafo/resumen")
    public String resumenGrafo() {
        return grafoService.resumenGrafo();
    }

    @GetMapping("/grafo/recargar")
    public String recargarGrafo() {
        grafoService.recargarGrafo();
        return "Grafo recargado correctamente.";
    }

    @GetMapping("/grafo/bfs")
    public List<String> bfs(@RequestParam String inicio) {
        return grafoService.bfs(inicio);
    }

    @GetMapping("/grafo/dfs")
    public List<String> dfs(@RequestParam String inicio) {
        return grafoService.dfs(inicio);
    }

    @GetMapping("/grafo/dijkstra")
    public Map<String, Object> dijkstra(@RequestParam String origen, @RequestParam String destino) {
        return grafoService.dijkstra(origen, destino);
    }

    @GetMapping("/grafo/prim")
    public Map<String, Object> prim(@RequestParam String inicio) {
        return grafoService.prim(inicio);
    }

    @GetMapping("/grafo/kruskal")
    public Map<String, Object> kruskal() {
        return grafoService.kruskal();
    }

    @PostMapping("/paquete")
    public String crearPaquete(
            @RequestParam double peso,
            @RequestParam String origen,
            @RequestParam String destino
    ) {
        Optional<Ciudad> cOrigenOpt = ciudadRepository.findByNombre(origen);
        Optional<Ciudad> cDestinoOpt = ciudadRepository.findByNombre(destino);

        if (cOrigenOpt.isEmpty() || cDestinoOpt.isEmpty()) {
            return "❌ Origen o destino inválido";
        }

        Ciudad cOrigen = cOrigenOpt.get();
        Ciudad cDestino = cDestinoOpt.get();


        if (cOrigen == null || cDestino == null) {
            return "❌ Origen o destino inválido";
        }

        Paquete paquete = new Paquete(peso, cOrigen, cDestino);
        paqueteRepository.save(paquete);
        return "📦 Paquete creado: " + peso + " kg de " + origen + " a " + destino;
    }

    // Eliminar paquete
    @DeleteMapping("/paquete/{id}")
    public String eliminarPaquete(@PathVariable Long id) {
        if (!paqueteRepository.existsById(id)) return "❌ Paquete no encontrado";
        paqueteRepository.deleteById(id);
        return "🗑️ Paquete eliminado correctamente";
    }

    @GetMapping("/paquetes/all")
    public List<Paquete> getPaquetes() {
        return neoService.getPaquetes();
    }

    @PostMapping("/paquetes/sort")
    public List<Paquete> quickSortPaquetes() {
        List<Paquete> paquetes = neoService.getPaquetes();
        grafoService.quickSortPaquetes(paquetes, 0, paquetes.size() - 1);
        return paquetes;
    }

    @GetMapping("/paquetes/sort")
    public Map<Long, Double> sortPaquetes() {
        List<Paquete> paquetes = neoService.getPaquetes();
        Map<Long, Double> paquetesMap = new LinkedHashMap<>();
        grafoService.quickSortPaquetes(paquetes, 0, paquetes.size() - 1);
        for (Paquete paquete : paquetes) {
            paquetesMap.put(paquete.getId(), paquete.getPeso());
        }
        return paquetesMap;
    }

    @PostMapping("/camion/cargar")
    public String cargarCamion(@RequestParam Long camionId) {
        Camion camion = camionRepository.findById(camionId).orElseThrow(() -> new RuntimeException("Camion no encontrado"));
        // Lógica de carga óptima (Knapsack)
        List<Paquete> seleccionados = grafoService.cargarCamionOptimo(camion);

        // Guardamos el camión con sus paquetes y destinos en Neo4j
        camionRepository.save(camion);

        for (Paquete p : seleccionados) {
            paqueteRepository.deleteById(p.getId());
        }

        // Mensaje de resumen
        return "🚚 Camión cargado con " + seleccionados.size() + " paquetes ("
                + camion.getCargaActual() + "/" + camion.getCapacidad() + " kg)";
    }

    // Crear un camión vacío
    @PostMapping("/camion")
    public String crearCamion(@RequestParam double capacidad) {
        Camion camion = new Camion(capacidad);
        camionRepository.save(camion);
        return "🚚 Camión creado con capacidad " + capacidad + " kg";
    }

    // Obtener todos los camiones
    @GetMapping("/camion")
    public List<Camion> obtenerCamiones() {
        return camionRepository.findAll();
    }
    // Eliminar camión
    @DeleteMapping("/camion/{id}")
    public String eliminarCamion(@PathVariable Long id) {
        if (!camionRepository.existsById(id)) return "❌ Camión no encontrado";
        camionRepository.deleteById(id);
        return "🗑️ Camión eliminado correctamente";
    }

    @GetMapping("/grafo/rutas")
    public List<List<String>> obtenerRutas(@RequestParam String origen, @RequestParam String destino) {
        return grafoService.encontrarTodasLasRutas(origen, destino);
    }

    @GetMapping("/grafo/optimizarCiudades")
    public GrafoService.ResultadoRuta optimizarCiudades(
            @RequestParam String inicio,
            @RequestParam double maxDistancia) {
        return grafoService.optimizarCiudades(inicio, maxDistancia);
    }


    @PutMapping("/camion/cargarManual")
    public String cargarCamionManual(@RequestBody Map<String, Object> data) {
        try {
            // 🔹 Forzamos conversión segura
            Long camionId = Long.valueOf(data.get("id").toString());

            // Buscamos el camión
            Camion camion = camionRepository.findById(camionId)
                    .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

            // 🔹 Convertimos los IDs de paquetes
            @SuppressWarnings("unchecked")
            List<Object> paqueteIdsRaw = (List<Object>) data.get("paquetes");

            List<Long> paqueteIds = paqueteIdsRaw.stream()
                    .map(Object::toString)
                    .map(Long::valueOf)
                    .toList();

            // Buscamos los paquetes y los agregamos
            List<Paquete> paquetes = new ArrayList<>();
            double pesoTotal = 0;
            for (Long id : paqueteIds) {
                Paquete p = neoService.getPaqueteById(id);
                if (p != null) {
                    paquetes.add(p);
                    pesoTotal += p.getPeso();
                }
            }

            camion.getPaquetes().addAll(paquetes);
            camion.setCargaActual(camion.getCargaActual() + pesoTotal);
            camionRepository.save(camion);

            // Eliminamos los paquetes cargados (opcional)
            for (Paquete p : paquetes) {
                neoService.deletePaquete(p.getId());
            }

            return "🚛 Camión " + camion.getId() + " cargado manualmente con " +
                    paquetes.size() + " paquetes (" +
                    camion.getCargaActual() + "/" + camion.getCapacidad() + " kg)";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error al cargar camión manual: " + e.getMessage();
        }
    }


    @GetMapping("/paquete/aTexto")
    public String paqueteATexto(@RequestParam Long ID) {
        Paquete paquete = paqueteRepository.findById(ID).orElse(null);
        return paquete.getPeso() + " | Origen: " + paquete.getOrigen().getNombre() + " -> Destino: " + paquete.getDestino().getNombre();
    }


}