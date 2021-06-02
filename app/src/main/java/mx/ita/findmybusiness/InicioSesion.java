package mx.ita.findmybusiness;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;

import mx.ita.findmybusiness.ui.home.HomeFragment;

public class InicioSesion extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mCustomToken;
    private String correo = "", contra = "";
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(InicioSesion.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(InicioSesion.this, MainMenu.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "No se reconoce la huella digital",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ventana de acceso biométrico")
                .setSubtitle("Inicia sesión usando tu credencial biométrica")
                .setNegativeButtonText("Usar correo y contraseña")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        EditText email = (EditText) findViewById(R.id.editTextTextPersonName3);
        EditText password = (EditText) findViewById(R.id.editTextTextPersonName4);
        if (mAuth.getCurrentUser() != null) {
            ref.child("usuario").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String cemail = dataSnapshot.child("correo").getValue(String.class);
                        String contra = dataSnapshot.child("contrasena").getValue(String.class);
                        email.setText(cemail);
                        password.setText(contra);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(InicioSesion.this,"Fallo la lectura: " + databaseError.getCode(),Toast.LENGTH_LONG);
                }
            });
            biometricPrompt.authenticate(promptInfo);
        }
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = email.getText().toString();
                contra = password.getText().toString();
                if(correo.isEmpty() || contra.isEmpty()){
                    Toast.makeText(InicioSesion.this, "Complete todos los campos necesario", Toast.LENGTH_SHORT);
                }else{
                    loginUser();
                }
            }
        });
        Button btnRegistro = (Button) findViewById(R.id.button4);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistrarUsuario.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    private void loginUser(){
        mAuth.signInWithEmailAndPassword(correo, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences sharedPreferences = InicioSesion.this.getSharedPreferences("Modo", Context.MODE_PRIVATE);
                    String mode = sharedPreferences.getString("is", "off");
                    Log.i("Valoractual",mode);
                    if(mode.equals("on")){
                        startActivity(new Intent(InicioSesion.this, adminSeller.class));
                    }else{
                        startActivity(new Intent(InicioSesion.this, MainMenu.class));
                    }


                }else{
                    Toast.makeText(InicioSesion.this,"Error de inicio de sesión", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
