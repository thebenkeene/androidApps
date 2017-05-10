package com.example.keene.fixedpricemeal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.example.ngklingler.fixedpricemeal.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class PlaceOrder extends AppCompatActivity {

    String[] order;
    TextView orderinfo;
    EditText nameinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        orderinfo = (TextView) findViewById(R.id.orderinfo);
        nameinput = (EditText) findViewById(R.id.nameinput);

        Intent input = getIntent();
        order = input.getStringArrayExtra("state");
        orderinfo.setText(order[0] + ", " + order[1] + ", " + order[2] + ", and " + order[3]);

    }

    public void onClick(View operatorbuttons) {
        if (operatorbuttons.getId() == R.id.sendorder) {
            String name = nameinput.getText().toString();
            CharSequence text;
            if (name.length() < 1) {
                text = "Error, no name entered.";
            } else {
                text = "Order sent to kitchen";
            }
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        if (operatorbuttons.getId() == R.id.neworder) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }




}
