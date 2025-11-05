package com.yeito.tpoprogra.repository;

import com.yeito.tpoprogra.model.Ruta;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends Neo4jRepository<Ruta, Long> {
}


