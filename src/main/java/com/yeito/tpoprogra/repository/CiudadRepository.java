package com.yeito.tpoprogra.repository;

import com.yeito.tpoprogra.model.Ciudad;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CiudadRepository extends Neo4jRepository<Ciudad, Long> {
    Optional<Ciudad> findByNombre(String nombre);
}

