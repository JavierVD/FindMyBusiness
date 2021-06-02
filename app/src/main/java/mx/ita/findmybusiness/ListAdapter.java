package mx.ita.findmybusiness;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;
    public ListAdapter(Context context){
        this.context = context;
    }
    public ListAdapter(List<ListElement> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }
    @Override
    public int getItemCount(){return mData.size();}
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.card_negocio, null);
        return new ListAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }
    public void setItems(List<ListElement> items){mData= items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView direc;
        RatingBar rate;
        CheckBox servicio;
        CheckBox estado;
        String id;

        ViewHolder(View ItemView){
            super(ItemView);
            image = ItemView.findViewById(R.id.picProductoCard);
            name = ItemView.findViewById(R.id.txtNombreEditPerson);
            direc = ItemView.findViewById(R.id.txtCategorias);
            rate = ItemView.findViewById(R.id.ratingCard);
            servicio = ItemView.findViewById(R.id.checkServicioCard);
            estado = ItemView.findViewById(R.id.checkOpenCard);
            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Log.i("id",id);
                        Intent i=new Intent(context, manageProducts.class);
                        i.putExtra("id_empresa", id);
                        context.startActivity(i);
                }
            });
        }
        void bindData(final ListElement item){
            image.setImageBitmap(item.getBitmap());
            name.setText(item.getName());
            direc.setText(item.getDireccion());
            rate.setRating(item.getPromedio());
            servicio.setChecked(item.isServicio());
            estado.setChecked(item.isEstado());
            id = item.getId();
        }
    }


}
