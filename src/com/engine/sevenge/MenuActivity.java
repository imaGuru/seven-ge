package com.engine.sevenge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {
	ImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		final ListView listView = (ListView) findViewById(R.id.listview);
		String[] values = new String[] { "Demo 1", "Demo 2" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.listview_custom, values);

		listView.setAdapter(adapter);

		imageButton = (ImageButton) findViewById(R.id.githubButton);

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://github.com/SeventhSon/seven-ge"));
				startActivity(browserIntent);

			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> aV, View view, int position,
					long id) {
				if (position > 0) {

					String item = ((TextView) view).getText().toString();
					Toast.makeText(getBaseContext(),
							item + " not yet implemented", Toast.LENGTH_LONG)
							.show();
				} else {
					Intent i = new Intent(getApplicationContext(),
							GameActivity.class);
					i.putExtra("demo", position + 1);
					startActivity(i);
				}
			}

		});

	}

}
