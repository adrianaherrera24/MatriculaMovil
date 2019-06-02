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

import com.example.matriculaapp.Adapter.CarreraAdapter;
import com.example.matriculaapp.Helper.RecyclerItemTouchHelper;
import com.example.matriculaapp.LogicaNegocio.Carrera;
import com.example.matriculaapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdmCarreraActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, CarreraAdapter.CarreraAdapterListener{

    private RecyclerView mRecyclerView;
    private CarreraAdapter mAdapter;
    private List<Carrera> listaCarreras;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;
    private FloatingActionButton btnAgregarCarrera;

    String apiUrl = "http://192.168.1.3:8080/MatriculaApp_Web/CarreraServlet?";
    String apiUrlTemporal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_carrera);

        Toolbar toolbar = findViewById(R.id.toolbar_carrera);
        setSupportActionBar(toolbar);

        // Inicializar Variables
        mRecyclerView = findViewById(R.id.recycler_carrera);
        listaCarreras = new ArrayList<>();
        mAdapter = new CarreraAdapter(listaCarreras, this);
        coordinatorLayout = findViewById(R.id.coordinator_layout_car);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        /// PARA MOSTRAR LOS DATOS DESDE LA BASE DE DATOS CON LA CONEXION AL SERVLET
        apiUrlTemporal = apiUrl + "opc=1";
        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

        // go to update or add career
        btnAgregarCarrera = findViewById(R.id.agregar_carrera);
        btnAgregarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irCarrera();
            }
        });

        // Para funciones de swipe
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        checkIntentInformation();

        //refresca la vista
        mAdapter.notifyDataSetChanged();
    }

    private void checkIntentInformation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Carrera aux;
            aux = (Carrera) getIntent().getSerializableExtra("agregarCarrera");
            if (aux == null) {
                aux = (Carrera) getIntent().getSerializableExtra("editarCarrera");
                if (aux != null) {
                    apiUrlTemporal = apiUrl + "opc=4&codigo="+aux.getCodigo()+"&nombre="+aux.getNombre()+"&titulo="+aux.getTitulo();
                    MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                    myAsyncTasks.execute();
                    Toast.makeText(getApplicationContext(), aux.getNombre() + " editado correctamente", Toast.LENGTH_LONG).show();
                }
            } else {
                apiUrlTemporal = apiUrl + "opc=2&codigo="+aux.getCodigo()+"&nombre="+aux.getNombre()+"&titulo="+aux.getTitulo();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();

                Toast.makeText(getApplicationContext(), "Carrera agregada.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void irCarrera() {
        Intent intent = new Intent(this, ModAgrCarreraActivity.class);
        intent.putExtra("editable", false);
        startActivity(intent);
    }

    // Nace de la implementacion de EstudianteAdapter
    @Override
    public void onContactSelected(Carrera carrera) {

    }

    // Nace de la implementacion de RecyclerItemTouchHelper
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (direction == ItemTouchHelper.START) { /// Para eliminar o restaurar
            // get the removed item name to display it in snack bar
            String codigo = listaCarreras.get(viewHolder.getAdapterPosition()).getCodigo();

            ///CONECTA LA URL AL SERVLET PARA ELIMINAR PROFESOR
            apiUrlTemporal = apiUrl + "opc=3&codigo="+codigo;
            MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
            myAsyncTasks.execute();

            // save the index deleted
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recyclerView
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            Toast.makeText(getApplicationContext(), "Carrera eliminada.", Toast.LENGTH_LONG).show();

        } else {
            /// SWIPED DE EDITAR
            Carrera aux = mAdapter.getSwipedItem(viewHolder.getAdapterPosition());
            /// Se pasa al activity de editar o agregar: UpdateAlumnoActivity.
            Intent intent = new Intent(this, ModAgrCarreraActivity.class);
            intent.putExtra("editable", true); /// Envia a decir es que el modo editable
            intent.putExtra("carrera", aux); /// Envia la position o index del alumno
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
                ArrayList<Carrera> carreraArrayList = (ArrayList<Carrera>) gson.fromJson(s,
                        new TypeToken<ArrayList<Carrera>>() {
                        }.getType());


                listaCarreras = carreraArrayList;
                mAdapter = new CarreraAdapter(listaCarreras, AdmCarreraActivity.this);
                coordinatorLayout = findViewById(R.id.coordinator_layout_car);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(AdmCarreraActivity.this, DividerItemDecoration.VERTICAL));
                mRecyclerView.setAdapter(mAdapter);


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
