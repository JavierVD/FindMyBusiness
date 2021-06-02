package mx.ita.findmybusiness;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class adminSeller extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    List<String> empresas;
    List<ListElement> elements;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String userID = mAuth.getCurrentUser().getUid();
    StorageReference
            storageRef = storage.getReference();
    float prom = 0;
    Bitmap bitmap, logos;
    Drawable logo;
    Button addnego;
    private GestureLibrary libreria;
    private TextView salida;

    public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {
        ArrayList<Prediction> predictions=libreria.recognize(gesture);

        if(predictions.size()>0) {
            String comando = predictions.get(0).name;
            if (comando.equals("mapa")) {
                Intent i = new Intent(adminSeller.this, MainMenu.class);
                startActivity(i);
                finish();
            } else if (comando.equals("conf")) {
                Intent i = new Intent(adminSeller.this, configuracion.class);
                startActivity(i);
                finish();
            } else if (comando.equals("idioma")) {
                Intent i = new Intent(adminSeller.this, idiomas.class);
                startActivity(i);
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_seller);
        Toolbar toolbar = findViewById(R.id.toolbaradminseller);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(adminSeller.this,MainMenu.class);
               startActivity(i);
                finish();
            }
        });

        libreria = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if (!libreria.load()) {
            finish();
        }
        GestureOverlayView gesturesView = (GestureOverlayView) findViewById(R.id.gestures);
        gesturesView.addOnGesturePerformedListener(adminSeller.this);


        addnego = findViewById(R.id.btnAddNegocio);
        addnego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(adminSeller.this, registrarNegocio.class);
                startActivity(i);
            }
        });



        ImageView btnsettings = findViewById(R.id.btnConfigGeneralus);
        btnsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(adminSeller.this, configuracion.class);
                startActivity(i);
            }
        });
        init();

    }
    public void init(){
        empresas = new ArrayList<>();
        Query query = ref.child("usuarioEmpresa").orderByChild("id_empresa");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot citi : dataSnapshot.getChildren()){
                        String id_empresa = citi.child("id_empresa").getValue().toString();
                        String idUsuario = citi.child("id_usuario").getValue().toString();
                        Log.i("String ALV", id_empresa+ " " + idUsuario + " ... " + userID);
                        if(idUsuario.equals(userID))
                            empresas.add(id_empresa);
                    }
                }
                Log.i("número de empresas", empresas.size()+"");
                Log.i("INIT","OK_________________-");
                elements = new ArrayList<>();
                for(String sss : empresas){
                    Log.i("empresa no. ", sss+"");
                }

                //Recuperar empresas del usuario
                Log.i("número de empresasx2", empresas.size()+"");

                for(String empresa : empresas){
                    Query AXD = ref.child("negocio").orderByChild("id_empresa");
                    Log.i("Valores empresa","No me quieren!");
                    AXD.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot citi : dataSnapshot.getChildren()) {
                                    String id_empresa = citi.child("id_empresa").getValue(Integer.class)+"";
                                    Log.i("Comparación:",id_empresa+" : " + empresa.replace("[",""));
                                    if(id_empresa.equals(empresa)){
                                        String nombre = citi.child("nombre").getValue(String.class);
                                        String calle = citi.child("calle").getValue(String.class);
                                        String numEXT = citi.child("numEXT").getValue(String.class);
                                        String numINT = citi.child("numINT").getValue(String.class);
                                        String fraccionamiento = citi.child("fraccionamiento").getValue(String.class);
                                        String uri = citi.child("pic").getValue(String.class);
                                        boolean abierto = Boolean.parseBoolean(citi.child("abierto").getValue(String.class));
                                        boolean servicio = Boolean.parseBoolean(citi.child("servicio").getValue(String.class));
                                        Log.i("Valores empresa",id_empresa + " " + nombre);
                                        Query de = ref.child("rateEmpresa");
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
                                                Glide.with(adminSeller.this).asBitmap()
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
                                                                elements.add(new ListElement(logos, nombre, (calle+ " " + numEXT + " ("+numINT+"), "+ fraccionamiento), prom,abierto,servicio,id_empresa));
                                                                Log.i("número de empresas :D", empresas.size()+"");
                                                                ListAdapter listAdapter = new ListAdapter(elements, adminSeller.this);
                                                                RecyclerView recyclerView = findViewById(R.id.recyclerMain);
                                                                recyclerView.setHasFixedSize(true);
                                                                recyclerView.setLayoutManager(new LinearLayoutManager(adminSeller.this));
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
                            Toast.makeText(adminSeller.this,"Fallo la lectura: " + databaseError.getCode(),Toast.LENGTH_LONG);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(adminSeller.this,"Fallo la lectura: " + databaseError.getCode(),Toast.LENGTH_LONG);
            }
        });

    }
}