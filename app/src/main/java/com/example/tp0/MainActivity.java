package com.example.tp0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button ConvertButton;
    FloatingActionButton ParamView;
    FloatingActionButton ModifyView;
    FloatingActionButton MapsView;
    EditText valueToConvert;
    TextView ResultBox;
    Spinner Dest_spinner;
    Spinner Current_spinner;

    ArrayList<String> currencyName = new ArrayList<String>();
    HashMap<String, String> HashMapCurrency;
    CurrencyRateHandler CR;

    FirebaseDatabase FirebaseDatabase;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link all widgets on the homepage to variables
        ConvertButton = findViewById(R.id.Convert_button);
        ParamView = findViewById(R.id.ParamButton);
        ModifyView = findViewById(R.id.ModifyButton);
        MapsView = findViewById((R.id.mapsButton));
        Dest_spinner = (Spinner) findViewById(R.id.dest_spinner);
        Current_spinner = (Spinner) findViewById(R.id.current_spinner);
        valueToConvert = findViewById(R.id.Input_Box);
        ResultBox = findViewById(R.id.Result_Box);

        // Select the policy of the app
        // An other param is add in the AndroidManifest.xml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Runtime permission Internet
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET
            },100);
        }
        // Runtime permission Wifi states
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_WIFI_STATE
            },100);
        }
        // Runtime permission network states
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_NETWORK_STATE
            },100);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Tag main 1", "onStart: passed");

        final DataBaseHelper databaseHelper = DataBaseHelper.getInstance(this);
        HashMapCurrency = databaseHelper.getAllCurrency();

        // If internet not connected, show a Snackbar message
        if (!isInternetAvailable()){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "Internet unavailable, please check connexion...", Snackbar.LENGTH_LONG);
            snackbar.show();
            // Not connected to internet, can't check current location, hide the button
            MapsView.hide();

        }else {
            // Retreve currency rate from internet
            CR = new CurrencyRateHandler();
            CR.doInBackground();

            // Update firebase data
            // If there is new data, update automatically
            for(Map.Entry<String, String> entry : CR.currencyRate.entrySet()) {
                writeInToFirebase(entry.getKey(),entry.getValue());
                databaseHelper.addOrUpdateCurrency(entry.getKey(),entry.getValue());
            }
            // Internet connected, no need to manually modify currency rate
            ModifyView.hide();
        }

        // Add currency name to an array (use by the spinner) (From database)
        for(Map.Entry<String, String> entry : HashMapCurrency.entrySet()) {
            currencyName.add(entry.getKey());
        }

        // Spinner set variables with monnaie_array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_dropdown_item, currencyName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Dest_spinner.setAdapter(adapter);
        Current_spinner.setAdapter(adapter);

        // On convert button clicked
        ConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag main 2", "onClick: passed");
                String value = valueToConvert.getText().toString();
                String dest_monnaie = Dest_spinner.getSelectedItem().toString();
                Log.e("Tag main 3", "onClick: monnaie dest : " + dest_monnaie);
                checkAndCompleteString(value);
            }
        });

        // On ParamView button clicked
        ParamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag Param", "onClick: Enter Param view...");
                paramView(v);
            }
        });

        // On ModifyView button clicked
        ModifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag Modify", "onClick: Enter Modify view...");
                modifyView(v);
            }
        });

        // On ModifyView button clicked
        MapsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag Modify", "onClick: Enter Modify view...");
                mapsView(v);
            }
        });

        // If data from database are changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue(String.class);
                Log.e("Tag Firebase", "Reading update on " +  key + " new rate : " + value);
                // Update in phone database
                databaseHelper.addOrUpdateCurrency(dataSnapshot.getKey(),dataSnapshot.getValue(String.class));
                // Refresh the used hashMap
                HashMapCurrency = databaseHelper.getAllCurrency();
                Log.e("Tag Database", "Database updated !");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Tag Firebase", "Reading from database failed !");
            }
        });

    }

    /*
     * Open the param window with currency list (listview)
     * @param View     Actual view
     */
    public void paramView(View v) {
        Intent intent = new Intent(this, ParamActivity.class);
        intent.putExtra("hashMap", HashMapCurrency);
        startActivity(intent);
    }

    /*
     * Open the modify window, you can modify rate is offline
     * @param View     Actual view
     */
    public void modifyView(View v) {
        Intent intent = new Intent(this, ModifyActivity.class);
        intent.putExtra("hashMap", HashMapCurrency);
        startActivity(intent);
    }

    /*
     * Open the maps window
     * @param View     Actual view
     */
    public void mapsView(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("hashMap", HashMapCurrency);
        startActivity(intent);
    }

    /*
     * Check connection stat on start
     * @return      true if internet enabled false otherwise
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Write into firebase reelTime Database
     * @param      key (name of currency)
     * @param      value (rate of currency)
     */
    public void writeInToFirebase(String key, String value){
        // Write into firebase dataBase
        FirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getReference("Currency/"+ key);
        myRef.setValue(value);
        Log.e("Tag Firebase", "writing into database..");
    }

    /*
     * Convert the current monnaie into the chosen currency
     * @param amont     amont of monnai to convert
     * @return amont    converted amont of monnaie
     */
    float calculMonnaie(float amont){
        // Finds currency name from spinner
        String Dest_monnaie = Dest_spinner.getSelectedItem().toString();
        String Current_monnaie = Current_spinner.getSelectedItem().toString();
        // If internet available, use currency from internet
        // Else use currency from database
        if(!isInternetAvailable()){
            float rateDest = CR.getCurrencyRateByMap(HashMapCurrency,Dest_monnaie);
            float rateCurrent = CR.getCurrencyRateByMap(HashMapCurrency,Current_monnaie);
            return (((amont * 1)/rateCurrent)*rateDest)/1;
        }else{
            float rateDest = CR.getCurrencyRateByName(Dest_monnaie);
            float rateCurrent = CR.getCurrencyRateByName(Current_monnaie);
            return (((amont * 1)/rateCurrent)*rateDest)/1;
        }
    }

    /*
     * Check if the input value is correct and formated
     * @param Tx    input rate of currency (only for TP0-3)
     * @param value input of the amont of monnaie to convert
     */
    void checkAndCompleteString(String value){
        // If the user put a , instead of .
        // Replace it
        if(value.contains(",")){
            value = value.replace(",",".");
        }

        float convertValue = calculMonnaie(Float.parseFloat(value));
        String symbole = getSymbole();
        ResultBox.setText(Float.toString(convertValue) + symbole);
    }

    /*
     * Get the symbole to print from the spinner value (just sample not all currency are in this list)
     * @return  the string containing the currency symbole
     */
    String getSymbole(){
        String monnaie = Dest_spinner.getSelectedItem().toString();
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