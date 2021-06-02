package mx.ita.findmybusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.Map;

public class editarPerfil extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private Uri selectedImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
    FirebaseUser usere = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_edit_product);
        String user = mAuth.getCurrentUser().getUid();
        String idUsuario = mAuth.getUid();
        mAuth = FirebaseAuth.getInstance();
        EditText txtNombre = findViewById(R.id.txtNombreEditPerson);
        EditText txtApellidos= findViewById(R.id.txtApellidosEditPerson);
        EditText txtContrasena1 = findViewById(R.id.txtEditPass);
        EditText txtContrasena2 = findViewById(R.id.txtConfirmPass);
        Button subir = findViewById(R.id.btnActualizarPerfil);
        Button agregar = findViewById(R.id.btnActualizarfotoperfil);
        picBox = findViewById(R.id.picPerfilEditar);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query consulta = myRef.child("usuario").child(user).orderByChild("nombre");
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot citi) {
                if (citi.exists()) {
                    String idtemp = citi.child("id_producto").getValue().toString();
                    if (idtemp.equals(id_producto)) {
                        id_empresa = citi.child("id_empresa").getValue().toString();
                        txtNombre.setText(citi.child("nombre").getValue().toString());
                        txtApellidos.setText(citi.child("apellidos").getValue().toString());
                        txtContrasena1.setText(citi.child("contrasena").getValue().toString());
                        txtContrasena2.setText(citi.child("contrasena").getValue().toString());
                        url = citi.child("perfil").getValue().toString();
                        Glide.with(editarPerfil.this).load(url)
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
                String txtnombre = txtNombre.getText().toString();
                String txtmarca = txtApellidos.getText().toString();
                String contra1 = txtContrasena1.getText().toString();
                String contra2 = txtContrasena2.getText().toString();
                if(contra1.equals(contra2)){
                    usere.updatePassword(contra1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Excelente!", "Password updated");
                            } else {
                                Log.d("Mal!", "Error password not updated");
                            }
                        }
                    });
                }

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

                Folder = firebaseStorage.getInstance().getReference().child("usuario");
                file_name = Folder.child("file"+selectedImage.getLastPathSegment());
                file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String, Object> map = new HashMap<>();
                    String IDnuevo = mAuth.getCurrentUser().getUid();
                    map.put("nombre", txtnombre);
                    map.put("apellidos",txtApellidos);
                    map.put("ID_user",user);
                    map.put("contrasena",contra1);
                    map.put("perfil", String.valueOf(uri));
                    mDatabase.child("usuario").child(user).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                Intent i = new Intent(editarPerfil.this, MainMenu.class);
                                startActivity(i);
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
