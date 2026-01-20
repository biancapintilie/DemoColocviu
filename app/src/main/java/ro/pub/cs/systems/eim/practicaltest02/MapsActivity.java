package ro.pub.cs.systems.eim.practicaltest02; // 1. PACHETUL (Critic!)

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

// 2. IMPORTURILE PENTRU HARTA (Astea lipseau)
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aici setam layout-ul. Daca nu ai creat fisierul XML, linia asta va da eroare la R.layout
        // Dar pentru examen poti lasa un layout simplu sau sa il creezi.
        setContentView(R.layout.activity_maps);

        // Initializare Harta
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // Asigura-te ca in XML ai un fragment cu id "map"
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 1. Definești locația cerută (Latitudine, Longitudine)
        // De exemplu: Ghelemegioaia (44.53 lat, 22.85 long)
        LatLng locatieCeruta = new LatLng(44.614722, 22.834722);

        // 2. Adaugi un marker (Opțional, dar dă bine)
        googleMap.addMarker(new MarkerOptions()
                .position(locatieCeruta)
                .title("Marker in Ghelemegioaia"));

        // 3. MUȚI CAMERA (Asta e cerința principală: "Centrată pe...")
        // "15" este nivelul de Zoom (cu cât mai mare, cu atât mai aproape de stradă)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locatieCeruta, 15));
    }
}