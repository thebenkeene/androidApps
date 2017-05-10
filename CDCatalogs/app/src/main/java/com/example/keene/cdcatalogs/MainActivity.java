package com.example.keene.cdcatalogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.example.metje.cdcatalogs.R;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
	private SQLiteDatabase catalogDB;
	private ArrayAdapter<String> arrayAdapter;

	private List<Map<String, String>> albumDataList = new ArrayList<>();
	private ArrayList<String> albumList;
	private ArrayList<String> artistList;

	private Map<String, String> titleArtistMap;
	private ListView albumListView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		albumListView = (ListView) findViewById(R.id.albumListView);
		albumDataList  = new ArrayList<>();
		artistList = new ArrayList<>();
		albumList = new ArrayList<>();
		titleArtistMap = new HashMap<>();
		albumDataList.add(titleArtistMap);

		catalogDB = openOrCreateDatabase("catalogDB",MODE_PRIVATE,null);


		catalogDB.execSQL("CREATE TABLE IF NOT EXISTS album(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, artist VARCHAR, year integer);");

		arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumList);
		albumListView.setAdapter(arrayAdapter);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showNewAlbumDialog();
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		populateAlbumInfoList();

		albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Intent songlistIntent = new Intent(MainActivity.this, SongListActivity.class);
				Bundle bundle  = new Bundle();

				bundle.putString("artist",artistList.get(i));
				bundle.putString("album",albumList.get(i));

				songlistIntent.putExtras(bundle);
				startActivity(songlistIntent);
			}
		});

		albumListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				// Delete element
				String title = albumList.get(i);
				String artist = artistList.get(i);

				Cursor res = catalogDB.rawQuery("Select id from album where title like '"+ title +"' and artist like '"+ artist +"'",null);
				res.moveToFirst();

				int id = res.getInt(res.getColumnIndex("id"));

				catalogDB.delete("song","album=?",new String[]{Integer.toString(id)});
				catalogDB.delete("album","title=?",new String[]{albumList.get(i)});

				albumList.remove(i);
				artistList.remove(i);
				arrayAdapter.notifyDataSetChanged();
				return  true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_clear)
		{
			catalogDB.delete("song","",new String[]{});
			catalogDB.delete("album","",new String[]{});

			artistList.clear();
			albumList.clear();

			arrayAdapter.notifyDataSetChanged();
		}

		if (id == R.id.action_help)
		{
			Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
			startActivity(helpIntent);
		}

		return super.onOptionsItemSelected(item);
	}

	private void showNewAlbumDialog()
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("New Album");

		final EditText albumInput = new EditText(MainActivity.this);
		albumInput.setInputType(InputType.TYPE_CLASS_TEXT);
		albumInput.setHint("Name");

		final EditText artistNameInput = new EditText(MainActivity.this);
		artistNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		artistNameInput.setHint("Artist");

		final EditText yearInput = new EditText(MainActivity.this);
		yearInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		yearInput.setHint("Year");

		LinearLayout layout = new LinearLayout(MainActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);

		layout.addView(albumInput);
		layout.addView(artistNameInput);
		layout.addView(yearInput);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = albumInput.getText().toString();

				String artistName = artistNameInput.getText().toString();
				String year = yearInput.getText().toString();


				catalogDB.execSQL("INSERT INTO album (title, artist, year) VALUES('" + name + "','"+ artistName
						                  + "',"+ year +");");
				albumList.add(name);
				artistList.add(artistName);
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

		albumInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (albumInput.getText().length() <= 0) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Album title too short!", Toast.LENGTH_SHORT).show();
				} else {
					if ((albumList.contains(albumInput.getText().toString())
							     && artistList.contains(artistNameInput.getText().toString()))
							    || (yearInput.getText().length() <= 0
									        || Integer.parseInt(yearInput.getText().toString()) < 1000
									        || Integer.parseInt(yearInput.getText().toString()) > Calendar.getInstance().get(Calendar.YEAR)))
						button.setEnabled(false);
					else
						button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		artistNameInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (albumList.contains(albumInput.getText().toString()) && artistList.contains(artistNameInput.getText().toString())) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Album title with similar artist already exist", Toast.LENGTH_SHORT).show();
				} else if (artistNameInput.getText().toString().length() == 0) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Artist name too short!", Toast.LENGTH_SHORT).show();
				} else {
					if ((albumInput.getText().length() <= 0) || (yearInput.getText().length() <= 0
							                                             || Integer.parseInt(yearInput.getText().toString()) < 1000
							                                             || Integer.parseInt(yearInput.getText().toString()) > Calendar.getInstance().get(Calendar.YEAR)))
						button.setEnabled(false);
					else
						button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		yearInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (yearInput.getText().length() <= 0 ) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Year cannot be empty", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(yearInput.getText().toString()) < 1000) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "That's not a reasonable year", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(yearInput.getText().toString()) > Calendar.getInstance().get(Calendar.YEAR)) {
					button.setEnabled(false);
					Toast.makeText(MainActivity.this, "Too far ahead", Toast.LENGTH_SHORT).show();
				} else {
					if ((albumInput.getText().length() <= 0) ||
							    (albumList.contains(albumInput.getText().toString()) && artistList.contains(artistNameInput.getText().toString())))
						button.setEnabled(false);
					else
						button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	private void populateAlbumInfoList()
	{
		albumList = new ArrayList<>();
		artistList = new ArrayList<>();


		Cursor res = catalogDB.rawQuery("Select * from album",null);
		res.moveToFirst();

		while(!res.isAfterLast()){
			albumList.add(res.getString(res.getColumnIndex("title")));
			artistList.add(res.getString(res.getColumnIndex("artist")));
			res.moveToNext();
		}

		res.close();


		arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, albumList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);

				text1.setText(albumList.get(position));
				text2.setText(artistList.get(position));
				return view;
			}
		};
		albumListView.setAdapter(arrayAdapter);

	}
}