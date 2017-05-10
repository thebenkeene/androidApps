package com.example.keene.fixedpricemeal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.example.ngklingler.fixedpricemeal.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    Button appetizerBtn;
    Button pastaBtn;
    Button meatBtn;
    Button dessertBtn;
    Button clearBtn;
    Button confirmBtn;

    TextView appetizerText;
    TextView pastaText;
    TextView meatText;
    TextView dessertText;

    String[] stateArray = {"","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appetizerBtn = (Button) findViewById(R.id.appetizerBtn);
        pastaBtn = (Button) findViewById(R.id.pastaBtn);
        meatBtn = (Button) findViewById(R.id.meatBtn);
        dessertBtn = (Button) findViewById(R.id.dessertBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);

        appetizerText = (TextView) findViewById(R.id.appetizerText);
        pastaText = (TextView) findViewById(R.id.pastaText);
        meatText = (TextView) findViewById(R.id.meatText);
        dessertText = (TextView) findViewById(R.id.dessertText);

        Intent input = getIntent();

        if (input.hasExtra("state")) { stateArray = input.getStringArrayExtra("state"); }
        appetizerText.setText("Appetizer: " + stateArray[0]);
        pastaText.setText("Pasta: " + stateArray[1]);
        meatText.setText("Meat/Fish: " + stateArray[2]);
        dessertText.setText("Dessert: " + stateArray[3]);
    }

    public void onConfirm() {
        if (stateArray[0].length() < 3 || stateArray[1].length() < 3 || stateArray[2].length() < 3 || stateArray[3].length() < 3) {
            Context context = getApplicationContext();
            CharSequence text = "One or more items not selected";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            Intent intent = new Intent(this, PlaceOrder.class);
            intent.putExtra("state", stateArray);
            startActivity(intent);
        }
    }

    public void onClear() {
        stateArray[0] = "";
        stateArray[1] = "";
        stateArray[2] = "";
        stateArray[3] = "";
        appetizerText.setText("Appetizer: ");
        pastaText.setText("Pasta: ");
        meatText.setText("Meat/Fish: ");
        dessertText.setText("Dessert: ");
    }

    public void onClick(View operatorButtons) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Intent intent = new Intent(this, picker.class);

        switch (operatorButtons.getId()) {
            case R.id.appetizerBtn:
                intent.putExtra("state", stateArray);
                intent.putExtra("task", "appetizer");
                startActivity(intent);
                break;
            case R.id.pastaBtn:
                intent.putExtra("state", stateArray);
                intent.putExtra("task", "pasta");
                startActivity(intent);
                break;
            case R.id.meatBtn:
                intent.putExtra("state", stateArray);
                intent.putExtra("task", "meat");
                startActivity(intent);
                break;
            case R.id.dessertBtn:
                intent.putExtra("state", stateArray);
                intent.putExtra("task", "dessert");
                startActivity(intent);
                break;
            case R.id.clearBtn:
                onClear();
                break;
            case R.id.confirmBtn:
                onConfirm();
                break;
        }

    }


}
