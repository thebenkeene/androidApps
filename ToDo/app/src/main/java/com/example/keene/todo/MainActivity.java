package com.example.keene.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.metje.todo.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
	private ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> taskList;
//	private TextToSpeech tts;
//	private boolean speechReady = false;
	private SQLiteDatabase taskDB;
	private ListView taskListView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		taskListView = (ListView) findViewById(R.id.taskListView);

//		tts  = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//			@Override
//			public void onInit(int status) {
//				speechReady = true;
//			}
//		});

//		Set up SQLITE DB
		taskDB = openOrCreateDatabase("taskDB",MODE_PRIVATE,null);
		taskDB.execSQL("CREATE TABLE IF NOT EXISTS task(name VARCHAR, description VARCHAR, parent VARCHAR);");

//		Sets up floating add button
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showNewTaskDialog();
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		populateTaskList();
		arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
		taskListView.setAdapter(arrayAdapter);

		taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Intent subtaskIntent = new Intent(MainActivity.this, SubtaskActivity.class);
				Bundle bundle  = new Bundle();
				bundle.putString("parent",taskList.get(i));
				subtaskIntent.putExtras(bundle);
				startActivity(subtaskIntent);
			}
		});

		taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				// Delete element
				taskDB.delete("task","name=?",new String[]{taskList.get(i)});
				taskDB.delete("task","parent=?",new String[]{taskList.get(i)});
				taskList.remove(i);
				arrayAdapter.notifyDataSetChanged();
				return  true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_clear_all)
		{
			taskDB.delete("task","",new String[]{});
//			taskList = new ArrayList<>();
			taskList.clear();
			arrayAdapter.notifyDataSetChanged();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showNewTaskDialog()
	{
//		Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("New Task");
		final EditText nameInput = new EditText(MainActivity.this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		nameInput.setHint("Name");
		final EditText descriptionInput = new EditText(MainActivity.this);
		descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT);
		descriptionInput.setHint("Description");

		LinearLayout layout = new LinearLayout(MainActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(nameInput);
		layout.addView(descriptionInput);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameInput.getText().toString();
				String description = descriptionInput.getText().toString();
				taskDB.execSQL("INSERT INTO task VALUES('" + name+ "','"+ description +"', '');");
				taskList.add(name);
				arrayAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setView(layout);

		final Button button = builder.show().getButton(AlertDialog.BUTTON_POSITIVE);
		button.setEnabled(false);
		nameInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (nameInput.getText().length() <= 0 || taskList.contains(nameInput.getText().toString())) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Name too short or exist already", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void populateTaskList()
	{
		taskList = new ArrayList<>();
		Cursor res = taskDB.rawQuery("Select * from task where parent=''",null);
		res.moveToFirst();

		while(!res.isAfterLast()){
			taskList.add(res.getString(res.getColumnIndex("name")));
			res.moveToNext();
		}

		res.close();
	}
}
