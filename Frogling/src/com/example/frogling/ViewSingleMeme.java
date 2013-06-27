/**
 * 
 */
package com.example.frogling;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

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
	
	public void saveMeme(){
		RelativeLayout memeSaved = (RelativeLayout) findViewById(R.id.meme_viewed);
		ParseObject memeSaver = new ParseObject("Meme");
		
		memeSaved.setDrawingCacheEnabled(false);
		memeSaved.setDrawingCacheEnabled(true);
		
		Bitmap memeMap = Bitmap.createBitmap(memeSaved.getDrawingCache());
		ParseFile file = new ParseFile("image.png", convertBitToByte(memeMap));
		file.saveInBackground();
		memeSaver.put("image", file);	
		try {
			memeSaver.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] convertBitToByte(Bitmap memeMap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		memeMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}
