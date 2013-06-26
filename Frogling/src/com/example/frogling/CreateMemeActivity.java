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
	public EditText resultButtom;
	public TextView showButtom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_meme);
		resultTop = (EditText) findViewById(R.id.top_text_edit);
		resultButtom = (EditText) findViewById(R.id.buttom_text_edit);

		resultTop.addTextChangedListener(filterTopWatcher);
		resultButtom.addTextChangedListener(filterButtomWatcher);

		showTop = (TextView) findViewById(R.id.top_text);
		showButtom = (TextView) findViewById(R.id.buttom_text);

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
			text = resultButtom.getText().toString();
			if (text != null) {
				showButtom.setText(text);
			} else {
				showButtom.setText("You Have Not type anything");
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
