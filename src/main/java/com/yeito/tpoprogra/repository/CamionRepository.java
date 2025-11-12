package com.yeito.tpoprogra.repository;

import com.yeito.tpoprogra.model.Camion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends Neo4jRepository<Camion, Long> {
}
