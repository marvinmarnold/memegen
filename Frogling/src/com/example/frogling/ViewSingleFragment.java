/**
 * 
 */
package com.example.frogling;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import utilities.BackEnd;
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

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

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

		return inflater.inflate(R.layout.view_single_meme, container, false);
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
				showNextMeme();
			}
		});
	}

	/**
	 * 
	 * @param unix_timestamp
	 * @return
	 * @throws ParseException
	 */

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem upload_item = menu.findItem(R.id.action_upload);
		upload_item.setVisible(false);
		MenuItem post_item = menu.findItem(R.id.action_save_to_parse);
		post_item.setVisible(false);
	}

	/**
	 * Shows the next meme from the meme query, uses the BackEnd.getNextMeme()
	 * to get memes.
	 */
	public void showNextMeme() {
		ParseObject object = BackEnd.getNextMeme();

		if (object != null) {
			ImageView viewedImage = (ImageView) getActivity().findViewById(
					R.id.view_meme_image);
			TextView viewedTopText = (TextView) getActivity().findViewById(
					R.id.view_top_text);
			TextView viewedBottomText = (TextView) getActivity().findViewById(
					R.id.view_bottom_text);
			TextView createdTime = (TextView) getActivity().findViewById(
					R.id.time_stamp);
			try {
				Bitmap map = BackEnd.convertByteToBit(((ParseFile) object
						.get(BackEnd.IMAGE_KEY)).getData());
				String topText = (String) object.get(BackEnd.TOP_TEXT);
				String bottomText = (String) object.get(BackEnd.BOTTOM_TEXT);
				long timeInMilli = (Long) object.get(BackEnd.TIME_STAMP);

				// Toast.makeText(getActivity(), unixToDate(timeInMilli),
				// Toast.LENGTH_LONG).show();

				viewedImage.setImageBitmap(map);
				viewedTopText.setText(topText);
				viewedBottomText.setText(bottomText);
				createdTime.setText(unixToDate(timeInMilli));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

		}
	}

	public String unixToDate(long timestamp_in_mill) {
		Date date = new Date(timestamp_in_mill);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		String dateStr = df.format(date);

		return dateStr;
	}
}
