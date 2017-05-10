package keene.drugdatabase;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.*;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    JSONObject jsonobject;
    JSONArray jsonarray;
    ArrayList<String> drugNames;
    ArrayList<Drug> drugs;
    private ImageView imgView;
    Button start;
    Button clear;
    EditText boxOne;
    EditText boxTwo;
    int wins, losses = 0;
    int rounds = 0;
    String firstName,lastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.Start);
        clear = (Button) findViewById(R.id.clear);
        boxOne = (EditText) findViewById(R.id.name);
        boxTwo = (EditText) findViewById(R.id.major);
        Log.e("tag", "first activity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.e("tag", "honeycomb");
            new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new DownloadJSON().execute();
        }
    }



    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.e("tag", "runnung downloadJSOn");

            drugs = new ArrayList<Drug>();
//Create Array of Drugs
            drugNames = new ArrayList<String>();
            // JSON file URL address
            jsonobject = JSONfunctions.getJSONfromURL("http://www.cs.bc.edu/~signoril/drugs");

            Log.e("json object",jsonobject.toString());
            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("drugs");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);

                    Drug newDrug = new Drug();

                    newDrug.setAddictions(jsonobject.optString("addictions"));
                    newDrug.setDrug(jsonobject.optString("drug"));
                    newDrug.setDeaths(jsonobject.optString("deaths"));
                    newDrug.setUsers(jsonobject.optString("users"));
                    newDrug.setFlag(jsonobject.optString("flag"));


                    //now let's pull down the flag bitmap using a seperate call to a website
//                    newDrug.drugPic = getImage.getImage(newDrug.getFlag());

                    drugs.add(newDrug);

                    // Populate spinner with country names
                    drugNames.add(jsonobject.optString("drug"));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onClick(View operatorButtons){

        switch (operatorButtons.getId()){
            case R.id.clear:
                boxOne.setText("");
                boxTwo.setText("");
                break;
            case R.id.Start:
                firstName =  boxOne.getText().toString();
                lastName =  boxTwo.getText().toString();
                Intent usersIntent = new Intent(MainActivity.this, moreUsersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ARRAYLIST",(Serializable)drugs);
                usersIntent.putExtra("BUNDLE", bundle);
                usersIntent.putExtra("drugNames",drugNames);
                usersIntent.putExtra("firstname",firstName);
                usersIntent.putExtra("lastname", lastName);
                usersIntent.putExtra("wins", wins);
                usersIntent.putExtra("loses", losses);
                usersIntent.putExtra("rounds", rounds);
                startActivity(usersIntent);
                break;
            case R.id.scores:
                Intent endIntent = new Intent(MainActivity.this, endActivity.class);
                startActivity(endIntent);
        }
    }
}
