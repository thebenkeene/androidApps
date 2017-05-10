package com.example.keene.todo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import com.example.metje.todo.R;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class SubtaskActivity extends AppCompatActivity
{
	private String parentName;
	private ArrayList<String> subtaskList;
	private ArrayAdapter<String> adapter;
	private SQLiteDatabase taskDB;
	private ListView subtaskListView;
	private TextView descriptionView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subtask);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		parentName = getIntent().getExtras().getString("parent");
		setTitle(parentName);

		taskDB = openOrCreateDatabase("taskDB",MODE_PRIVATE,null);
		taskDB.execSQL("CREATE TABLE IF NOT EXISTS task(name VARCHAR, description VARCHAR, parent VARCHAR);");

		populateSubtasks();
		setupDescriptionView();

		subtaskListView = (ListView) findViewById(R.id.subtaskListView);

		adapter = new ArrayAdapter<>(SubtaskActivity.this, android.R.layout.simple_list_item_1, subtaskList);
//		adapter = new CustomAdapter()
		subtaskListView.setAdapter(adapter);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				showNewTaskDialog();
				Snackbar.make(view, "New Subtask added", Snackbar.LENGTH_SHORT).show();
			}
		});

		descriptionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				showDescriptionChangeDialog();
				Snackbar.make(view, "Description Changed", Snackbar.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		subtaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l)
			{
				Cursor res = taskDB.rawQuery("Select description from task where name ='"+ subtaskList.get(i) +
						                             "' and parent='"+ parentName +"'",null);
				res.moveToFirst();

				String description = res.getString(0);
				res.close();

				AlertDialog.Builder builder = new AlertDialog.Builder(SubtaskActivity.this);
				builder.setTitle(subtaskList.get(i));

				final TextView descriptionView = new TextView(SubtaskActivity.this);

				descriptionView.setText(description);
				descriptionView.setGravity(Gravity.CENTER);

				LinearLayout layout = new LinearLayout(SubtaskActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.addView(descriptionView);

				builder.setView(layout);
				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						taskDB.delete("task","name=? and parent=?",new String[]{subtaskList.get(i),parentName});
						subtaskList.remove(i);
						adapter.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

				builder.show();
			}
		});

		subtaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				return  true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_subtask, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getItemId();


		if (id == R.id.action_rename)
		{
			showTaskNameChangeDialog();
		}

		if (id == R.id.action_clear_all_sub)
		{
			taskDB.delete("task","parent=?",new String[]{parentName});
			subtaskList.clear();
			adapter.notifyDataSetChanged();
		}

		if (id == R.id.action_delete)
		{
			taskDB.delete("task","name=? and parent=?",new String[]{parentName, ""});
			taskDB.delete("task","parent=?",new String[]{parentName});
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupDescriptionView()
	{
		descriptionView = (TextView) findViewById(R.id.descriptionView);
		Cursor res = taskDB.rawQuery("Select description from task where name='"+ parentName + "'",null);
		res.moveToFirst();
		String description = res.getString(res.getColumnIndex("description"));
		if (description.compareTo("") != 0)
		{
			descriptionView.setText(description);
//			descriptionView.setTypeface(null, Typeface.NORMAL);
		}

		res.close();
	}

	private void populateSubtasks()
	{
		subtaskList = new ArrayList<>();

		Cursor res = taskDB.rawQuery("Select * from task where parent='"+ parentName + "'",null);
		res.moveToFirst();

		while(!res.isAfterLast()){
			subtaskList.add(res.getString(res.getColumnIndex("name")));
			res.moveToNext();
		}

		res.close();
	}

	private void showNewTaskDialog()
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(SubtaskActivity.this);
		builder.setTitle("New Subtask");
		final EditText nameInput = new EditText(SubtaskActivity.this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		nameInput.setHint("Name");
		final EditText descriptionInput = new EditText(SubtaskActivity.this);
		descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT);
		descriptionInput.setHint("Description");

		LinearLayout layout = new LinearLayout(SubtaskActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(nameInput);
		layout.addView(descriptionInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameInput.getText().toString();
				String description = descriptionInput.getText().toString();
				taskDB.execSQL("INSERT INTO task VALUES('" + name+ "','"+ description +"', '"+ parentName +"');");
				subtaskList.add(name);
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		final Button button = builder.show().getButton(AlertDialog.BUTTON_POSITIVE);
		button.setEnabled(false);
		nameInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (nameInput.getText().length() <= 0 || subtaskList.contains(nameInput.getText().toString())) {
					button.setEnabled(false);
					Toast.makeText(SubtaskActivity.this, "Name too short or exist already", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	private void showTaskNameChangeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SubtaskActivity.this);
		builder.setTitle("Change Subtask Name");
		final EditText nameInput = new EditText(SubtaskActivity.this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		nameInput.setHint("New Name");


		LinearLayout layout = new LinearLayout(SubtaskActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(nameInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Toast.makeText(SubtaskActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		final Button button = builder.show().getButton(AlertDialog.BUTTON_POSITIVE);
		button.setEnabled(false);
		nameInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (nameInput.getText().length() <= 0 || subtaskList.contains(nameInput.getText().toString())) {
					button.setEnabled(false);
					Toast.makeText(SubtaskActivity.this, "Name too short or exist already", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	private void showDescriptionChangeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SubtaskActivity.this);
		builder.setTitle("Change Description");
		final EditText descriptionInput = new EditText(SubtaskActivity.this);
		descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT);
		descriptionInput.setHint("New Description");

		LinearLayout layout = new LinearLayout(SubtaskActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(descriptionInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				could use ContentValues
				String description = descriptionInput.getText().toString();
				taskDB.execSQL("UPDATE task SET description ='"+ description + "' where name='"+ parentName + "';");
				descriptionView.setText(description);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}
}
