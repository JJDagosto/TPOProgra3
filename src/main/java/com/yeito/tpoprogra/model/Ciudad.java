package com.yeito.tpoprogra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;


import java.util.HashSet;
import java.util.Set;

@Node("Ciudad")
public class Ciudad {

    @Id
    @GeneratedValue
    private Long id;
    private String nombre;

    public Ciudad() {}

    @Relationship(type = "RUTA")
    private Set<Ruta> rutas = new HashSet<>();


    public Ciudad(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Set<Ruta> getRutas() { return rutas; }
    public void setRutas(Set<Ruta> rutas) { this.rutas = rutas; }
    public Long getId() { return id; }

}