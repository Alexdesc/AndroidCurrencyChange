package com.example.tp0;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class CurrencyRateHandler extends AsyncTask<String, String, String> {

    private static final String CURRENCY = "currency";
    private static final String CUBE_NODE = "//Cube/Cube/Cube";
    private static final String RATE = "rate";

    // HashMap of Currency / Rate
    HashMap<String, String> currencyRate = new HashMap<String, String>();

    @Override
    protected String doInBackground(String... strings) {
        // Add Euros because not on XML from ecb
        currencyRate.put("EUR","1");
        this.getXMLFromUrl("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        return null;
    }

    /*
     * Get the xml content by the given url, parse into HashMap
     * @param   the chosen url
     */
    public void getXMLFromUrl(String ThisURL) {
        Log.e("Tag currency handler 1", "getXMLFromUrl: launched ");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        Log.e("Tag currency handler 2", "getXMLFromUrl: document created ");

        try {
            URL url = new URL(ThisURL);
            InputSource xml = new InputSource(url.openStream());
            Log.e("Tag currency handler 3", "getXMLFromUrl: url opened in outputStream");
            document = builder.parse(xml);
            Log.e("Tag currency handler 4", "getXMLFromUrl: document parse");

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xPathString = CUBE_NODE;
            XPathExpression expr = xpath.compile(xPathString);
            NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                NamedNodeMap attribs = node.getAttributes();
                if (attribs.getLength() > 0) {
                    Node currencyAttrib = attribs.getNamedItem(CURRENCY);
                    if (currencyAttrib != null) {
                        String currencyTxt = currencyAttrib.getNodeValue();
                        String rateTxt = attribs.getNamedItem(RATE).getNodeValue();
                        currencyRate.put(currencyTxt,rateTxt);
                    }
                }
            }
        } catch (SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }

        for(Map.Entry<String, String> entry : currencyRate.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.e("Tag currency handler","Key : " + key + " value : " + value);
        }
    }

    /*
     * Return currency value from the initialised HashMap
     * @param   the currency to convert
     */
    float getCurrencyRateByName(String currency){
        for(Map.Entry<String, String> entry : currencyRate.entrySet()) {
            if(entry.getKey().equals(currency))
                return Float.parseFloat(entry.getValue());
            else
                continue;
        }
        return 0;
    }

    /*
     * Return currency value from the initialised HashMap
     * @param   The map of currency (if not connected to the internet)
     * @param   the currency to convert
     */
    float getCurrencyRateByMap(HashMap<String,String> currencyMap,String currency){
        for(Map.Entry<String, String> entry : currencyMap.entrySet()) {
            if(entry.getKey().equals(currency))
                return Float.parseFloat(entry.getValue());
            else
                continue;
        }
        return 0;
    }
}
