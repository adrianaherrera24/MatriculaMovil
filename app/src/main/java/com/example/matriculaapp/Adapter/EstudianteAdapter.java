package com.example.matriculaapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matriculaapp.LogicaNegocio.Estudiante;
import com.example.matriculaapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.MyViewHolder>
        implements Filterable {

    private List<Estudiante> listaEstudiantes;
    private List<Estudiante> alumnoListFiltered;
    private EstudianteAdapterListener listener; // Por medio de la interfaz creada
    private Estudiante ItemEliminar;

    // NUEVA CLASE ANIDADA
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView nombre, txtid, correo, telefono;
        public RelativeLayout vistaPrincipal, vistaEditar, vistaBorrar;

        // Se debe crear un constructor super
        public MyViewHolder(View view) {
            super(view);
            // busca dentro del layout donde tiene que mostrar la informacion => findViewById
            nombre = view.findViewById(R.id.txt_nombre_est);
            txtid = view.findViewById(R.id.txt_id_est);
            correo = view.findViewById(R.id.txt_email_est);
            telefono = view.findViewById(R.id.txt_telefono_est);

            vistaPrincipal = view.findViewById(R.id.vista_principal); // foreground
            vistaEditar = view.findViewById(R.id.vista_editar);
            vistaBorrar  = view.findViewById(R.id.vista_borrar);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(alumnoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    } // TERMINA LA CLASE MyViewHolder

    //  Constructor de EstudianteAdapter
    public EstudianteAdapter(List<Estudiante> listaEstudiantes, EstudianteAdapterListener listener){
        this.listaEstudiantes = listaEstudiantes;
        this.listener = listener;
        // init filter
        this.alumnoListFiltered = listaEstudiantes;
    }

    // METODOS QUE SE ORIGINAN CON LA HERENCIA DE LA CLASE PRINCIPAL
    @Override
    public EstudianteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // asigna un layout XML en una vista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_est, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EstudianteAdapter.MyViewHolder holder, int position) {
        final Estudiante estudiante = alumnoListFiltered.get(position);
        holder.nombre.setText(estudiante.getNombre());
        holder.txtid.setText(estudiante.getId());
        holder.correo.setText("Correo: "+estudiante.getEmail());
        holder.telefono.setText(" Teléfono: "+estudiante.getTelefono());
    }

    @Override
    public int getItemCount() {
        return alumnoListFiltered.size();
    }

    /***********************************************************************************/
    public void removeItem(int position) { //Para eliminar datos
        ItemEliminar = alumnoListFiltered.remove(position);
        Iterator<Estudiante> iter = listaEstudiantes.iterator();
        while (iter.hasNext()) {
            Estudiante aux = iter.next();
            if (ItemEliminar.equals(aux))
                iter.remove();
        }
        // notify item removed
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) { // Para restaurar si hace UNDO

        if (alumnoListFiltered.size() == listaEstudiantes.size()) {
            alumnoListFiltered.add(position, ItemEliminar);
        } else {
            alumnoListFiltered.add(position, ItemEliminar);
            listaEstudiantes.add(ItemEliminar);
        }
        notifyDataSetChanged();
        // notify item added by position
        notifyItemInserted(position);
    }
    /**********************************************************************************************/

    public void onItemMove(int fromPosition, int toPosition) {
        if (listaEstudiantes.size() == alumnoListFiltered.size()) { // without filter
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listaEstudiantes, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listaEstudiantes, i, i - 1);
                }
            }
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(alumnoListFiltered, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(alumnoListFiltered, i, i - 1);
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /// Utilizado para cuando se hace un swipe saber si edita o borra
    public Estudiante getSwipedItem(int index) {
        if (this.listaEstudiantes.size() == this.alumnoListFiltered.size()) { //not filtered yet
            return listaEstudiantes.get(index);
        } else {
            return alumnoListFiltered.get(index);
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
                    alumnoListFiltered = listaEstudiantes;
                } else {
                    List<Estudiante> filteredList = new ArrayList<>();
                    for (Estudiante row : listaEstudiantes) {
                        // filter use two parameters
                        if (row.getId().toLowerCase().contains(charString.toLowerCase()) || row.getNombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    alumnoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = alumnoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                alumnoListFiltered = (ArrayList<Estudiante>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Creacion de interfaz
    public interface EstudianteAdapterListener {
        void onContactSelected(Estudiante alumno);
    }
}
