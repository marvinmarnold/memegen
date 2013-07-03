package com.example.frogling;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.parse.Parse;

import utilities.BackEnd;

import android.app.Activity;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateMemeFragment extends Fragment {

	public EditText resultTop;
	public TextView showTop;
	public EditText resultBottom;
	public TextView showBottom;
	private static final int SELECT_PICTURE = 1;
	protected Activity parentActivity;
	public EditText hashtagEdit;
	public String hashtag;
	public Button postButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		return inflater
				.inflate(R.layout.activity_create_meme, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		parentActivity = getActivity();
		resultTop = (EditText) getActivity().findViewById(R.id.top_text_edit);
		resultBottom = (EditText) getActivity().findViewById(
				R.id.bottom_text_edit);
		hashtagEdit = (EditText) getActivity().findViewById(R.id.hashtag_edit);

		resultTop.addTextChangedListener(filterTopWatcher);
		resultBottom.addTextChangedListener(filterBottomWatcher);
		hashtagEdit.addTextChangedListener(filterHashTagWatcher);

		showTop = (TextView) getActivity().findViewById(R.id.top_text);
		showBottom = (TextView) getActivity().findViewById(R.id.bottom_text);

		postButton = (Button) getActivity().findViewById(R.id.post_button);
		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				post();
			}
		});
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// MenuItem upload_item = menu.findItem(R.id.action_upload);
		// upload_item.setVisible(false);
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
			Toast.makeText(parentActivity.getApplicationContext(),
					"picking an image from gallery", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null && data.getData() != null) {
			Toast.makeText(parentActivity.getApplicationContext(),
					"onActivityResult()", Toast.LENGTH_LONG).show();

			if (requestCode == SELECT_PICTURE) {

				Toast.makeText(parentActivity.getApplicationContext(),
						"select pic()", Toast.LENGTH_LONG).show();

				Uri selectedImageUri = data.getData();
				InputStream imageStream;
				try {
					imageStream = parentActivity.getContentResolver()
							.openInputStream(selectedImageUri);

					Bitmap bmp = BitmapFactory.decodeStream(imageStream);
					// Convert to bitmap.
					// Replace the image id with bitmap.

					ImageView image = (ImageView) getActivity().findViewById(
							R.id.meme_image);

					image.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void share() {
		if (!isHashtagValid()) {
			return;
		}

		String url = save();

		if (url != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
			shareIntent.setType("image/jpeg");
			startActivity(Intent.createChooser(shareIntent,
					getString(R.string.menu_share)));
		} else {
			Toast.makeText(parentActivity.getApplicationContext(),
					"Url: " + url, Toast.LENGTH_LONG).show();
		}
	}

	private String save() {
		RelativeLayout meme = (RelativeLayout) getActivity().findViewById(
				R.id.actual_frogling);

		// force the refresh of the view drawing cache. meme is RelativeLayout.
		meme.setDrawingCacheEnabled(false);
		meme.setDrawingCacheEnabled(true);

		Bitmap bitmap = Bitmap.createBitmap(meme.getDrawingCache());

		// MediaStore insertImage().
		return Media.insertImage(parentActivity.getContentResolver(), bitmap,
				showTop.getText().toString(), showBottom.getText().toString());
	}

	/**
	 * Saves the meme on screen as a ParseObject, uses the
	 * Bakcend.saveToParse(Bitmap, String, String) method.
	 */
	public void post() {
		if (!isHashtagValid()) {
			return;
		}

		ImageView image = (ImageView) getActivity().findViewById(
				R.id.meme_image);

		// refresh cache.
		image.setDrawingCacheEnabled(false);
		image.setDrawingCacheEnabled(true);
		// generate image Bitmap
		Bitmap memeMap = Bitmap.createBitmap(image.getDrawingCache());
		BackEnd.saveToParse(memeMap, showTop.getText().toString(), showBottom
				.getText().toString(), hashtag);
	}

	/**
	 * A method for checking the validity of the inserted #hashtag. The user
	 * MUST insert a #hashtag when adding a photo, starting with "#" and
	 * containing no spaces.
	 */
	public boolean isHashtagValid() {
		parentActivity = getActivity();

		if (hashtag == null) {
			Toast.makeText(parentActivity.getApplicationContext(),
					"You should insert a hashtag", Toast.LENGTH_LONG).show();
			return false;
		}
		if (hashtag.isEmpty()) {
			Toast.makeText(parentActivity.getApplicationContext(),
					"You should insert a hashtag", Toast.LENGTH_LONG).show();
			return false;

		}

		while (hashtag.charAt(hashtag.length() - 1) == ' ') {
			hashtag = hashtag.substring(0, hashtag.length() - 1);
		}

		if (!hashtag.startsWith("#")) {
			Toast.makeText(parentActivity.getApplicationContext(),
					"Invalid hashtag: Does not start with #.",
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (hashtag.contains(" ")) {
			Toast.makeText(parentActivity.getApplicationContext(),
					"Invalid hashtag: Contains spaces.", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return true;
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

	private TextWatcher filterBottomWatcher = new TextWatcher() {

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

	private TextWatcher filterHashTagWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
			hashtag = hashtagEdit.getText().toString();

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
