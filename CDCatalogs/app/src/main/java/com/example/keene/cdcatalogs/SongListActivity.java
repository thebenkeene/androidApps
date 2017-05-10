package com.example.keene.cdcatalogs;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class SongListActivity extends AppCompatActivity
{
	private SQLiteDatabase catalogDB;
	private int albumID;

	private String artist;
	private String album;

	private TextView artisttNameView;
	private TextView albumNameView;
	private TextView release_year_view;
	private TextView num_songs_view;

	private ListView song_list_view;
	private ArrayList<String> songlist;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		catalogDB = openOrCreateDatabase("catalogDB",MODE_PRIVATE,null);
		catalogDB.execSQL("CREATE TABLE IF NOT EXISTS song(title VARCHAR, album integer);");

		artist = getIntent().getExtras().getString("artist");
		album = getIntent().getExtras().getString("album");
		albumID = getAlbumID();

		artisttNameView = (TextView) findViewById(R.id.artisttName);
		artisttNameView.setText("Artist: " + artist);

		albumNameView = (TextView) findViewById(R.id.albumName);
		albumNameView.setText("Albumt: " + album);

		release_year_view = (TextView) findViewById(R.id.release_year);
		release_year_view.setText("Year: " + getYear());

		num_songs_view = (TextView) findViewById(R.id.num_songs);

		populateSongList();
		setTitle(album);

		song_list_view = (ListView) findViewById(R.id.song_list);
		adapter = new ArrayAdapter<>(SongListActivity.this, android.R.layout.simple_list_item_1, songlist);
		song_list_view.setAdapter(adapter);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showNewSongDialog();
				Snackbar.make(view, "New Subtask", Snackbar.LENGTH_SHORT).show();
			}
		});
	}

	protected void onStart()
	{
		super.onStart();

		artisttNameView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				showArtistChangeDialog();
				Snackbar.make(view, "Artist Changed", Snackbar.LENGTH_SHORT).show();
			}
		});

		albumNameView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				showAlbumChangeDialog();
				Snackbar.make(view, "Album Changed", Snackbar.LENGTH_SHORT).show();
			}
		});

		release_year_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				showYearChangeDialog();
				Snackbar.make(view, "Year Changed", Snackbar.LENGTH_SHORT).show();
			}
		});

		song_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{

				catalogDB.delete("song","title=? and album=?",new String[]{songlist.get(i), Integer.toString(albumID)});
				songlist.remove(i);
				num_songs_view.setText("# of tracks: " + Integer.toString(songlist.size()));
				adapter.notifyDataSetChanged();
				return  true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		getMenuInflater().inflate(R.menu.menu_song_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_clear_songs)
		{
			catalogDB.delete("song","album=?",new String[]{Integer.toString(albumID)});

			songlist.clear();
			adapter.notifyDataSetChanged();
		}

		if (id == R.id.action_delete)
		{
			catalogDB.delete("song","album=?",new String[]{Integer.toString(albumID)});
			catalogDB.delete("album","id=?",new String[]{Integer.toString(albumID)});
			finish();
		}

		if (id == R.id.action_help)
		{
			Intent helpIntent = new Intent(SongListActivity.this, HelpActivity.class);
			startActivity(helpIntent);
		}

		return super.onOptionsItemSelected(item);
	}

	private int getAlbumID()
	{
		Cursor res = catalogDB.rawQuery("Select id from album where title like '"+ album
				                                +"' and artist like '"+ artist +"';",null);
		res.moveToFirst();
		return res.getInt(0);
	}

	private String getYear()
	{
		Cursor res = catalogDB.rawQuery("Select year from album where id="+ albumID +";",null);
		res.moveToFirst();
		return Integer.toString(res.getInt(0));
	}

	private  void showArtistChangeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SongListActivity.this);
		builder.setTitle("Update Artist Name");

		final EditText artistInput = new EditText(SongListActivity.this);
		artistInput.setInputType(InputType.TYPE_CLASS_TEXT);
		artistInput.setHint("New Artist name");

		LinearLayout layout = new LinearLayout(SongListActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(artistInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newArtist = artistInput.getText().toString();
				catalogDB.execSQL("UPDATE album SET artist ='"+ newArtist + "' where title='"+ album
						                  + "' and artist='"+ artist +"';");
				artisttNameView.setText("Artist: " + newArtist);
				artist = newArtist;
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

		artistInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (artistInput.getText().toString().length() == 0) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Artist name too short!", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private  void showAlbumChangeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SongListActivity.this);
		builder.setTitle("Update Album Name");
		final EditText albumInput = new EditText(SongListActivity.this);
		albumInput.setInputType(InputType.TYPE_CLASS_TEXT);
		albumInput.setHint("New Album Name");

		LinearLayout layout = new LinearLayout(SongListActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(albumInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newAlbum = albumInput.getText().toString();
				catalogDB.execSQL("UPDATE album SET title ='"+ newAlbum + "' where title='"+ album
						                  + "' and artist='"+ artist +"';");
				albumNameView.setText("Album: " + newAlbum);
				album = newAlbum;
				setTitle(album);
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

		albumInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (albumInput.getText().length() <= 0) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Album title too short!", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private  void showYearChangeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SongListActivity.this);
		builder.setTitle("Update Year");
		final EditText yearInput = new EditText(SongListActivity.this);
		yearInput.setInputType(InputType.TYPE_CLASS_TEXT);
		yearInput.setHint("New Year");

		LinearLayout layout = new LinearLayout(SongListActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(yearInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newYear = yearInput.getText().toString();
				catalogDB.execSQL("UPDATE album SET year ='"+ newYear + "' where title='"+ album
						                  + "' and artist='"+ artist +"';");
				release_year_view.setText("Year: " + newYear);
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

		yearInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (yearInput.getText().length() <= 0 ) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Year cant be blank", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(yearInput.getText().toString()) < 1000) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Not Year", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(yearInput.getText().toString()) > Calendar.getInstance().get(Calendar.YEAR)) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Year to late", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	private void populateSongList()
	{
		songlist = new ArrayList<>();

		Cursor res = catalogDB.rawQuery("Select title from song where album="+ albumID +";",null);
		res.moveToFirst();
//		num_songs_view.setText(res.getCount());

		while(!res.isAfterLast()){
			songlist.add(res.getString(res.getColumnIndex("title")));
			res.moveToNext();
		}

		num_songs_view.setText("# of tracks: " + Integer.toString(songlist.size()));
		res.close();
	}

	private void showNewSongDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(SongListActivity.this);
		builder.setTitle("New Song");

		final EditText songInput = new EditText(SongListActivity.this);
		songInput.setInputType(InputType.TYPE_CLASS_TEXT);
		songInput.setHint("Name");

		LinearLayout layout = new LinearLayout(SongListActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(songInput);

		builder.setView(layout);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String song = songInput.getText().toString();
				catalogDB.execSQL("INSERT INTO song VALUES('" + song+ "',"+ albumID +");");
				songlist.add(song);
				num_songs_view.setText("# of tracks: " + Integer.toString(songlist.size()));
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
		songInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (songInput.getText().length() <= 0 || songlist.contains(songInput.getText().toString())) {
					button.setEnabled(false);
					Toast.makeText(SongListActivity.this, "Name not long enough or already there", Toast.LENGTH_SHORT).show();
				} else {
					button.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
}
