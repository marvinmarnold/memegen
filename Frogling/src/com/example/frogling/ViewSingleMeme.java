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

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;

/**
 * @author tal11
 * 
 */
public class ViewSingleMeme extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_single_meme);
		Parse.initialize(this, "AyjtKEZBNR17lzzKJ5LBAQrnb86hNFUNe6eAJ55T",
				"9kpSKrmsdyCXfu7Q68nEpRe9qLBOUTNsdeZFeQqV");
		ParseAnalytics.trackAppOpened(getIntent());

		Button saveButton = (Button) findViewById(R.id.save_parse);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveMeme();
			}
		});
	}

	public void saveMeme() {
		RelativeLayout memeSaved = (RelativeLayout) findViewById(R.id.meme_viewed);
		ImageView image = (ImageView)findViewById(R.id.image_viewed);
		TextView topText = (TextView) findViewById(R.id.view_top_text);
		TextView bottomText = (TextView) findViewById(R.id.view_bottom_text);
		
		//refresh cache.
		memeSaved.setDrawingCacheEnabled(false);
		memeSaved.setDrawingCacheEnabled(true);
		//generate image Bitmap
		Bitmap memeMap = Bitmap.createBitmap(memeSaved.getDrawingCache());
		BackEnd.saveToParse(memeMap, topText.getText().toString(), bottomText.getText().toString());
	}
}
