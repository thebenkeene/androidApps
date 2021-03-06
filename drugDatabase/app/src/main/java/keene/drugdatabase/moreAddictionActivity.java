package keene.drugdatabase;

/**
 * Created by BigBen on 5/3/17.
 */
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.*;
import java.util.*;
import java.lang.Object.*;

public class moreAddictionActivity extends AppCompatActivity{
    Button drugA,drugB;
    private Random generator;
    //    MainActivity main = new MainActivity();
    int first,second,correct;
    TextView score, result;
    boolean clicked = false;
    ArrayList<String> drugNames;
    ArrayList<Drug> drugs;
    String firstName,lastName;
    int wins, losses;
    int rounds;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addictionsactivity);
        drugA = (Button) findViewById(R.id.btn1);
        drugB = (Button) findViewById(R.id.btn2);
        score = (TextView) findViewById(R.id.currentScore);
        result = (TextView) findViewById(R.id.result);
        Intent input = getIntent();
        drugNames = input.getStringArrayListExtra("drugNames");
        Bundle bundle = input.getBundleExtra("BUNDLE");
        drugs = (ArrayList<Drug>) bundle.getSerializable("ARRAYLIST");
        firstName = input.getStringExtra("firstname");
        lastName = input.getStringExtra("lastname");
        wins = input.getIntExtra("wins", 0);
        losses = input.getIntExtra("losses", 0);
        rounds = input.getIntExtra("rounds", 0);
        setandSelectDrugs();
        findWinner();

    }

    public void onClick(View opButtons){

        switch (opButtons.getId()){
            case R.id.btn1:
                if (!clicked) {
                    clicked = true;
                    if (correct == 0) {
                        result.setText("Correct!");
                        wins++;
                        score.setText(wins + "-" + losses);
                    }
                    else {
                        result.setText("Sorry you are wrong.");
                        losses++;
                        score.setText(wins + "-" + losses);
                    }
                }
                break;
            case R.id.btn2:
                if (!clicked) {
                    clicked = true;
                    if (correct == 0) {
                        result.setText("Sorry you are wrong.");
                        losses++;
                        score.setText(wins + "-" + losses);
                    }
                    else {
                        result.setText("Correct!");
                        wins++;
                        score.setText(wins + "-" + losses);
                    }
                }
                break;
            case R.id.next:
                if (clicked){
                    Intent deathsIntent = new Intent(moreAddictionActivity.this, moreDeathsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ARRAYLIST",(Serializable)drugs);
                    deathsIntent.putExtra("BUNDLE", bundle);
                    deathsIntent.putExtra("drugNames",drugNames);
                    deathsIntent.putExtra("firstname",firstName);
                    deathsIntent.putExtra("lastname", lastName);
                    deathsIntent.putExtra("wins", wins);
                    deathsIntent.putExtra("losses", losses);
                    deathsIntent.putExtra("rounds", rounds);
                    startActivity(deathsIntent);
                }
                break;

        }
    }

    public void setandSelectDrugs(){
        generator = new Random();
        first = generator.nextInt(drugs.size());
        second = generator.nextInt(drugs.size());
        while (first == second){
            first = generator.nextInt(drugs.size());
            second = generator.nextInt(drugs.size());
        }
        drugA.setText(drugNames.get(first));
        drugB.setText(drugNames.get(second));

    }

    public void findWinner(){
        if (Integer.parseInt(drugs.get(first).getAddictions())> Integer.parseInt(drugs.get(second).getAddictions()))
            correct = 0;
        else
            correct = 1;
    }
}
