package com.example.tp0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    TextView location_text;
    TextView result;
    Button BtnLocation;
    Button BtnConvert;
    EditText dest_address;
    EditText amount_input;
    LocationManager locationManager;

    private String currentCountry;
    private String destCountry;

    HashMap<String, String> hashMapCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        location_text = findViewById(R.id.currentLocation);
        result = findViewById(R.id.resultLocate_Box);
        dest_address = findViewById(R.id.address_Box);
        BtnLocation = findViewById(R.id.locationButton);
        BtnConvert = findViewById(R.id.mapsConvertButton);
        amount_input = findViewById(R.id.amont_Box);

        // Get hashMap from mainWindows
        Intent intent = getIntent();
        hashMapCurrency = (HashMap<String, String>) intent.getSerializableExtra("hashMap");

        // Runtime permission location
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        // On click, get current location and input destination
        // modify view content to print the country
        BtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                getInputCountry();
            }
        });

        // Compute the value of the dest currency
        BtnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float convertValue = calculMonnaie(Float.parseFloat(amount_input.getText().toString()));
                String symbole = getSymbole();
                result.setText(Float.toString(convertValue) + symbole);
            }
        });
    }


    /*
     * Get the current location on click of the input button
     * Trigger the onLocationChanged function
     */
    @SuppressLint("MissingPermission")
    private void getLocation(){
        try{
            // Retrieve current location of the phone
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 5,MapsActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * If the location of the phone change, update (on click off the button location change)
     * @param location     location of the phone
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            // On location update get country from current location
            currentCountry = getCountry(location.getLatitude(),location.getLongitude());
            location_text.setText(currentCountry);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Convert the current monnaie into the chosen currency
     * @param latitude     latitude of the location to find
     * @param longitude    longitude of the location to find
     * @return country     country of the location
     */
    public String getCountry(double latitude, double longitude) {
        try{
            Geocoder gcd = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
            {
                Log.e("Tag Maps", "Country : " + currentCountry);
                return addresses.get(0).getCountryName();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /*
     * Convert the country name of the input address
     * If not found print a snackbar message
     * If input is empty, print a snackbar message
     */
    public void getInputCountry(){
        Geocoder dest = new Geocoder(this);
        List<Address> addresses = null;
        if(!dest_address.getText().toString().isEmpty()){
            String address = dest_address.getText().toString();
            try{
                addresses = dest.getFromLocationName(address,1);
                Address addressDest = addresses.get(0);
                dest_address.setText(addressDest.getCountryName());
                destCountry = dest_address.getText().toString();
                Log.e("Tag input","Input country : " + addressDest.getCountryName() );

            } catch (IOException e) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(parentLayout, "Address compute not found, please retry...", Snackbar.LENGTH_LONG);
                snackbar.show();
                e.printStackTrace();
            }
        }else{
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "Address compute empty, please enter a valid address", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    /*
     * Convert the current monnaie into the chosen currency
     * @param amont     amont of monnai to convert
     * @return amont    converted amont of monnaie
     */
    float calculMonnaie(float amont){
        String trigDest = getTrigramByName(destCountry);
        String trigCurrent = getTrigramByName(currentCountry);
        float rateDest = getCurrencyRateByName(trigDest);
        float rateCurrent = getCurrencyRateByName(trigCurrent);
        return (((amont * 1)/rateCurrent)*rateDest)/1;
    }

    /*
     * Return currency value from the initialised HashMap
     * @param   the currency to convert
     */
    float getCurrencyRateByName(String currency){
        for(Map.Entry<String, String> entry : hashMapCurrency.entrySet()) {
            if(entry.getKey().equals(currency))
                return Float.parseFloat(entry.getValue());
            else
                continue;
        }
        return 0;
    }


    /*
     * Get the symbole to print from the spinner value (just sample not all currency are in this list)
     * @return  the string containing the currency symbole
     */
    public String getTrigramByName(String theCountry){
        String country = theCountry;
        if(country.equals("United States"))
            return "USD";
        if(country.equals("Bulgaria"))
            return "BGN";
        if(country.equals("United Kingdom"))
            return "GBP";
        if(country.equals("Mexico"))
            return " Mex$";
        if(country.equals("Australia"))
            return "AUD";
        if(country.equals("China"))
            return "CNY";
        if(country.equals("Japan"))
            return "JPY";
        if(country.equals("Hong Kong"))
            return "HKD";
        if(country.equals("Philippines"))
            return "PHP";
        if(country.equals("Singapore"))
            return "SGD";
        if(country.equals("Thailand"))
            return "THB";
        if(country.equals("Switzerland"))
            return "CHF";
        if(country.equals("Denmark"))
            return "DKK";
        if(country.equals("Poland"))
            return "PLN";
        if(country.equals("Romania"))
            return "RON";
        if(country.equals("France") || country.equals("Germany") || country.equals("Spain") || country.equals("Portugal"))// and many other...
            return " €";
        else
            return " unknown";
    }


    /*
     * Get the symbole to print from the spinner value (just sample not all currency are in this list)
     * @return  the string containing the currency symbole
     */
    public String getSymbole(){
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