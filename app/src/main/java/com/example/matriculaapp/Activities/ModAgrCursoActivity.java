package com.example.matriculaapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matriculaapp.LogicaNegocio.Curso;
import com.example.matriculaapp.R;


/** Este Activity se va a manejar para agregar y editar datos de Cursos. **/
public class ModAgrCursoActivity extends AppCompatActivity {

    private FloatingActionButton btn_ma_curso;
    private boolean editable = true; // Para  saber si es modo de edicion

    /* Datos requeridos para Cursos. */
    private EditText codigoCurso, nombreCurso, creditosCurso, horasCurso, carreraCurso, cicloCurso, annoCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_agr_curso); // Conexion con el layout

        Toolbar toolbar = findViewById(R.id.toolbar_ma_curso);
        setSupportActionBar(toolbar);

        // Inicializacion de Variables
        editable = true;

        // boton para agregar o editar
        btn_ma_curso = findViewById(R.id.ma_curso);

        //varibles del formulario
        codigoCurso = findViewById(R.id.codigoCurso);
        nombreCurso = findViewById(R.id.nombreCurso);
        creditosCurso = findViewById(R.id.creditosCurso);
        horasCurso = findViewById(R.id.horasCurso);
        carreraCurso = findViewById(R.id.carreraCurso);
        cicloCurso = findViewById(R.id.ciclosCurso);
        annoCurso = findViewById(R.id.annoCurso);
        codigoCurso.setText("");
        nombreCurso.setText("");
        creditosCurso.setText("");
        horasCurso.setText("");
        carreraCurso.setText("");
        cicloCurso.setText("");
        annoCurso.setText("");

        //recibe informacion de admCursoActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            editable = extras.getBoolean("editable");
            if (editable) {   // si su modo es de edicion
                Curso aux = (Curso) getIntent().getSerializableExtra("curso");
                // Completo los espacios con la información de cada curso.
                codigoCurso.setText(aux.getId());
                codigoCurso.setEnabled(false); // Para que no se pueda editar el codigo
                nombreCurso.setText(aux.getNombre());
                creditosCurso.setText(Integer.toString(aux.getCreditos()));
                horasCurso.setText(Integer.toString(aux.getHorasSemanales()));
                carreraCurso.setText(aux.getCarrera());
                cicloCurso.setText(Integer.toString(aux.getCiclo()));
                annoCurso.setText(aux.getAnno());

                // Cuando se termina de editar
                btn_ma_curso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editarCurso();
                    }
                });
            } else { // en caso de agregar un curso nuevo
                btn_ma_curso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarCurso();
                    }
                });
            }
        }
    }

    public void agregarCurso() {
        if (validateForm()) {
            Curso cur = new Curso(
                    codigoCurso.getText().toString(),
                    nombreCurso.getText().toString(),
                    Integer.parseInt(creditosCurso.getText().toString()),
                    Integer.parseInt(horasCurso.getText().toString()),
                    carreraCurso.getText().toString(),
                    Integer.parseInt(cicloCurso.getText().toString()),
                    annoCurso.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmCursoActivity.class);
            intent.putExtra("agregarCurso", cur);
            startActivity(intent);
            finish();
        }
    }

    public void editarCurso() {
        if (validateForm()) {
            Curso cur = new Curso(
                    codigoCurso.getText().toString(),
                    nombreCurso.getText().toString(),
                    Integer.parseInt(creditosCurso.getText().toString()),
                    Integer.parseInt(horasCurso.getText().toString()),
                    carreraCurso.getText().toString(),
                    Integer.parseInt(cicloCurso.getText().toString()),
                    annoCurso.getText().toString()
            );

            Intent intent = new Intent(getBaseContext(), AdmCursoActivity.class);
            intent.putExtra("editarCurso", cur);
            startActivity(intent);
            finish();
        }
    }

    public boolean validateForm() {
        int error = 0;
        if (TextUtils.isEmpty(this.nombreCurso.getText())) {
            nombreCurso.setError("Nombre requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.codigoCurso.getText())) {
            codigoCurso.setError("Código requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.creditosCurso.getText())) {
            creditosCurso.setError("Créditos requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.horasCurso.getText())) {
            horasCurso.setError("Horas requerido");
            error++;
        }
        if (error > 0) {
            Toast.makeText(getApplicationContext(), "Contiene algunos errores.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
