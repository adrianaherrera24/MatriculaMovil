package com.example.matriculaapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matriculaapp.LogicaNegocio.Carrera;
import com.example.matriculaapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CarreraAdapter extends RecyclerView.Adapter<CarreraAdapter.MyViewHolder>
        implements Filterable {

    private List<Carrera> listaCarreras;
    private List<Carrera> filtroListaCarreras;
    private CarreraAdapterListener listener; // Por medio de la interfaz creada
    private Carrera ItemEliminar;

    // NUEVA CLASE ANIDADA
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView codigo, nombre, titulo;
        public RelativeLayout vistaPrincipal, vistaEditar, vistaBorrar;

        // Se debe crear un constructor super
        public MyViewHolder(View view) {
            super(view);
            // busca dentro del layout donde tiene que mostrar la informacion => findViewById
            codigo = view.findViewById(R.id.txt_codigo_car);
            nombre = view.findViewById(R.id.txt_nombre_car);
            titulo = view.findViewById(R.id.txt_titulo_car);

            vistaPrincipal = view.findViewById(R.id.vista_principal); // foreground
            vistaEditar = view.findViewById(R.id.vista_editar);
            vistaBorrar  = view.findViewById(R.id.vista_borrar);
        }
    } // TERMINA LA CLASE MyViewHolder

    //  Constructor de EstudianteAdapter
    public CarreraAdapter(List<Carrera> listaCarreras, CarreraAdapterListener listener){
        this.listaCarreras = listaCarreras;
        this.listener = listener;
        // init filter
        this.filtroListaCarreras = listaCarreras;
    }

    // METODOS QUE SE ORIGINAN CON LA HERENCIA DE LA CLASE PRINCIPAL
    @Override
    public CarreraAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // asigna un layout XML en una vista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_car, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarreraAdapter.MyViewHolder holder, int position) {
        final Carrera carrera = filtroListaCarreras.get(position);
        holder.codigo.setText(carrera.getCodigo());
        holder.nombre.setText(carrera.getNombre());
        holder.titulo.setText(carrera.getTitulo());
    }

    @Override
    public int getItemCount() {
        return filtroListaCarreras.size();
    }

    /***********************************************************************************/
    public void removeItem(int position) { //Para eliminar datos
        ItemEliminar = filtroListaCarreras.remove(position);
        Iterator<Carrera> iter = listaCarreras.iterator();
        while (iter.hasNext()) {
            Carrera aux = iter.next();
            if (ItemEliminar.equals(aux))
                iter.remove();
        }
        // notify item removed
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) { // Para restaurar si hace UNDO

        if (filtroListaCarreras.size() == listaCarreras.size()) {
            filtroListaCarreras.add(position, ItemEliminar);
        } else {
            filtroListaCarreras.add(position, ItemEliminar);
            listaCarreras.add(ItemEliminar);
        }
        notifyDataSetChanged();
        // notify item added by position
        notifyItemInserted(position);
    }
    /**********************************************************************************************/

    public void onItemMove(int fromPosition, int toPosition) {
        if (listaCarreras.size() == filtroListaCarreras.size()) { // without filter
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listaCarreras, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listaCarreras, i, i - 1);
                }
            }
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(filtroListaCarreras, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(filtroListaCarreras, i, i - 1);
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /// Utilizado para cuando se hace un swipe saber si edita o borra
    public Carrera getSwipedItem(int index) {
        if (this.listaCarreras.size() == this.filtroListaCarreras.size()) { //not filtered yet
            return listaCarreras.get(index);
        } else {
            return filtroListaCarreras.get(index);
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
                    filtroListaCarreras = listaCarreras;
                } else {
                    List<Carrera> filteredList = new ArrayList<>();
                    for (Carrera row : listaCarreras) {
                        // filter use two parameters
                        if (row.getCodigo().toLowerCase().contains(charString.toLowerCase()) || row.getNombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtroListaCarreras = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtroListaCarreras;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtroListaCarreras = (ArrayList<Carrera>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Creacion de interfaz
    public interface CarreraAdapterListener {
        void onContactSelected(Carrera carrera);
    }
}
