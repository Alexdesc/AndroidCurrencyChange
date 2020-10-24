package com.example.tp0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // One euros equals to...
    float euroToDollar;
    float euroToYen;
    float euroToPeso;
    float dollarToPeso = (float) 21.39;
    float dollarToYen = (float) 105.25;
    float yenToPeso = (float) 0.2;

    String EURO = "Euro";
    String DOLLAR = "Dollar";
    String PESO = "Peso";
    String YEN = "Yen";

    Button ConvertButton;
    EditText valueToConvert;
    EditText valueTx;
    TextView ResultBox;
    Spinner Current_spinner;
    Spinner Dest_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConvertButton = findViewById(R.id.Convert_button);
        Current_spinner = (Spinner) findViewById(R.id.current_spinner);
        Dest_spinner = (Spinner) findViewById(R.id.dest_spinner);
        valueToConvert = findViewById(R.id.Input_Box);
        valueTx = findViewById(R.id.Tx_Box);
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
        CurrencyRateHandler CR = new CurrencyRateHandler();
        CR.doInBackground();

        // Spinner set variables with monnaie_array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.monnaie_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Current_spinner.setAdapter(adapter);
        Dest_spinner.setAdapter(adapter);

        euroToDollar = CR.getCurrencyRateByName("USD");
        euroToYen = CR.getCurrencyRateByName("JPY");
        euroToPeso = CR.getCurrencyRateByName("MXN");

        // On convert button clicked
        ConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Tag 2", "onClick: passed");
                String value = valueToConvert.getText().toString();
                String Tx = valueTx.getText().toString();
                String current_monnaie = Current_spinner.getSelectedItem().toString();
                Log.e("Tag 3", "onClick: monnaie selected : " + current_monnaie);
                String dest_monnaie = Dest_spinner.getSelectedItem().toString();
                Log.e("Tag 4", "onClick: monnaie dest : " + dest_monnaie);
                checkAndCompleteString(Tx,value);
            }
        });
    }

    /*
     * Convert the current monnaie into the chosen currency
     * @param amont     amont of monnai to convert
     * @return amont    converted amont of monnaie
     */
    float calculMonnaie(float amont){
        String Current_monnaie = Current_spinner.getSelectedItem().toString();
        String Dest_monnaie = Dest_spinner.getSelectedItem().toString();
        if(Current_monnaie.equals(EURO) && Dest_monnaie.equals(YEN))
            return amont * euroToPeso;
        if(Current_monnaie.equals(EURO) && Dest_monnaie.equals(PESO))
            return amont * euroToPeso;
        if(Current_monnaie.equals(EURO) && Dest_monnaie.equals(DOLLAR))
            return amont * euroToDollar;
        if(Current_monnaie.equals(DOLLAR) && Dest_monnaie.equals(PESO))
            return amont * dollarToPeso;
        if(Current_monnaie.equals(DOLLAR) && Dest_monnaie.equals(YEN))
            return amont * dollarToYen;
        if(Current_monnaie.equals(YEN) && Dest_monnaie.equals(PESO))
            return amont * yenToPeso;
        if(Current_monnaie.equals(YEN) && Dest_monnaie.equals(DOLLAR))
            return amont / dollarToPeso;
        if(Current_monnaie.equals(PESO) && Dest_monnaie.equals(DOLLAR))
            return amont / dollarToPeso;
        if(Current_monnaie.equals(PESO) && Dest_monnaie.equals(YEN))
            return amont / yenToPeso;
        if(Current_monnaie.equals(DOLLAR) && Dest_monnaie.equals(EURO))
            return amont / euroToDollar;
        if(Current_monnaie.equals(YEN) && Dest_monnaie.equals(EURO))
            return amont / euroToYen;
        if(Current_monnaie.equals(PESO) && Dest_monnaie.equals(EURO))
            return amont / euroToPeso;
        if(Current_monnaie.equals(Dest_monnaie))
            return amont;
        else
            return 0;

    }

    /*
     * Check if the input value is correct and formated
     * @param Tx    input rate of currency (only for TP0-3)
     * @param value input of the amont of monnaie to convert
     */
    void checkAndCompleteString(String Tx, String value){
        // If the user put a , instead of .
        // Replace it
        if(Tx.contains(",")){
            Tx = Tx.replace(",",".");
        }
        if(value.contains(",")){
            value = value.replace(",",".");
        }

        // Verify is the user put only digit in the inputbox
        // useless if we chose only digit keyboard on activity_main.xml
        /*for(char c:value.toCharArray()){
            if(!Character.isDigit(c)){
                ResultBox.setText("Value compute is not a digit.");
                return;
            }
        }*/

        float convertValue = calculMonnaie(Float.parseFloat(value));
        String symbole = getSymbole();
        ResultBox.setText(Float.toString(convertValue) + symbole);
    }

    /*
     * Get the symbole to print from the spinner value
     * @return  the string containing the currency symbole
     */
    String getSymbole(){
        String monnaie = Dest_spinner.getSelectedItem().toString();
        if(monnaie.equals(EURO))
            return " €";
        if(monnaie.equals(DOLLAR))
            return " $";
        if(monnaie.equals(PESO))
            return " Mex$";
        if(monnaie.equals(YEN))
            return " ¥";
        else
            return "unknown";
    }
}