package com.example.tp0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifyActivity extends AppCompatActivity {

    Button ModifyButton;
    EditText rateToModify;
    Spinner currency_spinner;

    DataBaseHelper dataBaseHelper;
    HashMap<String, String> HashMapCurrency;

    ArrayList<String> currencyName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ModifyButton = findViewById(R.id.rate_modify_button);
        rateToModify = findViewById(R.id.input_rate);
        currency_spinner = findViewById(R.id.currency_spinner);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        HashMapCurrency = dataBaseHelper.getAllCurrency();
        addSpinnerCurrency(HashMapCurrency);

        // On modify button clicked
        ModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = rateToModify.getText().toString();
                String dest_monnaie = currency_spinner.getSelectedItem().toString();
                updateRate(dest_monnaie,value);
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(parentLayout, "Currency "+ dest_monnaie + " Successfully modified !", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    /*
     * Add currency name in the spinner
     * @param currency    the hashmap of all currency
     */
    void addSpinnerCurrency(HashMap<String, String> hash){
        // Add currency name to list
        for(Map.Entry<String, String> entry : hash.entrySet()) {
            currencyName.add(entry.getKey());
        }
        // Spinner set variables with monnaie_array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_dropdown_item, currencyName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency_spinner.setAdapter(adapter);
    }

    /*
     * Update currency name and rate in the database
     * @param currency    the currency name to modify
     * @param rate        the new rate of currency
     */
    void updateRate(String currency, String rate){
        // Retrieve the database and update currency rate
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        dataBaseHelper.addOrUpdateCurrency(currency,rate);
    }

}