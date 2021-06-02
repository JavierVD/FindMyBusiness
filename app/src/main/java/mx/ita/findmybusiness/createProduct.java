package mx.ita.findmybusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createProduct extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private Uri selectedImage;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private int id_producto=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_create_product);
        Toolbar toolbar = findViewById(R.id.toolbareditarnegocio);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        String id_empresa=bundle.getString("id_empresa");
        mAuth = FirebaseAuth.getInstance();
        EditText txtNombreProducto = findViewById(R.id.txtNombreEditPerson);
        EditText txtMarca = findViewById(R.id.txtApellidosEditPerson);
        EditText txtDescripcion = findViewById(R.id.txtDescripcion);
        Spinner spiner = findViewById(R.id.spinner2);
        EditText txtMoney = findViewById(R.id.txtDineroEdit);
        EditText cantidad = findViewById(R.id.txtCantidad);
        Button subir = findViewById(R.id.btnSubirProducto);
        Button agregar = findViewById(R.id.btnActualizarfotoperfil);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        List<String> elementos = new ArrayList<>();
        Query query = myRef.child("clasifiprod").orderByChild("nombre");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot citi : dataSnapshot.getChildren())
                        elementos.add(citi.child("nombre").getValue().toString());
                } else {
                    Log.i("No data","Pito");
                }
                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(createProduct.this,
                        android.R.layout.simple_list_item_1, elementos);
                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spiner.setAdapter(adp1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria(v);
            }
        });

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtnombre = txtNombreProducto.getText().toString();
                String txtmarca = txtMarca.getText().toString();
                String txtdescipcion = txtDescripcion.getText().toString();
                String txtmoney = txtMoney.getText().toString();
                String txtcantidad = cantidad.getText().toString();
                String txtcatogoria= spiner.getSelectedItem().toString();
                Query query = myRef.child("productolist").orderByChild("id_producto");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot citi : dataSnapshot.getChildren())
                                id_producto = Integer.parseInt(citi.child("id_producto").getValue().toString());
                            Log.i("LA ID: ",id_producto+"");;
                            id_producto ++;
                        }else{
                            Toast.makeText(createProduct.this,"Valor :" + id_producto, Toast.LENGTH_LONG);
                            id_producto = 1;
                        }
                        Log.i("ID PRODUCTO",id_producto+"");
                        Log.i("empresa fuera:",id_producto+"");

                        Map<String, Object> soloE = new HashMap<>();
                        soloE.put("id_producto",id_producto);

                        mDatabase.child("productolist").child(id_producto+"").setValue(soloE).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                    Log.i("Nueva empresa", id_producto+"");
                                }else{
                                    Log.w("Error XD 1",task2.getException());
                                }
                            }
                        });
                        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("productos");
                        final StorageReference file_name = Folder.child("file"+selectedImage.getLastPathSegment());
                        file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {

                            Map<String, Object> map = new HashMap<>();
                            String IDnuevo = mAuth.getCurrentUser().getUid();
                            map.put("id_producto", id_producto);
                            map.put("nombre", txtnombre);
                            map.put("marca",txtmarca);
                            map.put("descripcion", txtdescipcion);
                            map.put("precio", txtmoney);
                            map.put("cantidad", txtcantidad);
                            map.put("categoria", txtcatogoria);
                            map.put("id_empresa", Integer.parseInt(id_empresa));
                            map.put("foto", String.valueOf(uri));
                            mDatabase.child("productos").child(id_producto+"").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if(task2.isSuccessful()){
                                        Intent intent = new Intent(createProduct.this, manageProducts.class);
                                        intent.putExtra("id_empresa",id_empresa);
                                        startActivity(intent);
                                    }else{
                                        Log.w("Error XD 1",task2.getException());
                                    }
                                }
                            });
                        }));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void abrirGaleria(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                SELECT_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        String filePath = null;
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();
                    if (requestCode == SELECT_FILE) {
                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(
                                        selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                            ImageView pic = findViewById(R.id.picPerfilEditar);
                            pic.setImageBitmap(bmp);
                        }
                    }
                }
                break;
        }
    }
}