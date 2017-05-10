package com.example.keene.fixedpricemeal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.example.ngklingler.fixedpricemeal.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class picker extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] stateArray;
    String task;

    String[] appetizers = {"Bacon Wrapped Snails", "Fried Beetles", "Saute Rattlesnake", "Hot and Spicy Chicken Feet", "Stuffed Blobfish"};
    int[] appetizerImages = {R.mipmap.baconwrappedsnails, R.mipmap.friedbeetles, R.mipmap.sauterattlesnake, R.mipmap.chickenfeet, R.mipmap.blobfish};
    String[] pasta = {"Spaghetti Alla Vongole", "Penne Puttanesca", "Rigatoni Alla Bolognese", "Cannelloni", "Arugula Ravioli", "Gnocchi Alle Castagne"};
    int[] pastaImages = {R.mipmap.vongole, R.mipmap.puttanesca, R.mipmap.bolognese, R.mipmap.cannelloni, R.mipmap.arugularavioli, R.mipmap.castagne};
    String[] meat = {"Tatarbeefsteak", "Pfeffersteak", "Seezungenfilet", "Langosta"};
    int[] meatImages = {R.mipmap.tatarbeefsteak, R.mipmap.pfeffersteak, R.mipmap.seezungenfilet, R.mipmap.langosta};
    String[] dessert = {"Clafoutis", "Mille-feuille", "Kouign-amann"};
    int[] dessertImages = {R.mipmap.clafoutis, R.mipmap.millefeuille, R.mipmap.kouignamann};

    String[] food = new String[0];
    int[] pictures = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        Intent input = getIntent();
        stateArray = input.getStringArrayExtra("state");
        task = input.getStringExtra("task");

        switch (task) {
            case "appetizer":
                food = appetizers;
                pictures = appetizerImages;
                break;
            case "pasta":
                food = pasta;
                pictures = pastaImages;
                break;
            case "meat":
                food = meat;
                pictures = meatImages;
                break;
            case "dessert":
                food = dessert;
                pictures = dessertImages;
                break;
        }

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<food.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", food[i]);
            hm.put("flag", Integer.toString(pictures[i]));
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "flag","txt" };

        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = (ListView) findViewById(R.id.food_list);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (task) {
            case "appetizer":
                stateArray[0] = food[position];
                break;
            case "pasta":
                stateArray[1] = food[position];
                break;
            case "meat":
                stateArray[2] = food[position];
                break;
            case "dessert":
                stateArray[3] = food[position];
                break;
        }
        intent.putExtra("state", stateArray);
        startActivity(intent);

    }
}
