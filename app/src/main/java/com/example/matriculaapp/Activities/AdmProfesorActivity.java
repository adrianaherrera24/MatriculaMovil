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

import com.example.matriculaapp.Adapter.ProfesorAdapter;
import com.example.matriculaapp.Helper.RecyclerItemTouchHelper;
import com.example.matriculaapp.LogicaNegocio.Profesor;
import com.example.matriculaapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdmProfesorActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, ProfesorAdapter.ProfesorAdapterListener{

    private RecyclerView mRecyclerView;
    private ProfesorAdapter mAdapter;
    private List<Profesor> listaProfesores;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;
    private FloatingActionButton btnAgregarProfesor;

    String apiUrl = "http://192.168.1.3:8080/MatriculaApp_Web/ProfesorServlet?";
    String apiUrlTemporal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_profesor);

        Toolbar toolbar = findViewById(R.id.toolbar_profesor);
        setSupportActionBar(toolbar);

        // Inicializar Variables
        mRecyclerView = findViewById(R.id.recycler_profesor);
        listaProfesores = new ArrayList<>();
        mAdapter = new ProfesorAdapter(listaProfesores, this);
        coordinatorLayout = findViewById(R.id.coordinator_layout_pro);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        /// PARA MOSTRAR LOS DATOS DESDE LA BASE DE DATOS CON LA CONEXION AL SERVLET
        apiUrlTemporal = apiUrl + "opc=1";
        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

        // Para agregar un nuevo Profesor
        btnAgregarProfesor = findViewById(R.id.agregar_profesor);
        btnAgregarProfesor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                irProfesor();
            }
        });

        // Para funciones de swipe
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        checkIntentInformation();

        //refresca la vista
        mAdapter.notifyDataSetChanged();
    }

    // Nace de la implementacion de EstudianteAdapter
    @Override
    public void onContactSelected(Profesor profesor) { }

    // Nace de la implementacion de RecyclerItemTouchHelper
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (direction == ItemTouchHelper.START) { /// Para eliminar o restaurar
            // get the removed item name to display it in snack bar
            String cedula = listaProfesores.get(viewHolder.getAdapterPosition()).getId();
            String name = listaProfesores.get(viewHolder.getAdapterPosition()).getNombre();

            ///CONECTA LA URL AL SERVLET PARA ELIMINAR PROFESOR
            apiUrlTemporal = apiUrl + "opc=3&id="+cedula;
            MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
            myAsyncTasks.execute();

            // save the index deleted
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recyclerView
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(coordinatorLayout, name + " removido!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
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
            Profesor aux = mAdapter.getSwipedItem(viewHolder.getAdapterPosition());
            /// Se pasa al activity de editar o agregar: UpdateAlumnoActivity.
            Intent intent = new Intent(this, ModAgrProfesorActivity.class);
            intent.putExtra("editable", true); /// Envia a decir es que el modo editable
            intent.putExtra("profesor", aux); /// Envia la position o index del alumno
            // Notifica el movimiento
            mAdapter.notifyDataSetChanged(); //restart left swipe view
            /// Despues de notificar el swipe entonces inicio el intent
            startActivity(intent);
        }
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

    // Relacion con ModAgr
    private void checkIntentInformation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Profesor aux;
            aux = (Profesor) getIntent().getSerializableExtra("agregarProfesor");
            if (aux == null) {
                aux = (Profesor) getIntent().getSerializableExtra("editarProfesor");
                if (aux != null) {
                    apiUrlTemporal = apiUrl + "opc=4&id="+aux.getId()+"&nombre="+aux.getNombre()+"&email="+aux.getEmail()+"&telefono="+aux.getTelefono();
                    MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                    myAsyncTasks.execute();
                    Toast.makeText(getApplicationContext(), aux.getNombre() + " editado correctamente", Toast.LENGTH_LONG).show();
                }
            } else {
                // Si encuentra un nuevo objeto
                apiUrlTemporal = apiUrl + "opc=2&id="+aux.getId()+"&nombre="+aux.getNombre()+"&email="+aux.getEmail()+"&telefono="+aux.getTelefono();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
                Toast.makeText(getApplicationContext(),"Profesor Agregado.", Toast.LENGTH_LONG).show();
            }
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

    //  Direcciona al formulario para agregar
    private void irProfesor() {
        Intent intent = new Intent(this, ModAgrProfesorActivity.class);
        intent.putExtra("editable", false);
        startActivity(intent);
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
            Log.w("Mi JSON", s);

            try {
                Gson gson = new Gson();
                ArrayList<Profesor> profesorArrayList = (ArrayList<Profesor>) gson.fromJson(s,
                        new TypeToken<ArrayList<Profesor>>() {
                        }.getType());


                listaProfesores = profesorArrayList;
                mAdapter = new ProfesorAdapter(listaProfesores, AdmProfesorActivity.this);
                coordinatorLayout = findViewById(R.id.coordinator_layout_pro);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(AdmProfesorActivity.this, DividerItemDecoration.VERTICAL));
                mRecyclerView.setAdapter(mAdapter);


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
