package org.example.model;

import java.util.HashSet;
import java.util.Set;


public class Camas {

    private int id;
    private String estado;

    public Camas(){
        estado = "DESOCUPADO";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}