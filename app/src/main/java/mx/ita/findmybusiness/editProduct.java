package mx.ita.findmybusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editProduct extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private Uri selectedImage;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private int id_producto=0;
    StorageReference Folder;
    StorageReference file_name;
    Toolbar toolbar;
    String id_empresa;
    String url;
    String urlnew;
    ImageView picBox;
    StorageReference photoRef;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menutoolbareditproduct,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_delete_product:
                //Hacer algo cuando presionen Home
                break;
            case R.id.item_update_product:
                Log.i("XD","JEJEJEJEJEJE");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbareditarnegocio);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_edit_product);
        Bundle bundle = getIntent().getExtras();
        String id_producto =bundle.getString("id_producto");
        mAuth = FirebaseAuth.getInstance();
        EditText txtNombreProducto = findViewById(R.id.txtNombreEditPerson);
        EditText txtMarca = findViewById(R.id.txtApellidosEditPerson);
        EditText txtDescripcion = findViewById(R.id.txtDescripcion);
        Spinner spiner = findViewById(R.id.spinner2);
        EditText txtMoney = findViewById(R.id.txtDineroEdit);
        EditText cantidad = findViewById(R.id.txtCantidad);
        Button subir = findViewById(R.id.btnSubirProducto);
        Button agregar = findViewById(R.id.btnActualizarfotoperfil);
        picBox = findViewById(R.id.picPerfilEditar);
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
                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(editProduct.this,
                        android.R.layout.simple_list_item_1, elementos);
                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spiner.setAdapter(adp1);
                Query consulta = myRef.child("productos").orderByChild("nombre");
                consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot citi : dataSnapshot.getChildren()){
                                String idtemp = citi.child("id_producto").getValue().toString();
                                if(idtemp.equals(id_producto)){
                                    id_empresa = citi.child("id_empresa").getValue().toString();
                                    txtNombreProducto.setText(citi.child("nombre").getValue().toString());
                                    txtMarca.setText(citi.child("marca").getValue().toString());
                                    txtDescripcion.setText(citi.child("descripcion").getValue().toString());
                                    txtMoney.setText(citi.child("precio").getValue().toString());
                                    cantidad.setText(citi.child("cantidad").getValue().toString());
                                    int spinnerPosition = adp1.getPosition(citi.child("categoria").getValue().toString());
                                    spiner.setSelection(spinnerPosition);
                                    url = citi.child("foto").getValue().toString();
                                    Glide.with(editProduct.this).load(url)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    return false;
                                                }
                                            }).into(picBox);
                                }
                            }

                        } else {
                            Log.i("No data","Pito");
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                photoRef = firebaseStorage.getReferenceFromUrl(url);
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d("XD", "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d("XD", "onFailure: did not delete file");
                    }
                });

                Folder = firebaseStorage.getInstance().getReference().child("productos");
                file_name = Folder.child("file"+selectedImage.getLastPathSegment());
                file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String, Object> map = new HashMap<>();
                    String IDnuevo = mAuth.getCurrentUser().getUid();
                    map.put("nombre", txtnombre);
                    map.put("id_empresa",Integer.parseInt(id_empresa));
                    map.put("id_producto",Integer.parseInt(id_producto));
                    map.put("marca",txtmarca);
                    map.put("descripcion", txtdescipcion);
                    map.put("precio", txtmoney);
                    map.put("cantidad", txtcantidad);
                    map.put("categoria", txtcatogoria);
                    map.put("foto", String.valueOf(uri));
                    mDatabase.child("productos").child(id_producto+"").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                Query select = myRef.child("productos").orderByChild("id_empresa");
                                select.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot citi : dataSnapshot.getChildren()){
                                                String tempo =citi.child("id_empresa").getValue().toString();
                                                String tempo2 =citi.child("id_producto").getValue().toString();
                                                if(tempo2.equals(id_producto))
                                                    id_empresa = tempo;
                                            }
                                        } else {
                                            Log.i("No data","Pito");
                                        }
                                        Intent intent = new Intent(editProduct.this, manageProducts.class);
                                        intent.putExtra("id_empresa",id_empresa);

                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                Log.w("Error XD 1",task2.getException());
                            }
                        }
                    });
                }));
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