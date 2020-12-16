package com.example.tp0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    TextView location_text;
    Button getLocation;
    LocationManager locationManager;

    private String currentCountry;
    private String destCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        location_text = findViewById(R.id.currentLocation);
        getLocation = findViewById(R.id.LocationButton);
        // Runtime permission
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 5,MapsActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            location_text.setText(address);
            getCountry(location);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCountry(Location location){
        try{
            Geocoder gcd = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)
            {
                currentCountry = addresses.get(0).getCountryName();
                Log.e("Tag Maps", "Country : " + currentCountry);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
     * Get the symbole to print from the spinner value (just sample not all currency are in this list)
     * @return  the string containing the currency symbole
     */
    String getSymbole(){
        String monnaie = destCountry;
        if(monnaie.equals("USD"))
            return " $";
        if(monnaie.equals("BGN"))
            return " лв";
        if(monnaie.equals("GBP"))
            return " £";
        if(monnaie.equals("MXN"))
            return " Mex$";
        if(monnaie.equals("AUD"))
            return " $";
        if(monnaie.equals("CNY"))
            return " ¥ /元";
        if(monnaie.equals("JPY"))
            return " ¥";
        if(monnaie.equals("HKD"))
            return " HK$";
        if(monnaie.equals("PHP"))
            return " ₱";
        if(monnaie.equals("SGD"))
            return " $";
        if(monnaie.equals("THB"))
            return " ฿";
        if(monnaie.equals("CHF"))
            return " CHF";
        if(monnaie.equals("DKK"))
            return " kr";
        if(monnaie.equals("PLN"))
            return " zł";
        if(monnaie.equals("RON"))
            return " lei";
        if(monnaie.equals("EUR"))
            return " €";
        else
            return " unknown";
    }
}