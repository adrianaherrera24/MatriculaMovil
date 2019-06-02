package com.example.matriculaapp.LogicaNegocio;

import java.io.Serializable;

public class Profesor implements Serializable {

    private String id;
    private String nombre;
    private String telefono;
    private String email;

    public Profesor(){
        id = new String();
        nombre = new String();
        telefono = new String();
        email = new String();
    }
    //Enviar Cursos Lista
    public Profesor(String id, String nombre, String telefono, String email){
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
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

    @Override
    public String toString() {
        String val = "Profesor{" + "id=" + id + ", nombre=" + nombre + ", telefono=" + telefono + ", email=" + email;
        return val;
    }

}
