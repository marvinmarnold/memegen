/**
 * 
 */
package com.example.frogling;

import utilities.BackEnd;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * @author tal11
 * 
 */
public class ViewSingleFragment extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_single_meme);
		ParseAnalytics.trackAppOpened(getIntent());

		showNextMeme();
		

		Button nextButton = (Button) findViewById(R.id.view_next_button);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showNextMeme();
			}
		});
	}

	/**
	 * Shows the next meme from the meme query, uses the BackEnd.getNextMeme()
	 * to get memes.
	 */
	public void showNextMeme() {

		ParseObject object = BackEnd.getNextMeme();
		if (object != null) {
			ImageView viewedImage = (ImageView) findViewById(R.id.view_meme_image);
			TextView viewedTopText = (TextView) findViewById(R.id.view_top_text);
			TextView viewedBottomText = (TextView) findViewById(R.id.view_bottom_text);
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