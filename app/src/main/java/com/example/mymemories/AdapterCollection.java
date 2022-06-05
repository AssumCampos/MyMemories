package com.example.mymemories;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.ViewHolder> {
    private ArrayList<String> collectionAL;
    Context context;
    public View.OnClickListener listener;

    public AdapterCollection(Context c, ArrayList collectionAL) {
        context = c;
        this.collectionAL = collectionAL;
    }

    @Override
    public int getItemCount() {
        return collectionAL.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreColleccion;
        ImageView imagen;

        public ViewHolder(View view) {
            super(view);
            nombreColleccion = (TextView) view.findViewById(R.id.idColeccionNombre);
            imagen = (ImageView) view.findViewById(R.id.idColeccionImagen);
            view.setTag(this);
        }

        public TextView getTextViewNombreColeccion() {
            return nombreColleccion;
        }

        public ImageView getImageViewImagenColeccion() { return imagen; }

    }
    @NonNull
    @Override
    public AdapterCollection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_colecction, parent, false);
        view.setOnClickListener(this.listener);
        return new AdapterCollection.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCollection.ViewHolder holder,
                                 int position) {
        // Nombre Colección
        String nombreColeccion = collectionAL.get(position);
        holder.getTextViewNombreColeccion().setText(nombreColeccion);
        // Imagen
        //Bitmap imagenRecuerdo = collectionAL.get(position).getImage();
        //holder.getImageViewImagen().setImageBitmap(imagenRecuerdo);
    }

    public String getCollectionByPosition(int position) {
        return collectionAL.get(position);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.listener = itemClickListener;
    }
}
