package com.yeito.tpoprogra.service;

import com.yeito.tpoprogra.model.Paquete;
import com.yeito.tpoprogra.repository.CiudadRepository;
import com.yeito.tpoprogra.repository.PaqueteRepository;
import com.yeito.tpoprogra.repository.RutaRepository;
import com.yeito.tpoprogra.model.Ciudad;
import com.yeito.tpoprogra.model.Ruta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.*;

@Service
public class NeoService {

    private final CiudadRepository CiudadRepository;
    private final RutaRepository RutaRepository;
    private final com.yeito.tpoprogra.repository.PaqueteRepository PaqueteRepository;

    @Autowired
    public NeoService(CiudadRepository CiudadRepository , RutaRepository RutaRepository, com.yeito.tpoprogra.repository.PaqueteRepository PaqueteRepository) {
        this.CiudadRepository = CiudadRepository;
        this.RutaRepository = RutaRepository;
        this.PaqueteRepository = PaqueteRepository;

    }

    public CiudadRepository getCiudadRepository() {
        return CiudadRepository;
    }
    public RutaRepository getRutaRepository() {
        return RutaRepository;
    }

    @Transactional
    public Ciudad crearCiudad(String nombre) {
        Ciudad ciudad = new Ciudad(nombre);
        return CiudadRepository.save(ciudad);
    }

    @Transactional
    public Ciudad editarCiudad(Long id, String nuevoNombre) {
        Optional<Ciudad> ciudadOpt = CiudadRepository.findById(id);
        if (ciudadOpt.isPresent()) {
            Ciudad ciudad = ciudadOpt.get();
            ciudad.setNombre(nuevoNombre);
            return CiudadRepository.save(ciudad);
        }
        return null;
    }

    @Transactional
    public boolean eliminarCiudad(Long id) {
        if (CiudadRepository.existsById(id)) {
            CiudadRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Long obtenerIdPorNombre(String nombre) {
        return CiudadRepository.findByNombre(nombre)
                .map(Ciudad::getId)
                .orElse(null);
    }

    public Long obtenerIdRuta(String origen , String destino) {
        var ciudadOrigenOpt = CiudadRepository.findByNombre(origen);
        var ciudadDestinoOpt = CiudadRepository.findByNombre(destino);

        if (ciudadOrigenOpt.isEmpty() || ciudadDestinoOpt.isEmpty()) {
            throw new RuntimeException("No se encontró una o ambas ciudades");
        }

        var ciudadOrigen = ciudadOrigenOpt.get();

        // Buscar la ruta que va al destino solicitado
        return ciudadOrigen.getRutas().stream()
                .filter(r -> r.getDestino().getNombre().equalsIgnoreCase(destino))
                .findFirst()
                .map(Ruta::getId)  // suponiendo que tu clase Ruta tiene un campo @Id Long id
                .orElseThrow(() -> new RuntimeException("No existe una ruta entre " + origen + " y " + destino));
    }

    @Transactional
    public Ruta crearRuta(Long idOrigen, Long idDestino, double distancia) {
        Optional<Ciudad> origenOpt = CiudadRepository.findById(idOrigen);
        Optional<Ciudad> destinoOpt = CiudadRepository.findById(idDestino);

        if (origenOpt.isEmpty() || destinoOpt.isEmpty()) {
            throw new RuntimeException("No se encontró una o ambas ciudades");
        }

        Ciudad origen = origenOpt.get();
        Ciudad destino = destinoOpt.get();

        Ruta ruta = new Ruta(distancia, origen, destino);
        origen.getRutas().add(ruta);

        CiudadRepository.save(origen);
        return ruta;
    }

    public Ruta editarRuta(Long idRuta, double nuevaDistanciaKm) {
        // Buscamos la ruta
        var rutaOpt = RutaRepository.findById(idRuta);
        if (rutaOpt.isEmpty()) {
            throw new RuntimeException("Ruta no encontrada");
        }

        Ruta ruta = rutaOpt.get();
        ruta.setDistancia(nuevaDistanciaKm);
        return RutaRepository.save(ruta);
    }

    @Transactional
    public boolean eliminarRuta(Long idRuta) {
        if (RutaRepository.existsById(idRuta)) {
            RutaRepository.deleteById(idRuta);
            return true;
        }
        return false;
    }

    public  Map<String, Map<String, Double>> importarGrafo() {
        Map<String, Map<String, Double>> grafo = new HashMap<>();

        // Trae todas las ciudades y rutas desde Neo4j
        List<Ciudad> ciudades = CiudadRepository.findAll();

        for (Ciudad ciudad : ciudades) {
            grafo.putIfAbsent(ciudad.getNombre(), new HashMap<>());
            for (Ruta ruta : ciudad.getRutas()) {
                grafo.get(ciudad.getNombre())
                        .put(ruta.getDestino().getNombre(), ruta.getDistancia());
            }
        }

        return grafo;
    }

    public List<Paquete> getPaquetes() {
        return PaqueteRepository.findAll();
    }

}