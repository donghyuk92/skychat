package itm.capstone.skychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.androidhive.webgroupchat.R;

public class Activity_Name extends AppCompatActivity {

	private Button btnJoin;
	private EditText txtName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name);

		btnJoin = (Button) findViewById(R.id.btnJoin);
		txtName = (EditText) findViewById(R.id.name);

		// Hiding the action bar
		getSupportActionBar().hide();

		btnJoin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txtName.getText().toString().trim().length() > 0) {

					String name = txtName.getText().toString().trim();

					Intent intent = new Intent(Activity_Name.this,
							Activity_Main.class);
					intent.putExtra("name", name);

					startActivity(intent);

				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter your name", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
