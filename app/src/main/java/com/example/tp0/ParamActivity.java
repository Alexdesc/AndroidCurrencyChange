package com.example.tp0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParamActivity extends AppCompatActivity {

    ListView CurrencyList;
    ArrayList<String> currencyName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        Log.e("Tag 1.1", "onClick: Create Param view...");
        CurrencyList = findViewById(R.id.CurrencyList);
        Intent intent = getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
        Log.e("Tag 1.2", "intent data : Ok !");
        Log.e("Tag 1.4", "Adaptater : set !" + hashMap);
        showCurrency(hashMap);
    }

    /*
     * Show currency from database into listview
     * @param currency    the hashmap of all currency
     */
    public void showCurrency(HashMap<String, String> currency) {
        // Add key and Value name to an array (use by the listview)
        currencyName.add("\tCurrency : \t\t Rate :");
        for(Map.Entry<String, String> entry : currency.entrySet()) {
            currencyName.add("\t" + entry.getKey()+ " \t\t\t\t\t\t\t\t\t\t " + entry.getValue());
        }
        Log.e("Tag 1.4", "Adaptater : Creating...");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currencyName);
        Log.e("Tag 1.4", "Adaptater : set !");
        CurrencyList.setAdapter(itemsAdapter);
        Log.e("Tag 1.4", "Listview : set !");
    }
}