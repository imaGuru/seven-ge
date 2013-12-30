package com.engine.sevenge;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		final ListView listView = (ListView) findViewById(R.id.listview);
		String[] values = new String[] { "Demo 1", "Demo 2", "Demo 3",
				"Demo 4", "Demo 5" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);

		listView.setAdapter(adapter);

		

	}

}
