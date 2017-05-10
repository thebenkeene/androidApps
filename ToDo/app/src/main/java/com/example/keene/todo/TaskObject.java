package com.example.keene.todo;

import java.util.ArrayList;

/**
 * Created by Ben Keene on 3/21/2017.
 */

public class TaskObject
{
	private String name;
	private String description;
	private ArrayList<TaskObject> subtaskList;

	public TaskObject(String name, String description)
	{
		this.name = name;
		this.description = description;
		subtaskList = new ArrayList<>();
	}

	public void addSubtask(String name, String description)
	{
		subtaskList.add(new TaskObject(name, description));
	}

	public void editTask(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public void removeSubtask(int id)
	{
		subtaskList.remove(id);
	}

	public void modifySubtask(String name, String description, int id)
	{
		subtaskList.get(id).editTask(name, description);
	}

	public String getName()
	{
		return name;
	}
}
