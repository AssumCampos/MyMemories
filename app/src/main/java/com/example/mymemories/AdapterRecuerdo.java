package com.example.mymemories;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdapterRecuerdo extends RecyclerView.Adapter<AdapterRecuerdo.ViewHolder> implements Filterable {
    private ArrayList<Recuerdo> recuerdoAL;
    Context context;
    public View.OnClickListener listener;

    private ArrayList<Recuerdo> recuerdosAL_filtered;
    public AdapterRecuerdo(Context c, ArrayList recuerdoAL) {
        context = c;
        this.recuerdoAL = recuerdoAL;
        this.recuerdosAL_filtered = recuerdoAL;
    }


    @Override
    public int getItemCount() {
        return recuerdosAL_filtered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    recuerdosAL_filtered = recuerdoAL;
                } else {
                    ArrayList<Recuerdo> filteredList = new ArrayList<>();
                    for (Recuerdo row : recuerdoAL) {
                        if (row.titulo.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    recuerdosAL_filtered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = recuerdosAL_filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                recuerdosAL_filtered = (ArrayList<Recuerdo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, fecha, lugar;
        ImageView imagen;

        public ViewHolder(View view) {
            super(view);
            titulo = (TextView) view.findViewById(R.id.idTituloList);
            imagen = (ImageView) view.findViewById(R.id.idImageViewList);
            lugar = (TextView) view.findViewById(R.id.idLugarList);
            fecha = (TextView) view.findViewById(R.id.idFechaList);
            view.setTag(this);
        }

        public TextView getTextViewTitulo() {
            return titulo;
        }

        public ImageView getImageViewImagen() {
            return imagen;
        }

        public TextView getTextViewLugar() {
            return lugar;
        }

        public TextView getTextViewFecha() {
            return fecha;
        }
    }

    @NonNull
    @Override
    public AdapterRecuerdo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recuerdo, parent, false);
        view.setOnClickListener(this.listener);
        return new AdapterRecuerdo.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecuerdo.ViewHolder holder,
                                 int position) {
        // Titulo
        String tituloRecuerdo = recuerdosAL_filtered.get(position).getTitulo();
        holder.getTextViewTitulo().setText(tituloRecuerdo);
        // Fecha
        String fechaRecuerdo = recuerdosAL_filtered.get(position).getFecha();
        holder.getTextViewLugar().setText(fechaRecuerdo);
        // Lugar
        String lugarRecuerdo = recuerdosAL_filtered.get(position).getLugar();
        holder.getTextViewFecha().setText(lugarRecuerdo);
        // Imagen
        Bitmap imagenRecuerdo = recuerdosAL_filtered.get(position).getImage();
        holder.getImageViewImagen().setImageBitmap(imagenRecuerdo);
    }

    public Recuerdo getRecuerdoByPosition(int position) {
        return recuerdosAL_filtered.get(position);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.listener = itemClickListener;
    }
}
