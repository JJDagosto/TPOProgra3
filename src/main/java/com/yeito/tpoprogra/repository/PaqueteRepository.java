package com.yeito.tpoprogra.repository;

import com.yeito.tpoprogra.model.Paquete;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaqueteRepository extends Neo4jRepository<Paquete, Long> {
}