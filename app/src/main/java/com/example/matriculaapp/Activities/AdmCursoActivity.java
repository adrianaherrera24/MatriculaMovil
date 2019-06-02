package com.example.matriculaapp.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.matriculaapp.Adapter.CursoAdapter;
import com.example.matriculaapp.Helper.RecyclerItemTouchHelper;
import com.example.matriculaapp.LogicaNegocio.Curso;
import com.example.matriculaapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdmCursoActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, CursoAdapter.CursoAdapterListener{

    private RecyclerView mRecyclerView;
    private CursoAdapter mAdapter;
    private List<Curso> listaCursos;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;
    private FloatingActionButton btnAgregarCurso;

    String apiUrl = "http://192.168.1.3:8080/MatriculaApp_Web/CursoServlet?";
    String apiUrlTemporal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_curso);

        Toolbar toolbar = findViewById(R.id.toolbar_curso);
        setSupportActionBar(toolbar);

        // Inicializar Variables
        mRecyclerView = findViewById(R.id.recycler_curso);
        listaCursos = new ArrayList<>();
        mAdapter = new CursoAdapter(listaCursos, this);
        coordinatorLayout = findViewById(R.id.coordinator_layout_cur);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        /// PARA MOSTRAR LOS DATOS DESDE LA BASE DE DATOS CON LA CONEXION AL SERVLET
        apiUrlTemporal = apiUrl + "opc=1";
        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

        // Boton para agregar
        btnAgregarCurso = findViewById(R.id.agregar_curso);
        btnAgregarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irCurso();
            }
        });

        // Para funciones de swipe
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        checkIntentInformation();

        //refresca la vista
        mAdapter.notifyDataSetChanged();
    }

    // Revisa los cambios necesarios para editar o agregar
    private void checkIntentInformation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Curso aux;
            aux = (Curso) getIntent().getSerializableExtra("agregarCurso");
            if (aux == null) {
                aux = (Curso) getIntent().getSerializableExtra("editarCurso");
                if (aux != null) {
                    apiUrlTemporal = apiUrl + "opc=4&codigo="+aux.getId()+"&nombre="+aux.getNombre()+"&creditos="+aux.getCreditos()+"&horas="+aux.getHorasSemanales()+"&ciclo="+aux.getCiclo()+"&carrera="+aux.getCarrera()+"&anno="+aux.getAnno();
                    MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                    myAsyncTasks.execute();
                    Toast.makeText(getApplicationContext(), aux.getNombre() + " editado correctamente", Toast.LENGTH_LONG).show();

                }
            } else {
                apiUrlTemporal = apiUrl + "opc=2&codigo="+aux.getId()+"&nombre="+aux.getNombre()+"&creditos="+aux.getCreditos()+"&horas="+aux.getHorasSemanales()+"&ciclo="+aux.getCiclo()+"&carrera="+aux.getCarrera()+"&anno="+aux.getAnno();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
                Toast.makeText(getApplicationContext(), "Curso agregado.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void irCurso() {
        Intent intent = new Intent(this, ModAgrCursoActivity.class);
        intent.putExtra("editable", false);
        startActivity(intent);
    }


    // Nace de la implementacion de EstudianteAdapter
    @Override
    public void onContactSelected(Curso curso) {

    }

    // Nace de la implementacion de RecyclerItemTouchHelper
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (direction == ItemTouchHelper.START) { /// Para eliminar o restaurar
            // get the removed item name to display it in snack bar
            String codigo = listaCursos.get(viewHolder.getAdapterPosition()).getId();
            String name = listaCursos.get(viewHolder.getAdapterPosition()).getNombre();

            ///CONECTA LA URL AL SERVLET PARA ELIMINAR PROFESOR
            apiUrlTemporal = apiUrl + "opc=3&codigo="+codigo;
            MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
            myAsyncTasks.execute();
            // save the index deleted
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recyclerView
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(coordinatorLayout, name + " removido!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item from adapter
                    mAdapter.restoreItem(deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        } else {
            /// SWIPED DE EDITAR
            Curso aux = mAdapter.getSwipedItem(viewHolder.getAdapterPosition());
            /// Se pasa al activity de editar o agregar: UpdateAlumnoActivity.
            Intent intent = new Intent(this, ModAgrCursoActivity.class);
            intent.putExtra("editable", true); /// Envia a decir es que el modo editable
            intent.putExtra("curso", aux); /// Envia la position o index del alumno
            // Notifica el movimiento
            mAdapter.notifyDataSetChanged(); //restart left swipe view
            /// Despues de notificar el swipe entonces inicio el intent
            startActivity(intent);
        }
    }

    /**************buscar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { /// Para buscar
        // Inflate the menu; this adds alumnoList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu); // xml

        // Associate searchable configuration with the SearchView   !IMPORTANT
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.accion_buscar)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change, every type on input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    // Nace de la implementacion de RecyclerItemTouchHelper
    @Override
    public void onItemMove(int source, int target) {
        mAdapter.onItemMove(source, target);
    }

    // Para que retroceda al menu principal
    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        // Se devuelve al NavDrawer
        Intent a = new Intent(this, NavDrawerActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        super.onBackPressed();
    }

    ///CLASE EXCLUSIVA PARA EL ENVIO Y RECIBIMIENTO DE DATOS DESDE EL SERVLET
    public class MyAsyncTasks extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(apiUrlTemporal);

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();

                    }
                    // return the data to onPostExecute method
                    Log.w("JSON", current);
                    return current;


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }


        @Override
        protected void onPostExecute(String s) {
            //S tiene la lista Actualizada que recibe del web service
            //Se actualiza el recycler view
            try {
                Gson gson = new Gson();
                ArrayList<Curso> profesorArrayList = (ArrayList<Curso>) gson.fromJson(s,
                        new TypeToken<ArrayList<Curso>>() {
                        }.getType());


                listaCursos = profesorArrayList;
                mAdapter = new CursoAdapter(listaCursos, AdmCursoActivity.this);
                coordinatorLayout = findViewById(R.id.coordinator_layout_cur);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(AdmCursoActivity.this, DividerItemDecoration.VERTICAL));
                mRecyclerView.setAdapter(mAdapter);


                //txtView.setText(ClienteList.toString());

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
