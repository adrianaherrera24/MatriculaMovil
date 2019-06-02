package com.example.matriculaapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matriculaapp.LogicaNegocio.Profesor;
import com.example.matriculaapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProfesorAdapter extends RecyclerView.Adapter<ProfesorAdapter.MyViewHolder>
        implements Filterable {

    private List<Profesor> listaProfesores;
    private List<Profesor> filtroListaProfesores;
    private ProfesorAdapterListener listener; // Por medio de la interfaz creada
    private Profesor ItemEliminar;

    // NUEVA CLASE ANIDADA
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView cedula, nombre, correo, telefono;
        public RelativeLayout vistaPrincipal, vistaEditar, vistaBorrar;

        // Se debe crear un constructor super
        public MyViewHolder(View view) {
            super(view);
            // busca dentro del layout donde tiene que mostrar la informacion => findViewById
            cedula = view.findViewById(R.id.txt_id_prof);
            nombre = view.findViewById(R.id.txt_nombre_prof);
            correo = view.findViewById(R.id.txt_email_prof);
            telefono = view.findViewById(R.id.txt_telefono_prof);

            vistaPrincipal = view.findViewById(R.id.vista_principal); // foreground
            vistaEditar = view.findViewById(R.id.vista_editar);
            vistaBorrar  = view.findViewById(R.id.vista_borrar);
        }
    } // TERMINA LA CLASE MyViewHolder

    //  Constructor de ProfesorAdapter
    public ProfesorAdapter(List<Profesor> listaProfesores, ProfesorAdapterListener listener){
        this.listaProfesores = listaProfesores;
        this.listener = listener;
        // init filter
        this.filtroListaProfesores = listaProfesores;
    }

    // METODOS QUE SE ORIGINAN CON LA HERENCIA DE LA CLASE PRINCIPAL
    @Override
    public ProfesorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // asigna un layout XML en una vista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_prof, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfesorAdapter.MyViewHolder holder, int position) {
        final Profesor profesor = filtroListaProfesores.get(position);
        holder.cedula.setText(profesor.getId());
        holder.nombre.setText(profesor.getNombre());
        holder.correo.setText("Correo: " + profesor.getEmail());
        holder.telefono.setText(" Teléfono: " + profesor.getTelefono());
    }

    @Override
    public int getItemCount() {
        return filtroListaProfesores.size();
    }

    /***********************************************************************************/
    public void removeItem(int position) { //Para eliminar datos
        ItemEliminar = filtroListaProfesores.remove(position);
        Iterator<Profesor> iter = listaProfesores.iterator();
        while (iter.hasNext()) {
            Profesor aux = iter.next();
            if (ItemEliminar.equals(aux))
                iter.remove();
        }
        // notify item removed
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) { // Para restaurar si hace UNDO

        if (filtroListaProfesores.size() == listaProfesores.size()) {
            filtroListaProfesores.add(position, ItemEliminar);
        } else {
            filtroListaProfesores.add(position, ItemEliminar);
            listaProfesores.add(ItemEliminar);
        }
        notifyDataSetChanged();
        // notify item added by position
        notifyItemInserted(position);
    }
    /**********************************************************************************************/

    public void onItemMove(int fromPosition, int toPosition) {
        if (listaProfesores.size() == filtroListaProfesores.size()) { // without filter
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listaProfesores, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listaProfesores, i, i - 1);
                }
            }
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(filtroListaProfesores, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(filtroListaProfesores, i, i - 1);
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /// Utilizado para cuando se hace un swipe saber si edita o borra
    public Profesor getSwipedItem(int index) {
        if (this.listaProfesores.size() == this.filtroListaProfesores.size()) { //not filtered yet
            return listaProfesores.get(index);
        } else {
            return filtroListaProfesores.get(index);
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
                    filtroListaProfesores = listaProfesores;
                } else {
                    List<Profesor> filteredList = new ArrayList<>();
                    for (Profesor row : listaProfesores) {
                        // filter use two parameters
                        if (row.getId().toLowerCase().contains(charString.toLowerCase()) || row.getNombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtroListaProfesores = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtroListaProfesores;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtroListaProfesores = (ArrayList<Profesor>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Creacion de interfaz
    public interface ProfesorAdapterListener {
        void onContactSelected(Profesor profesor);
    }
}
