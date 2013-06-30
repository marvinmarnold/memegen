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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
		
		Button nextButton = (Button) getActivity().findViewById(R.id.view_next_button);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showNextMeme();
			}
		});
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// MenuItem upload_item = menu.findItem(R.id.action_upload);
		// upload_item.setVisible(false);
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
			try {
				Bitmap map = BackEnd.convertByteToBit(((ParseFile) object
						.get(BackEnd.IMAGE_KEY)).getData());
				String topText = (String) object.get(BackEnd.TOP_TEXT);
				String bottomText = (String) object.get(BackEnd.BOTTOM_TEXT);

				viewedImage.setImageBitmap(map);
				viewedTopText.setText(topText);
				viewedBottomText.setText(bottomText);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

		}
	}
}
