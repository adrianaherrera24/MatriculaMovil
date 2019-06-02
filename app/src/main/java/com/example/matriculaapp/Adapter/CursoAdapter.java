package com.example.matriculaapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matriculaapp.LogicaNegocio.Curso;
import com.example.matriculaapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.MyViewHolder>
        implements Filterable {

    private List<Curso> listaCursos;
    private List<Curso> filtroListaCursos;
    private CursoAdapterListener listener; // Por medio de la interfaz creada
    private Curso ItemEliminar;

    // NUEVA CLASE ANIDADA
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView codigo, nombre, creditos, horas;
        public RelativeLayout vistaPrincipal, vistaEditar, vistaBorrar;

        // Se debe crear un constructor super
        public MyViewHolder(View view) {
            super(view);
            // busca dentro del layout donde tiene que mostrar la informacion => findViewById
            codigo = view.findViewById(R.id.txt_codigo_cur);
            nombre = view.findViewById(R.id.txt_nombre_cur);
            creditos = view.findViewById(R.id.txt_creditos);
            horas = view.findViewById(R.id.txt_horas);

            vistaPrincipal = view.findViewById(R.id.vista_principal); // foreground
            vistaEditar = view.findViewById(R.id.vista_editar);
            vistaBorrar  = view.findViewById(R.id.vista_borrar);
        }
    } // TERMINA LA CLASE MyViewHolder

    //  Constructor de ProfesorAdapter
    public CursoAdapter(List<Curso> listaCursos, CursoAdapterListener listener){
        this.listaCursos = listaCursos;
        this.listener = listener;
        // init filter
        this.filtroListaCursos = listaCursos;
    }

    // METODOS QUE SE ORIGINAN CON LA HERENCIA DE LA CLASE PRINCIPAL
    @Override
    public CursoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // asigna un layout XML en una vista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CursoAdapter.MyViewHolder holder, int position) {
        final Curso curso = filtroListaCursos.get(position);
        holder.codigo.setText(curso.getId());
        holder.nombre.setText(curso.getNombre());
        holder.creditos.setText("Créditos: " + curso.getCreditos());
        holder.horas.setText("  Horas Semalanes: " + curso.getHorasSemanales());
    }

    @Override
    public int getItemCount() {
        return filtroListaCursos.size();
    }

    /***********************************************************************************/
    public void removeItem(int position) { //Para eliminar datos
        ItemEliminar = filtroListaCursos.remove(position);
        Iterator<Curso> iter = listaCursos.iterator();
        while (iter.hasNext()) {
            Curso aux = iter.next();
            if (ItemEliminar.equals(aux))
                iter.remove();
        }
        // notify item removed
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) { // Para restaurar si hace UNDO

        if (filtroListaCursos.size() == listaCursos.size()) {
            filtroListaCursos.add(position, ItemEliminar);
        } else {
            filtroListaCursos.add(position, ItemEliminar);
            listaCursos.add(ItemEliminar);
        }
        notifyDataSetChanged();
        // notify item added by position
        notifyItemInserted(position);
    }
    /**********************************************************************************************/

    public void onItemMove(int fromPosition, int toPosition) {
        if (listaCursos.size() == filtroListaCursos.size()) { // without filter
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listaCursos, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listaCursos, i, i - 1);
                }
            }
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(filtroListaCursos, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(filtroListaCursos, i, i - 1);
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /// Utilizado para cuando se hace un swipe saber si edita o borra
    public Curso getSwipedItem(int index) {
        if (this.listaCursos.size() == this.filtroListaCursos.size()) { //not filtered yet
            return listaCursos.get(index);
        } else {
            return filtroListaCursos.get(index);
        }
    }

    // METODOS QUE SE ORIGINAN CON LA IMPLEMENTACION DE Filterable
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtroListaCursos = listaCursos;
                } else {
                    List<Curso> filteredList = new ArrayList<>();
                    for (Curso row : listaCursos) {
                        // filter use two parameters
                        if (row.getId().toLowerCase().contains(charString.toLowerCase()) || row.getNombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtroListaCursos = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtroListaCursos;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtroListaCursos = (ArrayList<Curso>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Creacion de interfaz
    public interface CursoAdapterListener {
        void onContactSelected(Curso curso);
    }
}
