package mx.ita.findmybusiness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class editNegocio extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String userID = mAuth.getCurrentUser().getUid();
    private static final int SELECT_FILE = 1;
    Button uploadPic, submit;
    ImageView picView;
    private GoogleMap mMap;
    private static final int MY_REQUEST_INT = 177;
    MapView mapView;
    private Uri selectedImageUri = null;
    private Uri selectedImage;
    ChipsInput chipsInput;
    private double lat = 0, lon = 0;
    int id_empresa = 0;
    String switchval = "0";
    Toolbar toolbar;
    String url;
    String urlnew;
    ImageView picBox;
    String[] dias = {"Lunes", "Martes","Miercoles","Jueves","Viernes","Sábado","Domingo"};
    StorageReference photoRef;
    EditText txtCalles, txtEXTs, txtFraccionamientos, txtCPs,txtMunicipios;
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
        mapView = findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        };

        chipsInput = findViewById(R.id.chips_input);
        submit = findViewById(R.id.btnSubmitNegocio);
        uploadPic = findViewById(R.id.btnUploadPic);
        picView = findViewById(R.id.picFotEdit);


        ImageButton btnLocate = findViewById(R.id.btnLocate);
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText locationSearch = findViewById(R.id.txtLocation);
                String finalLocation = txtCalles.getText().toString() +" " + txtEXTs.getText().toString() + ", " + txtFraccionamientos.getText().toString() + ", " + txtCPs.getText().toString() +" "  + txtMunicipios.getText().toString();
                locationSearch.setText(finalLocation);
                String location = locationSearch.getText().toString();
                Geocoder geocoder = new Geocoder(editNegocio.this, Locale.getDefault());
                Log.i("Location",location);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(location,1);
                    if(addresses.size() > 0){
                        Marker marker = null;
                        if(marker != null){
                            marker.remove();
                            marker = null;
                        }else{
                            Address address = addresses.get(0);
                            Log.i("Coordenadas", (address.getLatitude() + " " + address.getLongitude()));
                            lat = address.getLatitude();
                            lon = address.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lat,lon), 15));
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_location_size)));
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregar();
            }
        });
        Query query = ref.child("negocio").orderByChild("id_empresa");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot citi : dataSnapshot.getChildren()){
                        String idtemp = citi.child("id_empresa").getValue().toString();
                        if(idtemp.equals(id_empresa)){
                            Switch sw = (Switch) findViewById(R.id.switch1);
                            EditText txtNombre = findViewById(R.id.txtNombreEdit);
                            EditText txtCalles = (EditText) findViewById(R.id.txtCalle);
                            EditText txtEXTs = (EditText) findViewById(R.id.txtEXT);
                            EditText txtINTs = (EditText) findViewById(R.id.txtINT);
                            EditText txtFraccionamientos = (EditText) findViewById(R.id.txtFraccionamiento);
                            EditText txtMunicipios = (EditText) findViewById(R.id.txtMunicipio);
                            EditText txtCPs = (EditText) findViewById(R.id.txtCP);
                            EditText txtCorreos = (EditText) findViewById(R.id.txtCorreo);
                            EditText txtTelefonos = (EditText) findViewById(R.id.txtTeléfono);
                            CheckBox txtLunes = (CheckBox) findViewById(R.id.lunes);
                            EditText txtLunes1 = (EditText) findViewById(R.id.lunes1);
                            EditText txtLunes2 = (EditText) findViewById(R.id.lunes2);
                            CheckBox txtMartes = (CheckBox) findViewById(R.id.martes);
                            EditText txtMartes1 = (EditText) findViewById(R.id.martes1);
                            EditText txtMartes2 = (EditText) findViewById(R.id.martes2);
                            CheckBox txtMiercoles = (CheckBox) findViewById(R.id.miercoles);
                            EditText txtMiercoles1 = (EditText) findViewById(R.id.miercoles1);
                            EditText txtMiercoles2 = (EditText) findViewById(R.id.miercoles2);
                            CheckBox txtJueves = (CheckBox) findViewById(R.id.jueves);
                            EditText txtJueves1 = (EditText) findViewById(R.id.jueves1);
                            EditText txtJueves2 = (EditText) findViewById(R.id.jueves2);
                            CheckBox txtViernes = (CheckBox) findViewById(R.id.viernes);
                            EditText txtViernes1 = (EditText) findViewById(R.id.viernes1);
                            EditText txtViernes2 = (EditText) findViewById(R.id.viernes2);
                            CheckBox txtSabado = (CheckBox) findViewById(R.id.sabado);
                            EditText txtSabado1 = (EditText) findViewById(R.id.sabado1);
                            EditText txtSabado2 = (EditText) findViewById(R.id.sabado2);
                            CheckBox txtDomingo = (CheckBox) findViewById(R.id.domingo);
                            EditText txtDomingo1 = (EditText) findViewById(R.id.domingo1);
                            EditText txtDomingo2 = (EditText) findViewById(R.id.domingo2);
                            EditText acerca = (EditText) findViewById(R.id.txtAcerca);

                            picBox = (ImageView) findViewById(R.id.picFotEdit);
                            sw.setChecked(Boolean.parseBoolean(citi.child("ambulante").getValue().toString()));
                             txtNombre.setText(citi.child("nombre").getValue().toString());
                             txtCalles.setText(citi.child("calle").getValue().toString());
                            txtEXTs.setText(citi.child("EXT").getValue().toString());
                             txtINTs.setText(citi.child("INT").getValue().toString());
                             txtFraccionamientos.setText(citi.child("fraccionamiento").getValue().toString());
                             txtMunicipios.setText(citi.child("municipio").getValue().toString());
                             txtCPs.setText(citi.child("CP").getValue().toString());
                             txtCorreos.setText(citi.child("correo").getValue().toString());
                             txtTelefonos.setText(citi.child("telefono").getValue().toString());
                             Query segundogrado = ref.child("horario").child(id_empresa+"").orderByChild("dia");
                            segundogrado.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot citis : dataSnapshot.getChildren()){
                                            if(citis.child("dia").getValue().toString().equals("Lunes")){
                                                txtLunes.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtLunes1.setText(citi.child("de").getValue().toString());
                                                txtLunes2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Martes")){
                                                txtMartes.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtMartes1.setText(citi.child("de").getValue().toString());
                                                txtMartes2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Miercoles")){
                                                txtMiercoles.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtMiercoles1.setText(citi.child("de").getValue().toString());
                                                txtMiercoles2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Jueves")){
                                                txtJueves.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtJueves1.setText(citi.child("de").getValue().toString());
                                                txtJueves2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Viernes")){
                                                txtViernes.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtViernes1.setText(citi.child("de").getValue().toString());
                                                txtViernes2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Sábado")){
                                                txtSabado.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtSabado1.setText(citi.child("de").getValue().toString());
                                                txtSabado2.setText(citi.child("a").getValue().toString());
                                            }
                                            if(citis.child("dia").getValue().toString().equals("Domingo")){
                                                txtDomingo.setChecked(Boolean.parseBoolean(citis.child("abre").getValue().toString()));
                                                txtDomingo1.setText(citi.child("de").getValue().toString());
                                                txtDomingo2.setText(citi.child("a").getValue().toString());
                                            }

                                        }

                                    }

                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                             txtLunes.setText(findViewById(R.id.lunes));
                             txtLunes1.setText(findViewById(R.id.lunes1));
                             txtLunes2.setText(findViewById(R.id.lunes2));
                             txtMartes.setText(findViewById(R.id.martes));
                             txtMartes1.setText(findViewById(R.id.martes1));
                             txtMartes2.setText(findViewById(R.id.martes2));
                             txtMiercoles.setText(findViewById(R.id.miercoles));
                             txtMiercoles1.setText(findViewById(R.id.miercoles1));
                             txtMiercoles2.setText(findViewById(R.id.miercoles2));
                             txtJueves.setText(findViewById(R.id.jueves));
                             txtJueves1.setText(findViewById(R.id.jueves1));
                             txtJueves2.setText(findViewById(R.id.jueves2));
                             txtViernes.setText(findViewById(R.id.viernes));
                             txtViernes1.setText(findViewById(R.id.viernes1));
                             txtViernes2.setText(findViewById(R.id.viernes2));
                             txtSabado.setText(findViewById(R.id.sabado));
                             txtSabado1.setText(findViewById(R.id.sabado1));
                             txtSabado2.setText(findViewById(R.id.sabado2));
                             txtDomingo.setText(findViewById(R.id.domingo));
                             txtDomingo1.setText(findViewById(R.id.domingo1));
                             txtDomingo2.setText(findViewById(R.id.domingo2));
                             acerca.setText(findViewById(R.id.txtAcerca));
                             url = citi.child("pic").getValue().toString();
                            Glide.with(editNegocio.this).load(url)
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

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void agregar(){
        Switch sw = (Switch) findViewById(R.id.switch1);
        EditText txtNombres = findViewById(R.id.txtNombreEdit);
        EditText txtCalles = (EditText) findViewById(R.id.txtCalle);
        EditText txtEXTs = (EditText) findViewById(R.id.txtEXT);
        EditText txtINTs = (EditText) findViewById(R.id.txtINT);
        EditText txtFraccionamientos = (EditText) findViewById(R.id.txtFraccionamiento);
        EditText txtMunicipios = (EditText) findViewById(R.id.txtMunicipio);
        EditText txtCPs = (EditText) findViewById(R.id.txtCP);
        EditText txtCorreos = (EditText) findViewById(R.id.txtCorreo);
        EditText txtTelefonos = (EditText) findViewById(R.id.txtTeléfono);
        CheckBox txtLunes = (CheckBox) findViewById(R.id.lunes);
        EditText txtLunes1 = (EditText) findViewById(R.id.lunes1);
        EditText txtLunes2 = (EditText) findViewById(R.id.lunes2);
        CheckBox txtMartes = (CheckBox) findViewById(R.id.martes);
        EditText txtMartes1 = (EditText) findViewById(R.id.martes1);
        EditText txtMartes2 = (EditText) findViewById(R.id.martes2);
        CheckBox txtMiercoles = (CheckBox) findViewById(R.id.miercoles);
        EditText txtMiercoles1 = (EditText) findViewById(R.id.miercoles1);
        EditText txtMiercoles2 = (EditText) findViewById(R.id.miercoles2);
        CheckBox txtJueves = (CheckBox) findViewById(R.id.jueves);
        EditText txtJueves1 = (EditText) findViewById(R.id.jueves1);
        EditText txtJueves2 = (EditText) findViewById(R.id.jueves2);
        CheckBox txtViernes = (CheckBox) findViewById(R.id.viernes);
        EditText txtViernes1 = (EditText) findViewById(R.id.viernes1);
        EditText txtViernes2 = (EditText) findViewById(R.id.viernes2);
        CheckBox txtSabado = (CheckBox) findViewById(R.id.sabado);
        EditText txtSabado1 = (EditText) findViewById(R.id.sabado1);
        EditText txtSabado2 = (EditText) findViewById(R.id.sabado2);
        CheckBox txtDomingo = (CheckBox) findViewById(R.id.domingo);
        EditText txtDomingo1 = (EditText) findViewById(R.id.domingo1);
        EditText txtDomingo2 = (EditText) findViewById(R.id.domingo2);
        EditText acerca = (EditText) findViewById(R.id.txtAcerca);

        String nombre = txtNombres.getText().toString();
        String txtCalle = txtCalles.getText().toString();
        String txtEXT = txtEXTs.getText().toString();
        String txtINT = txtINTs.getText().toString();
        String txtFraccionamiento = txtFraccionamientos.getText().toString();
        String txtMunicipio = txtMunicipios.getText().toString();
        String txtCP = txtCPs.getText().toString();
        String txtTelefono = txtTelefonos.getText().toString();
        String txtCorreo = txtCorreos.getText().toString();
        boolean lunes = txtLunes.isChecked();
        String lunes1 = txtLunes1.getText().toString();
        String lunes2 = txtLunes2.getText().toString();
        boolean martes = txtLunes.isChecked();
        String martes1 = txtMartes1.getText().toString();
        String martes2 = txtMartes2.getText().toString();
        boolean miercoles = txtMiercoles.isChecked();
        String miercoles1 = txtMiercoles1.getText().toString();
        String miercoles2 = txtMiercoles2.getText().toString();
        boolean jueves = txtJueves.isChecked();
        String jueves1 = txtJueves1.getText().toString();
        String jueves2 = txtJueves2.getText().toString();
        boolean viernes = txtViernes.isChecked();
        String viernes1 = txtViernes1.getText().toString();
        String viernes2 = txtViernes2.getText().toString();
        boolean sabado = txtSabado.isChecked();
        String sabado1 = txtSabado1.getText().toString();
        String sabado2 = txtSabado2.getText().toString();
        boolean domingo = txtDomingo.isChecked();
        String domingo1 = txtDomingo1.getText().toString();
        String domingo2 = txtDomingo2.getText().toString();
        String acerc = acerca.getText().toString();
        List<String> chips = new ArrayList<>();
        chipsInput = findViewById(R.id.chips_input);
        List<ChipInterface> contactsSelected = (List<ChipInterface>) chipsInput.getSelectedChipList();
        for(ChipInterface list : contactsSelected){
            Log.i("Chips habanero 8=D ♥", " " + list.getLabel());
            Log.i("Box ☻ 8====D", " "+domingo);
        }


        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("negocio").child(id_empresa+"");
        final StorageReference file_name = Folder.child("file"+selectedImage.getLastPathSegment());
        file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
            Map<String, Object> map = new HashMap<>();
            String IDnuevo = mAuth.getCurrentUser().getUid();
            map.put("id_empresa", id_empresa);
            map.put("nombre", nombre);
            map.put("calle", txtCalle);
            map.put("numEXT", txtEXT);
            map.put("numINT", txtINT);
            map.put("fraccionamiento", txtFraccionamiento);
            map.put("cp", txtCP);
            map.put("municipio", txtMunicipio);
            map.put("correo", txtCorreo);
            map.put("telefono", txtTelefono);
            map.put("latitud", lat);
            map.put("longitud",lon);
            map.put("acerca",acerc);
            map.put("pic", String.valueOf(uri));
            map.put("ambulante", switchval);
            mDatabase.child("negocio").child(id_empresa+"").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                        Intent intent = new Intent(editNegocio.this, adminSeller.class);
                        startActivity(intent);
                    }else{
                        Log.w("Error XD 1",task2.getException());

                    }
                }
            });

            String[] dias = {"Lunes", "Martes","Miercoles","Jueves","Viernes","Sábado","Domingo"};
            boolean[] abre = {lunes,martes,miercoles,jueves,viernes,sabado,domingo};
            String[][] hora = {{lunes1,lunes2},
                    {martes1,martes2},
                    {miercoles1,miercoles2},
                    {jueves1,jueves2},
                    {viernes1,viernes2},
                    {sabado1,sabado2},
                    {domingo1,domingo2}};

            for(byte x = 0; x < 7;x++){
                map.clear();
                map.put("dia",dias[x]);
                map.put("abre",abre[x]);
                map.put("de",hora[x][0]);
                map.put("a",hora[x][1]);
                mDatabase.child("horario").child((id_empresa-1)+"").child(dias[x]).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task2) {
                        if(task2.isSuccessful()){
                            Log.i("OK","OK!!!!");
                        }else{
                            Log.w("Error XD 1",task2.getException());

                        }
                    }
                });
            }
        }));
    }
    public void getLastLocation(){
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                if (mMap != null) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } catch (SecurityException e) { e.printStackTrace(); }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(editNegocio.this);
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }
            return;
        } else {
            mMap.setMyLocationEnabled(true);
            getLastLocation();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}