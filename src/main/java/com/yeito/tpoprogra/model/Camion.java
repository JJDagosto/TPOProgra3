package com.yeito.tpoprogra.model;

import org.springframework.data.neo4j.core.schema.*;
import java.util.HashSet;
import java.util.Set;

@Node("Camion")
public class Camion {

    @Id
    @GeneratedValue
    private Long id;

    private double capacidad;
    private double cargaActual;

    @Relationship(type = "TRANSPORTA")
    private Set<Paquete> paquetes = new HashSet<>();

    private Set<Ciudad> destinos = new HashSet<>();

    public Camion() {}

    public Camion(double capacidad) {
        this.capacidad = capacidad;
        this.cargaActual = 0.0;
    }

    public double getCapacidad() { return capacidad; }
    public void setCapacidad(double capacidad) { this.capacidad = capacidad; }

    public Set<Paquete> getPaquetes() { return paquetes; }

    public Set<Ciudad> getDestinos() { return destinos; }

    public void agregarPaquete(Paquete p) {
        paquetes.add(p);
        cargaActual += p.getPeso();
        destinos.add(p.getOrigen());
        destinos.add(p.getDestino());
    }

    public double getCargaActual() { return cargaActual; }
}
