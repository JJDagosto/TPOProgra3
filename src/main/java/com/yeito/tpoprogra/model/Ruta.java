package com.yeito.tpoprogra.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Id;

@RelationshipProperties
public class Ruta {

    @Id
    @GeneratedValue
    private Long id;


    private double distancia;

    private Ciudad origen;

    @TargetNode
    private Ciudad destino;

    public Ruta() {}

    public Ruta(double distancia, Ciudad origen, Ciudad destino) {
        this.distancia = distancia;
        this.destino = destino;
        this.origen = origen;
    }

    public Long getId() {
        return id;
    }

    public double getDistancia() { return distancia; }
    public void setDistancia(double distancia) { this.distancia = distancia; }

    public Ciudad getDestino() { return destino; }
    public void setDestino(Ciudad destino) { this.destino = destino; }

    public Ciudad getOrigen() { return origen; }
    public void setOrigen(Ciudad origen) { this.origen = origen; }
}

