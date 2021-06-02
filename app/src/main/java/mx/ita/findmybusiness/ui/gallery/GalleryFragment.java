package mx.ita.findmybusiness.ui.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pchmn.materialchips.ChipsInput;

import java.util.ArrayList;
import java.util.List;

import mx.ita.findmybusiness.ContactChip;
import mx.ita.findmybusiness.R;

public class GalleryFragment extends Fragment {
    //private static GalleryFragment INSTANCE = null;

    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ChipsInput chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
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
                        contactList.add(new ContactChip(dato+"",dato+""));
                        Log.i("Valor obtenido, ciclo " + (++con), dato+" :=)");
                    }
                    con = 0;
                    for(ContactChip list: contactList){
                        Log.i("Chip: ",  chipsInput.getHint());
                    }
                    contactList.add(new ContactChip("XD","Equiste"));
                    chipsInput.setFilterableList(contactList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
