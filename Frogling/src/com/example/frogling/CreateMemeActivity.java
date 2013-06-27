package com.example.frogling;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class CreateMemeActivity extends Activity {

	public EditText resultTop;
	public TextView showTop;
	public EditText resultBottom;
	public TextView showBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_meme);
		resultTop = (EditText) findViewById(R.id.top_text_edit);
		resultBottom = (EditText) findViewById(R.id.bottom_text_edit);

		resultTop.addTextChangedListener(filterTopWatcher);
		resultBottom.addTextChangedListener(filterButtomWatcher);

		showTop = (TextView) findViewById(R.id.top_text);
		showBottom = (TextView) findViewById(R.id.bottom_text);

	}

	private TextWatcher filterTopWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
			// Do your stuff
			String text;
			text = resultTop.getText().toString();
			if (text != null) {
				showTop.setText(text);
			} else {
				showTop.setText("You Have Not type anything");
			}

		}
		

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do your stuff
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// do your stuff

		}

	};
	
	private TextWatcher filterButtomWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
			// Do your stuff
			String text;
			text = resultBottom.getText().toString();
			if (text != null) {
				showBottom.setText(text);
			} else {
				showBottom.setText("You Have Not type anything");
			}

		}
		

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do your stuff
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// do your stuff

		}

	};

}
