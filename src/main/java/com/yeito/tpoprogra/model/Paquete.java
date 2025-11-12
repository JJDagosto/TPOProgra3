package com.yeito.tpoprogra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Node("Paquete")
@JsonIgnoreProperties({"origen", "destino"})

public class Paquete {

    @Id
    @GeneratedValue
    private Long id;
    private double peso;
    private Ciudad origen;
    private Ciudad destino;

    public Paquete() {}

    public Paquete(double peso, Ciudad origen, Ciudad destino) {
        this.peso = peso;
        this.destino = destino;
        this.origen = origen;
    }

    public Long getId() {
        return id;
    }

    public double getPeso() { return peso; }
    public void setpeso(double peso) { this.peso = peso; }

    public Ciudad getDestino() { return destino; }
    public void setDestino(Ciudad destino) { this.destino = destino; }

    public Ciudad getOrigen() { return origen; }
    public void setOrigen(Ciudad origen) { this.origen = origen; }
}

