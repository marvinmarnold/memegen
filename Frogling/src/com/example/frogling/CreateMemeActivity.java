package com.example.frogling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_share:
			Toast.makeText(getApplicationContext(), "Share option chosen!",
					Toast.LENGTH_LONG).show();
			share();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void share() {
		String url = save();

		if (url != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
			shareIntent.setType("image/jpeg");
			startActivity(Intent.createChooser(shareIntent,
					getString(R.string.menu_share)));
		} else {
			Toast.makeText(getApplicationContext(), "Url: " + url,
					Toast.LENGTH_LONG).show();
		}
	}

	private String save() {
		RelativeLayout meme = (RelativeLayout) findViewById(R.id.meme);
		// force the refresh of the view drawing cache. meme is RelativeLayout.
		meme.setDrawingCacheEnabled(false);
		meme.setDrawingCacheEnabled(true);

		Bitmap bitmap = Bitmap.createBitmap(meme.getDrawingCache());

		// MediaStore insertImage().
		MediaStore.Images.Media image = new MediaStore.Images.Media();
		return image.insertImage(getContentResolver(), bitmap, "meme",
				"first try");
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
