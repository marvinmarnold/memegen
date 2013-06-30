package com.example.frogling;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

		resultTop.addTextChangedListener(filterTopWatcher);
		resultBottom.addTextChangedListener(filterButtomWatcher);

		showTop = (TextView) getActivity().findViewById(R.id.top_text);
		showBottom = (TextView) getActivity().findViewById(R.id.bottom_text);

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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 1 && data != null && data.getData() != null) {
			if (requestCode == SELECT_PICTURE) {
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
				R.id.meme);
		// force the refresh of the view drawing cache. meme is RelativeLayout.
		meme.setDrawingCacheEnabled(false);
		meme.setDrawingCacheEnabled(true);

		Bitmap bitmap = Bitmap.createBitmap(meme.getDrawingCache());

		// MediaStore insertImage().
		MediaStore.Images.Media image = new MediaStore.Images.Media();
		return image.insertImage(parentActivity.getContentResolver(), bitmap,
				"meme", "first try");
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
