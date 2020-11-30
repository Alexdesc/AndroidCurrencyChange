package com.example.tp0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button ConvertButton;
    FloatingActionButton ParamView;
    EditText valueToConvert;
    TextView ResultBox;
    Spinner Dest_spinner;

    ArrayList<String> currencyName = new ArrayList<String>();
    CurrencyRateHandler CR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConvertButton = findViewById(R.id.Convert_button);
        ParamView = findViewById(R.id.ParamButton);
        Dest_spinner = (Spinner) findViewById(R.id.dest_spinner);
        valueToConvert = findViewById(R.id.Input_Box);
        ResultBox = findViewById(R.id.Result_Box);

        // Select the policy of the app
        // An other param is add in the AndroidManifest.xml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Tag 1", "onStart: passed");

        // Retreve currency rate from internet
        CR = new CurrencyRateHandler();
        CR.doInBackground();

        // Add currency name to an array (use by the spinner
        for(Map.Entry<String, String> entry : CR.currencyRate.entrySet()) {
            currencyName.add(entry.getKey());
        }

        // Spinner set variables with monnaie_array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_dropdown_item, currencyName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Dest_spinner.setAdapter(adapter);


        // On convert button clicked
        ConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag 2", "onClick: passed");
                String value = valueToConvert.getText().toString();
                String dest_monnaie = Dest_spinner.getSelectedItem().toString();
                Log.e("Tag 4", "onClick: monnaie dest : " + dest_monnaie);
                checkAndCompleteString(value);
            }
        });

        // On ParamView button clicked
        ParamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag 4", "onClick: Enter Param view...");
                paramView(v);
            }
        });
    }

    public void paramView(View v) {
        Intent intent = new Intent(this, ParamActivity.class);
        intent.putExtra("hashMap", CR.currencyRate);
        startActivity(intent);
    }


    /*
     * Convert the current monnaie into the chosen currency
     * @param amont     amont of monnai to convert
     * @return amont    converted amont of monnaie
     */
    float calculMonnaie(float amont){
        String Dest_monnaie = Dest_spinner.getSelectedItem().toString();
        float rate = CR.getCurrencyRateByName(Dest_monnaie);
        return amont * rate;

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
     * Get the symbole to print from the spinner value (just sample)
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
        else
            return " unknown";
    }
}