package com.example.matriculaapp.LogicaNegocio;

import java.io.Serializable;

public class Curso implements Serializable {

    private String id;
    private String nombre;
    private int creditos;
    private int horasSemanales;
    private String carrera;
    private int ciclo;
    private String anno;

    public Curso(){
        id = new String();
        nombre = new String();
        creditos = 0;
        horasSemanales =0;
        carrera = new String();
        ciclo = 0;
        anno = new String();
    }
    //Enviar Cursos Lista
    public Curso(String id, String nombre, int creditos, int horasSemanales, String carrera, int ciclo, String anno){
        this.id = id;
        this.nombre = nombre;
        this.creditos = creditos;
        this.horasSemanales = horasSemanales;
        this.carrera = carrera;
        this.ciclo = ciclo;
        this.anno = anno;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public int getCiclo() {
        return ciclo;
    }

    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    @Override
    public String toString() {
        return "Curso{" + "id=" + id + ", nombre=" + nombre + ", creditos=" + creditos + ", horasSemanales=" + horasSemanales + ", carrera=" + carrera + ", ciclo=" + ciclo + ", anno=" + anno + '}';
    }
}
