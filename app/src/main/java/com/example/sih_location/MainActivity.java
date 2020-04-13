package com.example.sih_location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // for hiding title

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this );
        fetchLastLocation();

    }



    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    currentLocation = location;
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> listAddress = null;
                    try {
                        listAddress = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                        if(listAddress!=null && listAddress.size()>0) {
                            Log.i("R-Address",listAddress.get(0).getAddressLine(0).toString());
                            Log.i("R-State",listAddress.get(0).getAdminArea().toString());
                            Log.i("R-Country Name",listAddress.get(0).getCountryName().toString());
                            Log.i("R-City",listAddress.get(0).getLocality().toString());
                            Log.i("R-Postal Code",listAddress.get(0).getPostalCode().toString());
                            Log.i("R-District",listAddress.get(0).getSubAdminArea().toString());
                            Log.i("R-Locality",listAddress.get(0).getSubLocality().toString());
                            Log.i("R-Latitude", String.valueOf(currentLocation.getLatitude()));
                            Log.i("R-Longitude", String.valueOf(currentLocation.getLongitude()));
                            Log.i("R-Altitude", String.valueOf(currentLocation.getAltitude()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String address = listAddress.get(0).getAddressLine(0).toString();
                    Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng LatLng =new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(LatLng)
                .title("I am Here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 5));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void toNextActivity(View view){
        Intent intent = new Intent(getApplicationContext(), NextActivity.class);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> listAddress = null;
        try {
            listAddress = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = listAddress.get(0).getAddressLine(0).toString();
        String state =  listAddress.get(0).getAdminArea().toString();
        String countryName = listAddress.get(0).getCountryName().toString();
        String city = listAddress.get(0).getLocality().toString();
        String postalCode = listAddress.get(0).getPostalCode().toString();
        String district = listAddress.get(0).getSubAdminArea().toString();
        String locality = listAddress.get(0).getSubLocality().toString();
        String latitude = String.valueOf(currentLocation.getLatitude());
        String longitude = String.valueOf(currentLocation.getLongitude());

        intent.putExtra("Address",address);
        intent.putExtra("Country_Name",countryName);
        intent.putExtra("State",state);
        intent.putExtra("City",city);
        intent.putExtra("Postal_Code",postalCode);
        intent.putExtra("District",district);
        intent.putExtra("Locality",locality);
        intent.putExtra("Latitude",latitude);
        intent.putExtra("Longitude",longitude);

        startActivity(intent);
    }
}
