package com.yeito.tpoprogra.repository;

import com.yeito.tpoprogra.model.Ruta;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends Neo4jRepository<Ruta, Long> {

    @Query("MATCH (a:Ciudad),(b:Ciudad) WHERE id(a)=$idOrigen AND id(b)=$idDestino " +
            "MERGE (a)-[:RUTA {distancia:$distancia}]->(b)")
    void enlazarCiudades(Long idOrigen, Long idDestino, double distancia);

    @Query("MATCH (a:Ciudad {nombre:$origen})-[r:RUTA]->(b:Ciudad {nombre:$destino}) DELETE r")
    void borrarRuta(String origen, String destino);


}

