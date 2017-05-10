package com.example.keene.todo;

import android.app.ListFragment;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.example.metje.todo.R;
import android.widget.ArrayAdapter;
import android.widget.Toast;



import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ben Keene on 3/24/2017.
 */

public class SubtaskFragment extends ListFragment implements AdapterView.OnItemClickListener
{
	private String parent;
	private ArrayList<String> subtask;
	private SQLiteDatabase taskDB;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		parent = getArguments().getString("parent");
		return inflater.inflate(R.layout.subtask_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		taskDB.execSQL("CREATE TABLE IF NOT EXISTS task(name VARCHAR, description VARCHAR, parent VARCHAR);");

		populateSubtasks();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subtask);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
	}

	private void populateSubtasks()
	{

		Cursor res = taskDB.rawQuery("Select * from taskDB where parent='"+ parent + "'",null);
		res.moveToFirst();

		while(res.isAfterLast() == false){
			subtask.add(res.getString(res.getColumnIndex("name")));
			res.moveToNext();
		}
	}
}
