package com.example.matriculaapp.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.matriculaapp.LogicaNegocio.Carrera;
import com.example.matriculaapp.LogicaNegocio.Estudiante;
import com.example.matriculaapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Este Activity se va a manejar para agregar y editar datos de Estudiantes. **/
public class ModAgrEstudianteActivity extends AppCompatActivity {

    FloatingActionButton btn_ma_alumno;
    private boolean editable = true; // Para  saber si es modo de edicion

    /* Datos requeridos para Estudiantes. */
    private EditText nombreEstudiante,cedulaEstudiante,emailEstudiante,
            telefonoEstudiante,fechaEstudiante,carreraEstudiante;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_agr_estudiante); // Conexion con el layout

        Toolbar toolbar = findViewById(R.id.toolbar_ma_est);
        setSupportActionBar(toolbar);

        // Inicializacion de Variables
        editable = true;

        // boton para agregar o editar
        btn_ma_alumno = findViewById(R.id.ma_alumno);
/*
        spinner = (Spinner) findViewById(R.id.carreraAlumno_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.carreras, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/


        //varibles del formulario
        nombreEstudiante = findViewById(R.id.nombreAlumno);
        cedulaEstudiante = findViewById(R.id.cedulaAlumno);
        emailEstudiante = findViewById(R.id.emailAlumno);
        telefonoEstudiante = findViewById(R.id.telefonoAlumno);
        fechaEstudiante = findViewById(R.id.fechaAlumno);
        carreraEstudiante = findViewById(R.id.carreraAlumno);

        nombreEstudiante.setText("");
        cedulaEstudiante.setText("");
        emailEstudiante.setText("");
        telefonoEstudiante.setText("");
        fechaEstudiante.setText("");
        carreraEstudiante.setText("");

        //recibe informacion de AdmEstudianteActivity.
        Bundle extras = getIntent().getExtras(); /// Trae el objeto desde AdmEstudianteActivity

        if (extras != null) { /// Si lo trae es que quiere editar el objeto.

            editable = extras.getBoolean("editable");

            if (editable) {   // si va a editar entonces muestra los datos del objeto en cada casilla.
                Estudiante aux = (Estudiante) getIntent().getSerializableExtra("alumno");
                // Completo los espacios con la información de cada estudiante.
                cedulaEstudiante.setText(aux.getId());
                cedulaEstudiante.setEnabled(false); // Para que no se pueda editar el codigo
                nombreEstudiante.setText(aux.getNombre());
                emailEstudiante.setText(aux.getEmail());
                telefonoEstudiante.setText(aux.getTelefono());
                fechaEstudiante.setText(aux.getFechaNacimiento());
                carreraEstudiante.setText(aux.getCarrera());

                // Cuando se termina de editar
                btn_ma_alumno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarEstudiante();
                    }
                });
            } else {  // en caso de agregar un estudiante nuevo
                btn_ma_alumno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarEstudiante();
                    }
                });
            }
        }
    }

    public void agregarEstudiante() {
        if (validateForm()) {
            Estudiante prof = new Estudiante(
                    cedulaEstudiante.getText().toString(),
                    nombreEstudiante.getText().toString(),
                    telefonoEstudiante.getText().toString(),
                    emailEstudiante.getText().toString(),
                    fechaEstudiante.getText().toString(),
                    carreraEstudiante.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmEstudianteActivity.class);
            intent.putExtra("agregarAlumno", prof);
            startActivity(intent);
            finish();
        }
    }

    public void editarEstudiante() {
        if (validateForm()) {
            Estudiante prof = new Estudiante(
                    cedulaEstudiante.getText().toString(),
                    nombreEstudiante.getText().toString(),
                    telefonoEstudiante.getText().toString(),
                    emailEstudiante.getText().toString(),
                    fechaEstudiante.getText().toString(),
                    carreraEstudiante.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmEstudianteActivity.class);
            intent.putExtra("editarAlumno", prof);
            startActivity(intent);
            finish();
        }
    }

    public boolean validateForm() {
        int error = 0;
        if (TextUtils.isEmpty(this.nombreEstudiante.getText())) {
            nombreEstudiante.setError("Nombre requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.cedulaEstudiante.getText())) {
            cedulaEstudiante.setError("Cédula requerida");
            error++;
        }
        if (TextUtils.isEmpty(this.emailEstudiante.getText())) {
            emailEstudiante.setError("Email requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.telefonoEstudiante.getText())) {
            telefonoEstudiante.setError("Teléfono requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.fechaEstudiante.getText())) {
            fechaEstudiante.setError("Fecha requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.carreraEstudiante.getText())) {
            carreraEstudiante.setError("Carrera requerido");
            error++;
        }
        if (error > 0) {
            Toast.makeText(getApplicationContext(), "Contiene algunos errores.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
