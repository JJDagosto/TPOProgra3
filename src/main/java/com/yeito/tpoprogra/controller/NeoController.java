
package com.yeito.tpoprogra.controller;


import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.service.NeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yeito.tpoprogra.service.GrafoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/neo")
public class NeoController {



    @Autowired
    private NeoService neoService;
    @Autowired
    private GrafoService grafoService;

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

//    @GetMapping("/ciudad/new")
//    public String newCiudad(@RequestParam String nombre) {
//        neoService.crearCiudad(nombre);
//        return nombre+"Creada correctamente";
//    }

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


}