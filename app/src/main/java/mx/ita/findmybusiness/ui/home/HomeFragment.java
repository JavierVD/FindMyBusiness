package mx.ita.findmybusiness.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import mx.ita.findmybusiness.ContactChip;
import mx.ita.findmybusiness.MainMenu;
import mx.ita.findmybusiness.R;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    View view;
    GoogleMap map;
    MapView mapView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    private static final int MY_REQUEST_INT = 177;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //MapsInitializer.initialize(getContext());
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }
            return;
        } else {
            map.setMyLocationEnabled(true);
            getLastLocation();
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            Query query = ref.child("negocio");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        byte con = 0;
                        for (DataSnapshot citi : dataSnapshot.getChildren()) {
                            String nombre = citi.child("nombre").getValue().toString();
                            String calle = citi.child("calle").getValue().toString();
                            String numEXT = citi.child("numEXT").getValue().toString();
                            String numINT = citi.child("numINT").getValue().toString();
                            String fraccionamiento = citi.child("fraccionamiento").getValue().toString();
                            String cp = citi.child("cp").getValue().toString();
                            double lat = Double.parseDouble(citi.child("latitud").getValue().toString());
                            double lon = Double.parseDouble((citi.child("longitud").getValue().toString()));
                            map.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(nombre).snippet("Pollas con cebollas"));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
                                if (map != null) {
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
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
}