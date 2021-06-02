package mx.ita.findmybusiness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class manageProducts extends AppCompatActivity {
    List<String> productos;
    List<ListElementEditProduct> elements;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String userID = mAuth.getCurrentUser().getUid();
    StorageReference storageRef = storage.getReference();
    ImageView btnConfNegocio;
    float prom = 0;
    Bitmap bitmap, logos;
    Drawable logo;
    String dato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        Bundle bundle = getIntent().getExtras();
        dato=bundle.getString("id_empresa");
        Button add = findViewById(R.id.btnAddProducto);
        btnConfNegocio = findViewById(R.id.btnConfNegocio);
        btnConfNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(manageProducts.this, editNegocio.class);
                i.putExtra("id_empresa", dato);
                startActivity(i);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent crear = new Intent(manageProducts.this, createProduct.class);
                crear.putExtra("id_empresa",dato);
                startActivity(crear);
            }
        });
        init();
    }
    public void init(){
        productos = new ArrayList<>();
        Query query = ref.child("productos").orderByChild("id_empresa");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot citi : dataSnapshot.getChildren()){
                        String id_empresa = citi.child("id_empresa").getValue().toString();
                        String id_producto = citi.child("id_producto").getValue().toString();
                        Log.i("String ALV", id_empresa+"");
                        if(id_empresa.equals(dato))
                            productos.add(id_producto);
                    }
                }
                Log.i("número de productos", productos.size()+"");
                Log.i("INIT","OK_________________-");
                elements = new ArrayList<>();

                for(String empresa : productos){
                    Query AXD = ref.child("productos").orderByChild("id_producto");
                    Log.i("Valores empresa","No me quieren!");
                    AXD.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot citi : dataSnapshot.getChildren()) {
                                    String id_producto = citi.child("id_producto").getValue(Integer.class)+"";
                                    if(id_producto.equals(empresa)){
                                        String nombre = citi.child("nombre").getValue(String.class);
                                        String descripcion = citi.child("descripcion").getValue(String.class);
                                        String marca = citi.child("marca").getValue(String.class);
                                        float precio = Float.parseFloat(citi.child("precio").getValue(String.class));
                                        int cantidad = Integer.parseInt(citi.child("cantidad").getValue(String.class));
                                        String categoria = citi.child("categoria").getValue(String.class);
                                        String uri = citi.child("foto").getValue(String.class);
                                        Log.i("Valores producto",id_producto + " " + nombre);
                                        Query de = ref.child("rateProducto");
                                        de.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot citi : dataSnapshot.getChildren()){
                                                        String points = citi.child("puntaje").getValue().toString();
                                                        String idE = citi.child("puntaje").getValue().toString();
                                                        if(idE == empresa){
                                                            prom+=Float.parseFloat(points);
                                                        }
                                                    }
                                                }
                                                Glide.with(manageProducts.this).asBitmap()
                                                        .load(uri)
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                bitmap = resource;
                                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                                byte[] byteArray = stream.toByteArray();
                                                                logos = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                                                Log.i("awevo","imagen cargada");
                                                                if(logos == null)
                                                                    Log.i("LAPUTAMADRE","IVAN");
                                                                else
                                                                    Log.i("Sapbeee","loquita");
                                                                elements.add(new ListElementEditProduct(logos, nombre, marca,precio, prom, cantidad,id_producto,descripcion,categoria));
                                                                Log.i("número de productos :D", productos.size()+"");
                                                                ListAdapterEditProduct listAdapter = new ListAdapterEditProduct(elements, manageProducts.this);
                                                                RecyclerView recyclerView = findViewById(R.id.recicladoraXD);
                                                                recyclerView.setHasFixedSize(true);
                                                                recyclerView.setLayoutManager(new LinearLayoutManager(manageProducts.this));
                                                                recyclerView.setAdapter(listAdapter);
                                                            }

                                                            @Override
                                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                            }
                                                        });

                                                prom = 0;
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(manageProducts.this,"Fallo la lectura: " + databaseError.getCode(),Toast.LENGTH_LONG);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(manageProducts.this,"Fallo la lectura: " + databaseError.getCode(),Toast.LENGTH_LONG);
            }
        });

    }
}