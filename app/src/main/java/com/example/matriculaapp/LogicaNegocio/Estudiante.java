package com.example.matriculaapp.LogicaNegocio;

import java.io.Serializable;

public class Estudiante implements Serializable {

    private String id;
    private String nombre;
    private String telefono;
    private String email;
    private String fechaNacimiento;
    private String carrera;

    public Estudiante(){
        id= new String();
        nombre = new String();
        telefono = new String();
        email = new String();
        fechaNacimiento = new String();
        carrera = new String();

    }
    //Enviar Cursos Lista
    public Estudiante(String id, String nombre, String telefono, String email, String fechaNacimiento, String carrera){
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.carrera = carrera;
    }

    @Override
    public String toString() {
        return "Alumno{" + "id=" + id + ", nombre=" + nombre + ", telefono=" + telefono + ", email=" + email + ", fechaNacimiento=" + fechaNacimiento + ", carrera=" + carrera + '}';
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
}
