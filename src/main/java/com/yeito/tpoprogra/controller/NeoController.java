
package com.yeito.tpoprogra.controller;


import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.service.NeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neo")
public class NeoController {



    @Autowired
    private NeoService neoService;

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
    public String crearRuta(@RequestParam long idorigen, @RequestParam long iddestino, @RequestParam double distancia) {
        neoService.crearRuta(idorigen, iddestino, distancia);
        return "✅ Ruta creada entre " + idorigen + " y " + iddestino;
    }

    @PutMapping("/ruta")
    public String editarRuta(@RequestParam Long destino, @RequestParam double distancia) {
        neoService.editarRuta(destino, distancia);
        return "✏️ Ruta actualizada entre "; // + origen + " y " + destino;
    }

    @DeleteMapping("/ruta")
    public String eliminarRuta(@RequestParam String origen, @RequestParam String destino) {
        neoService.eliminarRuta(neoService.obtenerIdRuta(origen, destino));
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



}

//@RestController
//@RequestMapping("/neo")
//public class NeoController {
//
//    @Autowired
//    private NeoService neoService;
//
//    // --- CIUDADES ---
//
//    @PostMapping("/ciudad")
//    public String crearCiudad(@RequestParam String nombre) {
//        neoService.crearCiudad(nombre);
//        return "✅ Ciudad creada: " + nombre;
//    }
//
//    @PutMapping("/ciudad")
//    public String editarCiudad(@RequestParam String nombreActual, @RequestParam String nuevoNombre) {
//        neoService.editarCiudadPorNombre(nombreActual, nuevoNombre);
//        return "✏️ Ciudad actualizada: " + nuevoNombre;
//    }
//
//    @DeleteMapping("/ciudad")
//    public String eliminarCiudad(@RequestParam String nombre) {
//        neoService.eliminarCiudadPorNombre(nombre);
//        return "🗑️ Ciudad eliminada: " + nombre;
//    }
//
//    // --- RUTAS ---
//
//    @PostMapping("/ruta")
//    public String crearRuta(@RequestParam String origen, @RequestParam String destino, @RequestParam double distancia) {
//        neoService.crearRutaPorNombres(origen, destino, distancia);
//        return "✅ Ruta creada entre " + origen + " y " + destino;
//    }
//
//    @DeleteMapping("/ruta")
//    public String eliminarRuta(@RequestParam String origen, @RequestParam String destino) {
//        neoService.eliminarRutaPorNombres(origen, destino);
//        return "🗑️ Ruta eliminada entre " + origen + " y " + destino;
//    }
//
//    @GetMapping("/hola")
//    public String hola() {
//        return "Hola desde NeoController 👋";
//    }
//}
