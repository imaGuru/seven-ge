package com.engine.sevenge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MenuActivity extends Activity
{
	ImageButton imageButton;

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

		imageButton = (ImageButton) findViewById(R.id.githubButton);

		imageButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{

				// Toast.makeText(MenuActivity.this,
				// "ImageButton is clicked!", Toast.LENGTH_SHORT).show();

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://github.com/SeventhSon/seven-ge"));
				startActivity(browserIntent);

			}

		});

	}

}
