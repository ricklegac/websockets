  package org.example.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hospital {

    private int id;
    private Set<Camas> camas = new HashSet<>();
    private int cantCamas;

    public Hospital() {
        cantCamas = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Camas> getCamas() {
        return camas;
    }

    public void setCamas(Set<Camas> camas) {
        this.camas = camas;
    }

    public int getCantCamas() {
        return cantCamas;
    }

    public void setCantCamas(int cantCamas) {
        this.cantCamas = cantCamas;
    }

    public String getIdCamas(int idHospital) {
        List<Camas> aList = camas.stream().collect(Collectors.toList());
        String out = "";
        for (Camas x : aList) {
            out += "<a href=\"#\" OnClick=cambiarEstadoCama(" + idHospital +","+ x.getId() + ") > Cama " + x.getId() + " : " + x.getEstado() + "</a><br>";
            out += "<a href=\"#\" OnClick=eliminarCama(" + idHospital +","+ x.getId() + ") > Eliminar Cama</a><br>";
        }
        return out;
    }

    public Camas getCamasById(int idCama) {
        List<Camas> aList = camas.stream().collect(Collectors.toList());
        String out = "";
        for (Camas x : aList) {
            if(idCama == x.getId())
                return x;
        }
        return null;
    }
    
    public void eliminarCamas(Camas cama){
        camas.remove(cama);
    }
    

}
