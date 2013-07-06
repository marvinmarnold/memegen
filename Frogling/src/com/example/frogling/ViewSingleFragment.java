/**
 * 
 */
package com.example.frogling;

import java.util.Date;

import utilities.BackEnd;
import utilities.BackEnd.PopulateQueueMode;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
		View toBeReturned = inflater.inflate(R.layout.view_single_meme,
				container, false);
		return toBeReturned;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		Button nextButton = (Button) getActivity().findViewById(
				R.id.view_next_button);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMeme(PopulateQueueMode.BACKGROUND_NEXT);
			}
		});
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem upload_item = menu.findItem(R.id.action_upload);
		upload_item.setVisible(false);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_share:
			return true;
		case R.id.action_refresh:
			if (!((MainActivity) getActivity()).hasConnection()) {
				Toast.makeText(getActivity(), "No internet connection found.",
						Toast.LENGTH_LONG).show();
				return false;
			}
			showMeme(PopulateQueueMode.REFRESH);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/**
	 * Shows the meme from BackEnd.getCurrentMeme() in the current activity
	 * view.
	 */
	public void showMeme(PopulateQueueMode populateMode) {
		if (!((MainActivity) getActivity()).hasConnection()) {
			Toast.makeText(getActivity(), "No internet connection found.",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		Object[] shownMeme = BackEnd.getNextMeme(populateMode);
		if (shownMeme != null && shownMeme.length > 0) {
			ImageView viewedImage = (ImageView) getActivity().findViewById(
					R.id.view_meme_image);
			TextView viewedTopText = (TextView) getActivity().findViewById(
					R.id.view_top_text);
			TextView viewedBottomText = (TextView) getActivity().findViewById(
					R.id.view_bottom_text);
			TextView viewedHashtag = (TextView) getActivity().findViewById(
					R.id.hashtag);
			TextView createdTime = (TextView) getActivity().findViewById(
					R.id.time_stamp);

			Bitmap map = (Bitmap) shownMeme[0];
			String topText = (String) shownMeme[1];
			String bottomText = (String) shownMeme[2];
			String hashtagText = (String) shownMeme[3];
			Date uploadDate = (Date) shownMeme[4];

			// Toast.makeText(getActivity(), unixToDate(timeInMilli),
			// Toast.LENGTH_LONG).show();

			viewedImage.setImageBitmap(map);
			viewedTopText.setText(topText);
			viewedBottomText.setText(bottomText);
			viewedHashtag.setText(hashtagText);
			createdTime.setText(uploadDate.toString());

		}
	}

}
