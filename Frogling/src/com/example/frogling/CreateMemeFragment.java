package com.example.frogling;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.parse.Parse;

import utilities.BackEnd;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateMemeFragment extends Activity {

	public EditText resultTop;
	public TextView showTop;
	public EditText resultBottom;
	public TextView showBottom;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "AyjtKEZBNR17lzzKJ5LBAQrnb86hNFUNe6eAJ55T",
				"9kpSKrmsdyCXfu7Q68nEpRe9qLBOUTNsdeZFeQqV");
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

			share();
			return true;
		case R.id.action_upload:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					SELECT_PICTURE);
			return true;
		case R.id.action_browse:
			browse();
			return true;
		case R.id.action_save_to_parse:
			post();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImageUri);

					Bitmap bmp = BitmapFactory.decodeStream(imageStream);
					// Convert to bitmap.
					// Replace the image id with bitmap.

					ImageView image = (ImageView) findViewById(R.id.meme_image);

					image.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void browse() {
		Intent viewNoteActivityIntent = new Intent(CreateMemeFragment.this,
				ViewSingleFragment.class);
		startActivity(viewNoteActivityIntent);
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
		return Media.insertImage(getContentResolver(), bitmap, showTop.getText().toString(),
				showBottom.getText().toString());
	}
	
	/**
	 * Saves the meme on screen as a ParseObject, uses the
	 * Bakcend.saveToParse(Bitmap, String, String) method.
	 */
	public void post() {
		ImageView image = (ImageView) findViewById(R.id.meme_image);
		Log.d("post()", (image==null)+ "image view is");
		// refresh cache.
		image.setDrawingCacheEnabled(false);
		image.setDrawingCacheEnabled(true);
		// generate image Bitmap
		Bitmap memeMap = Bitmap.createBitmap(image.getDrawingCache());
		BackEnd.saveToParse(memeMap, showTop.getText().toString(), showBottom
				.getText().toString());
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
