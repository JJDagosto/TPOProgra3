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


    public GrafoService(CiudadRepository CiudadRepository , RutaRepository RutaRepository, NeoService NeoService) {
        this.CiudadRepository = CiudadRepository;
        this.RutaRepository = RutaRepository;
        this.NeoService = NeoService;
    }

        public List<String> bfs(String inicio) {
            Map<String, Map<String, Double>> grafo = NeoService.importarGrafo();
            List<String> recorrido = new ArrayList<>();
            Set<String> visitados = new HashSet<>();
            Queue<String> cola = new LinkedList<>();

            cola.add(inicio);
            visitados.add(inicio);

            while (!cola.isEmpty()) {
                String actual = cola.poll();
                recorrido.add(actual);

                if (grafo.containsKey(actual)) {
                    for (String vecino : grafo.get(actual).keySet()) {
                        if (!visitados.contains(vecino)) {
                            visitados.add(vecino);
                            cola.add(vecino);
                        }
                    }
                }
            }
            return recorrido;
        }
    }
