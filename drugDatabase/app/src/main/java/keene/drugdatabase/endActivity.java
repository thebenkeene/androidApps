package keene.drugdatabase;

/**
 * Created by BigBen on 5/6/17.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class endActivity extends AppCompatActivity {
    private SQLiteDatabase sampleDB = null;
    private databaseHelper playerHelper;
    private playerAdapter playerAdapter;
    private SimpleCursorAdapter dataAdapter;
    MainActivity main = new MainActivity();
    String firstName,lastName;
    int wins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endclass);
        Intent input = getIntent();
        firstName = input.getStringExtra("firstname");
        lastName = input.getStringExtra("lastname");
        wins = input.getIntExtra("wins", 0);
        try
        {
            sampleDB = openOrCreateDatabase("PlayerDB", MODE_PRIVATE, null);
            playerHelper = new databaseHelper(this,sampleDB);
            playerAdapter = new playerAdapter(this,sampleDB,playerHelper);

        }
        catch(SQLiteException se)
        {
            Log.e(getClass().getSimpleName(),
                    "Could not create or Open the database");
        }
        insert();
        displayListView();
    }


    public void insert() {
        // do stuff here
        //get the data from the edittext fields

//        firstname = main.firstName;
//        lastname = main.lastName;
//        score = main.wins;
        playerAdapter.createPlayer(firstName,lastName,wins);

    }

    private void displayListView() {

        Cursor cursor = playerAdapter.fetchAllStudents();
        // The desired columns to be bound
        //see SimpleCursorAdapter(this, R.layout.country_info, cursor, columns, to, 0);
        String[] columns = new String[]{
                playerAdapter.FIRSTNAME,
                playerAdapter.LASTNAME,
                playerAdapter.SCORE
        };


        // the XML defined views which the data will be bound to
        //see SimpleCursorAdapter(this, R.layout.country_info, cursor, columns, to, 0);
        int[] to = new int[]{
                R.id.first,
                R.id.last,
                R.id.score
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        //uses builtin SimpleCursorAdapter to create List
        //parameters columns and to bind database field names to ListView xml names
        dataAdapter = new SimpleCursorAdapter(this, R.layout.playerow, cursor, columns, to,0);

        final ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        // set up event listening for clicks on the list for LONG click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String playerName =
                        cursor.getString(cursor.getColumnIndexOrThrow("FIRSTNAME"));
                Toast.makeText(getApplicationContext(),
                        playerName + " has been Deleted", Toast.LENGTH_SHORT).show();

                playerAdapter.deleteOnePlayer(playerName);
                displayListView();
                return true;
            }
        });


    }
    @Override
    public void onPause() {
        super.onPause();
        playerAdapter.close();
    }

}
