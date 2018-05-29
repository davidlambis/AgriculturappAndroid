package com.interedes.agriculturappv3.modules.comprador.productores.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.interedes.agriculturappv3.R;
import com.interedes.agriculturappv3.modules.models.producto.Producto;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EnuarMunoz on 28/05/18.
 */


public class PaginationProductoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    private boolean isLoadingAdded = false;

    private List<Producto> movieResults;
    private Context context;



    public PaginationProductoAdapter(Context context) {
        this.context = context;
        movieResults = new ArrayList<>();
    }

    public List<Producto> getMovies() {
        return movieResults;
    }

    public void setMovies(List<Producto> movieResults) {
        this.movieResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.content_list_producto_productor, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Producto result = movieResults.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;



                movieVH.txtNombreProductor.setText(result.getNombreProductor());

                String disponibilidad="";

                if(result.getStock().toString().contains(".0")){
                    disponibilidad=String.format(context.getString(R.string.price_empty_signe),
                            result.getStock());
                }else{
                    disponibilidad=result.getStock().toString();
                }

                movieVH.txtDisponibilidad.setText(String.format("%s: %s %s", result.getNombreCalidad(),disponibilidad,result.getNombreUnidadMedidaCantidad()));
                movieVH.txtProducto.setText(result.getNombre());

                movieVH.txtPrecio.setText(String.format(context.getString(R.string.price_producto),
                        result.getPrecio(),result.getPrecioUnidadMedida()));

                movieVH.txtUbicacion.setText(String.format("%s / %s", result.getCiudad(), result.getDepartamento()));



                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Producto r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Producto> moveResults) {
        for (Producto result : moveResults) {
            add(result);
        }
    }

    public void remove(Producto r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Producto());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Producto result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Producto getItem(int position) {
        return movieResults.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder {
        private TextView txtNombreProductor;
        private TextView txtProducto;
        private TextView txtUbicacion; // displays "year | language"
        private TextView txtDisponibilidad;
        private TextView txtPrecio;

        public MovieVH(View itemView) {
            super(itemView);

            txtNombreProductor = (TextView) itemView.findViewById(R.id.txtNombreProductor);
            txtProducto = (TextView) itemView.findViewById(R.id.txtProducto);
            txtUbicacion = (TextView) itemView.findViewById(R.id.txtUbicacion);
            txtDisponibilidad = (TextView) itemView.findViewById(R.id.txtDisponibilidad);
            txtPrecio = (TextView) itemView.findViewById(R.id.txtPrecio);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}

/*
public class PaginationProductoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";

    private List<Producto> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;

    public PaginationProductoAdapter(Context context) {
        this.context = context;
        movieResults = new ArrayList<>();
    }

    public List<Producto> getMovies() {
        return movieResults;
    }

    public void setMovies(List<Producto> movieResults) {
        this.movieResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.content_list_producto_productor, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Producto result = movieResults.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;



                movieVH.txtNombreProductor.setText(result.getNombreProductor());

                String disponibilidad="";

                if(result.getStock().toString().contains(".0")){
                    disponibilidad=String.format(context.getString(R.string.price_empty_signe),
                            result.getStock());
                }else{
                    disponibilidad=result.getStock().toString();
                }

                movieVH.txtDisponibilidad.setText(String.format("%s: %s %s", result.getNombreCalidad(),disponibilidad,result.getNombreUnidadMedidaCantidad()));
                movieVH.txtProducto.setText(result.getNombre());

                movieVH.txtPrecio.setText(String.format(context.getString(R.string.price_producto),
                    result.getPrecio(),result.getPrecioUnidadMedida()));

                movieVH.txtUbicacion.setText(String.format("%s / %s", result.getCiudad(), result.getDepartamento()));



                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }



    public void add(Producto r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Producto> moveResults) {
        for (Producto result : moveResults) {
            add(result);
        }
    }

    public void remove(Producto r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Producto());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Producto result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Producto getItem(int position) {
        return movieResults.get(position);
    }


    protected class MovieVH extends RecyclerView.ViewHolder {


        private TextView txtNombreProductor;
        private TextView txtProducto;
        private TextView txtUbicacion; // displays "year | language"
        private TextView txtDisponibilidad;
        private TextView txtPrecio;

        public MovieVH(View itemView) {
            super(itemView);

            txtNombreProductor = (TextView) itemView.findViewById(R.id.txtNombreProductor);
            txtProducto = (TextView) itemView.findViewById(R.id.txtProducto);
            txtUbicacion = (TextView) itemView.findViewById(R.id.txtUbicacion);
            txtDisponibilidad = (TextView) itemView.findViewById(R.id.txtDisponibilidad);
            txtPrecio = (TextView) itemView.findViewById(R.id.txtPrecio);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}*/