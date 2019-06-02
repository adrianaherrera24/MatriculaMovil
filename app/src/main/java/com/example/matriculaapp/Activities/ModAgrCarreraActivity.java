package com.example.matriculaapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matriculaapp.LogicaNegocio.Carrera;
import com.example.matriculaapp.R;

/** Este Activity se va a manejar para agregar y editar datos de Carreras. **/
public class ModAgrCarreraActivity extends AppCompatActivity {

    private FloatingActionButton btm_ma_carrera;
    private boolean editable = true; // Para  saber si es modo de edicion

    /* Datos requeridos para Carreras. */
    private EditText codigoCarrera, nombreCarrera, tituloCarrera;
    private View activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_agr_carrera); // Conexion con el layout

        Toolbar toolbar = findViewById(R.id.toolbar_ma_carrera);
        setSupportActionBar(toolbar);

        // Inicializacion de Variables
        editable = true;

        // boton para agregar o editar
        btm_ma_carrera = findViewById(R.id.ma_carrera);

        //varibles del formulario
        codigoCarrera = findViewById(R.id.codigoCarrera);
        nombreCarrera = findViewById(R.id.nombreCarrera);
        tituloCarrera = findViewById(R.id.tituloCarrera);
        codigoCarrera.setText("");
        nombreCarrera.setText("");
        tituloCarrera.setText("");

        //recibe informacion de admCarreraActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            editable = extras.getBoolean("editable");
            if (editable) {   // si su modo es de edicion
                Carrera aux = (Carrera) getIntent().getSerializableExtra("carrera");
                // Completo los espacios con la información de cada carrera.
                codigoCarrera.setText(aux.getCodigo());
                codigoCarrera.setEnabled(false); // Para que no se pueda editar el codigo
                nombreCarrera.setText(aux.getNombre());
                tituloCarrera.setText(aux.getTitulo());

                // Cuando se termina de editar
                btm_ma_carrera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarCarrera();
                    }
                });
            } else { // en caso de agregar una carrera nueva
                btm_ma_carrera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarCarrera();
                    }
                });
            }
        }
    }

    public void agregarCarrera() {
        if (validateForm()) {
            Carrera car = new Carrera(
                    codigoCarrera.getText().toString(),
                    nombreCarrera.getText().toString(),
                    tituloCarrera.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmCarreraActivity.class);
            intent.putExtra("agregarCarrera", car);
            startActivity(intent);
            finish();
        }
    }

    public void editarCarrera() {
        if (validateForm()) {
            Carrera car = new Carrera(
                    codigoCarrera.getText().toString(),
                    nombreCarrera.getText().toString(),
                    tituloCarrera.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmCarreraActivity.class);
            intent.putExtra("editarCarrera", car);
            startActivity(intent);
            finish();
        }
    }

    public boolean validateForm() {
        int error = 0;
        if (TextUtils.isEmpty(this.nombreCarrera.getText())) {
            nombreCarrera.setError("Nombre requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.codigoCarrera.getText())) {
            codigoCarrera.setError("Código requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.tituloCarrera.getText())) {
            tituloCarrera.setError("Título requerido");
            error++;
        }
        if (error > 0) {
            Toast.makeText(getApplicationContext(), "Contiene algunos errores.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
