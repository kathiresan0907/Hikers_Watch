package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    updatelocation(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation!=null){
                try {
                    updatelocation(lastKnownLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permsissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permsissions,grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    private void startListening() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }


    private void updatelocation(Location location) throws IOException {

        TextView latTextView=findViewById(R.id.text2);
        TextView lonTextView=findViewById(R.id.text3);
        TextView aacTextView=findViewById(R.id.text4);
        TextView altTextView=findViewById(R.id.text5);
        TextView addTextView=findViewById(R.id.text6);

        latTextView.setText("Latitude :"+ Double.toString(location.getLatitude()));
        lonTextView.setText("Longitude : "+Double.toString(location.getLongitude()));
        aacTextView.setText("Accuracy : "+Double.toString(location.getAccuracy()));
        altTextView.setText("Altitude : "+Double.toString(location.getAltitude()));

        String address="Could not find :(";
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());

        try{
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(listAddress!= null && listAddress.size()>0){
                address="Address : \n";
            }
            if (listAddress.get(0).getThoroughfare()!=null)
                address += listAddress.get(0).getThoroughfare() + "\n";
            if (listAddress.get(0).getLocality()!=null)
                address += listAddress.get(0).getLocality() + "\n";
            if (listAddress.get(0).getPostalCode()!=null)
                address += listAddress.get(0).getPostalCode() + "\n";
            if (listAddress.get(0).getAdminArea()!=null)
                address += listAddress.get(0).getAdminArea() + "\n";
        } catch (IOException e) {
            e.printStackTrace();
        }
        addTextView.setText(address);

    }
}