package mx.ita.findmybusiness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrarUsuario extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private EditText nombreText, apellidosText,pass1Text, pass2Text, emailText;
    private String snombreText, sapellidosText,spass1Text, spass2Text, semailText;
    private FirebaseAuth mAuth;
    private Intent intent;
    private Uri selectedImageUri = null;
    private Uri selectedImage;
    private Intent datas;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("usuario");
        nombreText = (EditText) findViewById(R.id.txtNombre);
        apellidosText = (EditText) findViewById(R.id.txtApellidos);
        emailText = (EditText) findViewById(R.id.txtEmail);
        pass1Text = (EditText) findViewById(R.id.txtPassword);
        pass2Text = (EditText) findViewById(R.id.txtPassword2);
        CircleImageView cargarImagen = (CircleImageView) findViewById(R.id.btnSubir);
        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snombreText = nombreText.getText().toString();
                sapellidosText = apellidosText.getText().toString();
                semailText = emailText.getText().toString();
                spass1Text = pass1Text.getText().toString();
                spass2Text = pass2Text.getText().toString();
                intent = new Intent(v.getContext(), MainMenu.class);
                if(!snombreText.isEmpty() && !semailText.isEmpty() && !spass1Text.isEmpty() && !spass2Text.isEmpty()){
                    if(spass1Text.compareTo(spass2Text)==0){
                        registrarUsuario();
                    }else{
                        Toast.makeText(RegistrarUsuario.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegistrarUsuario.this, "Requerimos que llenes todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria(v);
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
                            ImageView mImg = (ImageView) findViewById(R.id.picFoto);
                            mImg.setImageBitmap(bmp);
                        }
                    }
                }
                break;
        }
    }


    private void registrarUsuario(){
        mAuth.createUserWithEmailAndPassword(semailText,spass1Text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    StorageReference Folder = FirebaseStorage.getInstance().getReference().child("usuario");
                    final StorageReference file_name = Folder.child("file"+selectedImage.getLastPathSegment());
                    file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> map = new HashMap<>();
                        String IDnuevo = mAuth.getCurrentUser().getUid();
                        map.put("ID_user", IDnuevo);
                        map.put("nombre", snombreText);
                        map.put("apellidos",sapellidosText);
                        map.put("correo", semailText);
                        map.put("contrasena", spass1Text);
                        map.put("perfil", String.valueOf(uri));
                        mDatabase.child("usuario").child(IDnuevo).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                    startActivity(intent);
                                }else{
                                    Log.w("Error XD 1",task2.getException());
                                    Toast.makeText(RegistrarUsuario.this, "Cagaste", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }));
                }else{
                    Toast.makeText(RegistrarUsuario.this, "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                    Log.w("Error",task.getException());
                }
            }
        });
    }
}