package mx.ita.findmybusiness;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;


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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class tab_bienes extends Fragment implements OnMapReadyCallback{
    Location mLastLocation;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String userID = mAuth.getCurrentUser().getUid();
    private static final int SELECT_FILE = 1;
    Button uploadPic;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public void getLastLocation(){
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_bienes, container, false);
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        };
        Switch sw = (Switch) view.findViewById(R.id.switch1);
        EditText txtCalles = (EditText) view.findViewById(R.id.txtCalle);
        EditText txtEXTs = (EditText) view.findViewById(R.id.txtEXT);
        EditText txtINTs = (EditText) view.findViewById(R.id.txtINT);
        EditText txtFraccionamientos = (EditText) view.findViewById(R.id.txtFraccionamiento);
        EditText txtMunicipios = (EditText) view.findViewById(R.id.txtMunicipio);
        EditText txtCPs = (EditText) view.findViewById(R.id.txtCP);
        EditText txtCorreos = (EditText) view.findViewById(R.id.txtCorreo);
        EditText txtTelefonos = (EditText) view.findViewById(R.id.txtTeléfono);
        CheckBox txtLunes = (CheckBox) view.findViewById(R.id.lunes);
        EditText txtLunes1 = (EditText) view.findViewById(R.id.lunes1);
        EditText txtLunes2 = (EditText) view.findViewById(R.id.lunes2);
        CheckBox txtMartes = (CheckBox) view.findViewById(R.id.martes);
        EditText txtMartes1 = (EditText) view.findViewById(R.id.martes1);
        EditText txtMartes2 = (EditText) view.findViewById(R.id.martes2);
        CheckBox txtMiercoles = (CheckBox) view.findViewById(R.id.miercoles);
        EditText txtMiercoles1 = (EditText) view.findViewById(R.id.miercoles1);
        EditText txtMiercoles2 = (EditText) view.findViewById(R.id.miercoles2);
        CheckBox txtJueves = (CheckBox) view.findViewById(R.id.jueves);
        EditText txtJueves1 = (EditText) view.findViewById(R.id.jueves1);
        EditText txtJueves2 = (EditText) view.findViewById(R.id.jueves2);
        CheckBox txtViernes = (CheckBox) view.findViewById(R.id.viernes);
        EditText txtViernes1 = (EditText) view.findViewById(R.id.viernes1);
        EditText txtViernes2 = (EditText) view.findViewById(R.id.viernes2);
        CheckBox txtSabado = (CheckBox) view.findViewById(R.id.sabado);
        EditText txtSabado1 = (EditText) view.findViewById(R.id.sabado1);
        EditText txtSabado2 = (EditText) view.findViewById(R.id.sabado2);
        CheckBox txtDomingo = (CheckBox) view.findViewById(R.id.domingo);
        EditText txtDomingo1 = (EditText) view.findViewById(R.id.domingo1);
        EditText txtDomingo2 = (EditText) view.findViewById(R.id.domingo2);
        EditText acerca = (EditText) view.findViewById(R.id.txtAcerca);
        chipsInput = view.findViewById(R.id.chips_input);
        Button submit = view.findViewById(R.id.btnSubmitNegocio);
        uploadPic = view.findViewById(R.id.btnUploadPic);
        picView = view.findViewById(R.id.picFotEdit);
        ImageButton btnLocate = view.findViewById(R.id.btnLocate);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchval = "1";
                    txtCalles.setVisibility(view.INVISIBLE);
                }else{
                    switchval = "0";
                    txtCalles.setVisibility(view.VISIBLE);
                }
            }
        });
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText locationSearch = view.findViewById(R.id.txtLocation);
                String finalLocation = txtCalles.getText().toString() +" " + txtEXTs.getText().toString() + ", " + txtFraccionamientos.getText().toString() + ", " + txtCPs.getText().toString() +" "  + txtMunicipios.getText().toString();
                locationSearch.setText(finalLocation);
                String location = locationSearch.getText().toString();
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                chipsInput = view.findViewById(R.id.chips_input);
                List<ChipInterface> contactsSelected = (List<ChipInterface>) chipsInput.getSelectedChipList();
                for(ChipInterface list : contactsSelected){
                    Log.i("Chips habanero 8=D ♥", " " + list.getLabel());
                    Log.i("Box ☻ 8====D", " "+domingo);
                }

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                Query query = myRef.child("empresa").orderByChild("id_empresa");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot citi : dataSnapshot.getChildren())
                                id_empresa = Integer.parseInt(citi.child("id_empresa").getValue().toString());
                            Log.i("LA ID: ",id_empresa+"");;
                            id_empresa ++;
                        }else{
                            Toast.makeText(getContext(),"Valor :" + id_empresa, Toast.LENGTH_LONG);
                            id_empresa = 1;
                        }
                        Log.i("ID EMPRESA",id_empresa+"");
                        Log.i("empresa fuera:",id_empresa+"");

                        Map<String, Object> soloE = new HashMap<>();
                        soloE.put("id_empresa",id_empresa);

                        mDatabase.child("empresa").child(id_empresa+"").setValue(soloE).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                    Log.i("Nueva empresa", id_empresa+"");
                                }else{
                                    Log.w("Error XD 1",task2.getException());
                                    Toast.makeText(getContext(), "Cagaste", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        soloE.clear();
                        soloE.put("id_empresa",id_empresa);
                        soloE.put("id_usuario",userID);
                        mDatabase.child("usuarioEmpresa").child(id_empresa+"").setValue(soloE).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                    Log.i("Nueva empresa", "Nueva empresa");
                                }else{
                                    Log.w("Error XD 1",task2.getException());
                                    Toast.makeText(getContext(), "Cagaste", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("negocio").child(id_empresa+"");
                        final StorageReference file_name = Folder.child("file"+selectedImage.getLastPathSegment());
                        file_name.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                            Map<String, Object> map = new HashMap<>();
                            String IDnuevo = mAuth.getCurrentUser().getUid();
                            map.put("id_empresa", id_empresa);
                            map.put("nombre", ((registrarNegocio) getActivity()).getNombre());
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
                                        Intent intent = new Intent(getActivity(), adminSeller.class);
                                        startActivity(intent);
                                    }else{
                                        Log.w("Error XD 1",task2.getException());
                                        Toast.makeText(getContext(), "Cagaste", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getContext(), "Cagaste", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria(view);
            }
        });

        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        // build the ContactChip list
        List<ContactChip> contactList = new ArrayList<ContactChip>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child("girobien").orderByChild("nombre");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    byte con = 0;
                    for (DataSnapshot citi : dataSnapshot.getChildren()) {
                        String dato = citi.child("nombre").getValue().toString();
                        contactList.add(new ContactChip(dato + "", dato + ""));
                        Log.i("Valor obtenido, ciclo " + (++con), dato + " :=)");
                    }
                    con = 0;
                    for (ContactChip list : contactList) {
                        Log.i("Chip: ", list.getLabel());
                    }
                }
                chipsInput.setFilterableList(contactList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                // chip removed
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });


        return view;
    }


    public void abrirGaleria(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                SELECT_FILE);
    }
   public void onActivityResult(int requestCode, int resultCode,
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
                                Uri returnUri = imageReturnedIntent.getData();
                                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                                picView.setImageBitmap(bitmapImage);
                                imageStream = getActivity().getApplicationContext().getContentResolver().openInputStream(
                                        selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
    }

}