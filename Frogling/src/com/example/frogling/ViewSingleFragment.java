/**
 * 
 */
package com.example.frogling;

import utilities.BackEnd;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;

/**
 * @author tal11
 * 
 */
public class ViewSingleFragment extends Fragment {

	protected Activity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		return inflater
				.inflate(R.layout.view_single_meme, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button saveButton = (Button) getActivity()
				.findViewById(R.id.save_parse);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveMeme();
			}
		});

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// MenuItem upload_item = menu.findItem(R.id.action_upload);
		// upload_item.setVisible(false);
	}

	public void saveMeme() {

		ImageView image = (ImageView) getActivity().findViewById(
				R.id.view_meme_image);
		TextView topText = (TextView) getActivity().findViewById(
				R.id.view_top_text);
		TextView bottomText = (TextView) getActivity().findViewById(
				R.id.view_bottom_text);

		// refresh cache.
		image.setDrawingCacheEnabled(false);
		image.setDrawingCacheEnabled(true);
		// generate image Bitmap
		Bitmap memeMap = Bitmap.createBitmap(image.getDrawingCache());
		BackEnd.saveToParse(memeMap, topText.getText().toString(), bottomText
				.getText().toString());
	}
}
