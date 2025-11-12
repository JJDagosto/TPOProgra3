package com.yeito.tpoprogra.controller;


import com.yeito.tpoprogra.model.Camion;
import com.yeito.tpoprogra.model.Ciudad;
import com.yeito.tpoprogra.model.Paquete;
import com.yeito.tpoprogra.model.Ruta;
import com.yeito.tpoprogra.repository.CamionRepository;
import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.repository.PaqueteRepository;
import com.yeito.tpoprogra.service.NeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yeito.tpoprogra.service.GrafoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @GetMapping("/paquetes/sort")
    public List<Paquete> quickSortPaquetes() {
        List<Paquete> paquetes = neoService.getPaquetes();
        grafoService.quickSortPaquetes(paquetes, 0, paquetes.size() - 1);
        return paquetes;
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


    @PostMapping("/camion/cargar")
    public String cargarCamion(@RequestParam Long camionId) {
        Optional<Camion> camionOpt = camionRepository.findById(camionId);
        if(camionOpt.isEmpty()) return "❌ Camión no encontrado";

        Camion camion = camionOpt.get();
        List<Paquete> seleccionados = grafoService.cargarCamionOptimo(camion);
        camionRepository.save(camion);

        return "🚚 Camión cargado con " + seleccionados.size() + " paquetes ("
                + camion.getCargaActual() + "/" + camion.getCapacidad() + " kg)";
    }

    @GetMapping("/ciudades/all")
    public List<String> getCiudades() {
        return ciudadRepository.findAll()
                .stream()
                .map(Ciudad::getNombre)
                .toList();
    }

    @GetMapping("/ruta/distancia")
    public Double obtenerDistancia(
            @RequestParam String origen,
            @RequestParam String destino
    ) {
        Long idOrigen = neoService.obtenerIdPorNombre(origen);
        var ciudadOpt = ciudadRepository.findById(idOrigen);

        if (ciudadOpt.isEmpty()) return null;

        return ciudadOpt.get()
                .getRutas()
                .stream()
                .filter(r -> r.getDestino().getNombre().equals(destino))
                .map(Ruta::getDistancia)
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/ciudad/get/nombres")
    public List<String> getCiudadNombres() {
        return ciudadRepository.findAll().stream().map(Ciudad::getNombre).collect(Collectors.toList());
    }


}