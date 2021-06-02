package mx.ita.findmybusiness;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterEditProduct extends RecyclerView.Adapter<ListAdapterEditProduct.ViewHolder>{
    private List<ListElementEditProduct> mData;
    private LayoutInflater mInflater;
    private Context context;
    public ListAdapterEditProduct(Context context){
        this.context = context;
    }
    public ListAdapterEditProduct(List<ListElementEditProduct> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }
    @Override
    public int getItemCount(){return mData.size();}
    @Override
    public ListAdapterEditProduct.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.card_edit_producto, null);
        return new ListAdapterEditProduct.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ListAdapterEditProduct.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }
    public void setItems(List<ListElementEditProduct> items){mData= items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        RatingBar rate;
        TextView descripcion;
        TextView precio;
        TextView disponible;
        TextView categoria;
        String id_producto;
        ViewHolder(View ItemView){
            super(ItemView);
            image = ItemView.findViewById(R.id.picProductoCard);
            name = ItemView.findViewById(R.id.txtNombreEditPerson);
            rate = ItemView.findViewById(R.id.ratingCard);
            categoria = ItemView.findViewById(R.id.txtCategorias);
            descripcion = ItemView.findViewById(R.id.txtDescripcion);
            precio = ItemView.findViewById(R.id.txtPrecio);
            disponible = ItemView.findViewById(R.id.txtStock);
            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Log.i("id",id_producto);
                        Intent i=new Intent(context, editProduct.class);
                        i.putExtra("id_producto", id_producto);
                        context.startActivity(i);
                }
            });
        }
        void bindData(final ListElementEditProduct item){
            image.setImageBitmap(item.getImage());
            name.setText(item.getName());
            rate.setRating(item.getPromedio());
            descripcion.setText(item.getDescripcion());
            categoria.setText(item.getCategoria());
            precio.setText(item.getPrecio()+"");
            disponible.setText(item.getStock()+ " unidades");
            id_producto = item.getId_producto();
        }
    }


}
