package com.example.matriculaapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matriculaapp.LogicaNegocio.Profesor;
import com.example.matriculaapp.R;

/** Este Activity se va a manejar para agregar y editar datos de Profesores. **/
public class ModAgrProfesorActivity extends AppCompatActivity {

    private FloatingActionButton btn_ma_profesor;
    private boolean editable = true; // Para  saber si es modo de edicion

    /* Datos requeridos para Profesores. */
    private EditText nombreProfesor;
    private EditText cedulaProfesor;
    private EditText emailProfesor;
    private EditText telefonoProfesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_agr_profesor); // Conexion con el layout

        Toolbar toolbar = findViewById(R.id.toolbar_ma_prof);
        setSupportActionBar(toolbar);

        // Inicializacion de Variables
        editable = true;

        // boton para agregar o editar
        btn_ma_profesor = findViewById(R.id.ma_profesor);

        //varibles del formulario
        nombreProfesor = findViewById(R.id.nombreProfesor);
        cedulaProfesor = findViewById(R.id.cedulaProfesor);
        emailProfesor = findViewById(R.id.emailProfesor);
        telefonoProfesor = findViewById(R.id.telefonoProfesor);
        nombreProfesor.setText("");
        cedulaProfesor.setText("");
        emailProfesor.setText("");
        telefonoProfesor.setText("");

        //recibe informacion de admProfesorActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            editable = extras.getBoolean("editable");
            if (editable) {   // si su modo es de edicion
                Profesor aux = (Profesor) getIntent().getSerializableExtra("profesor");
                // Completo los espacios con la información de cada profesor.
                cedulaProfesor.setText(aux.getId());
                cedulaProfesor.setEnabled(false); // Para que no se pueda editar el codigo
                nombreProfesor.setText(aux.getNombre());
                emailProfesor.setText(aux.getEmail());
                telefonoProfesor.setText(aux.getTelefono());

                // Cuando se termina de editar
                btn_ma_profesor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarProfesor();
                    }
                });
            } else { // en caso de agregar un profesor nuevo
                btn_ma_profesor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarProfesor();
                    }
                });
            }
        }
    }

    public void agregarProfesor() {
        if (validateForm()) {
            // Ingreso los datos obtenidos al objeto Profesor
            Profesor prof = new Profesor(
                    cedulaProfesor.getText().toString(),
                    nombreProfesor.getText().toString(),
                    telefonoProfesor.getText().toString(),
                    emailProfesor.getText().toString()
            );

            // Después de agregado el profesor, se redirecciona hacia el Módulo Prodesores
            // donde se listan todos los datos.
            Intent intent = new Intent(getBaseContext(), AdmProfesorActivity.class);
            intent.putExtra("agregarProfesor", prof);
            startActivity(intent);
            finish(); //finalizo este activity
        }
    }

    public void editarProfesor() {
        if (validateForm()) {
            // Ingreso los datos nuevos al objeto Profesor
            Profesor prof = new Profesor(
                    cedulaProfesor.getText().toString(),
                    nombreProfesor.getText().toString(),
                    telefonoProfesor.getText().toString(),
                    emailProfesor.getText().toString()

            );
            Log.w("xD: ", prof.toString());
            // Después de agregado el profesor, se redirecciona hacia el Módulo Prodesores
            // donde se listan todos los datos.
            Intent intent = new Intent(getBaseContext(), AdmProfesorActivity.class);
            intent.putExtra("editarProfesor", prof);
            startActivity(intent);
            finish(); //finalizo este activity
        }
    }

    /* Realiza las validaciones del formulario. */
    public boolean validateForm() {
        int error = 0;
        if (TextUtils.isEmpty(this.nombreProfesor.getText())) {
            nombreProfesor.setError("Nombre requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.cedulaProfesor.getText())) {
            cedulaProfesor.setError("Cédula requerida");
            error++;
        }
        if (TextUtils.isEmpty(this.emailProfesor.getText())) {
            emailProfesor.setError("Email requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.telefonoProfesor.getText())) {
            telefonoProfesor.setError("Teléfono requerido");
            error++;
        }
        if (error > 0) {
            Toast.makeText(getApplicationContext(), "Presenta algunos errores.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
