package com.example.frogling;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 
 */

/**
 * @author Elishuv
 *
 */
public class BrowseMeme extends Activity {
	
//	private int imageIdCounter = R.drawable.firstmeme;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "Q44SjEv06zE0OEvGnSUIZMMW3jblHzBf4h0m56EB", "g7Id0jreo83O4wJ13Vwi3QON7W8YFveLP0TTIdw9");
		setContentView(R.layout.activity_view_meme);
		ParseObject imageSaver = new ParseObject("ImageSaver");
		
		imageSaver.put("test", "firstInput");
		imageSaver.saveInBackground();
		Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ImageView displayedImage = (ImageView) findViewById(R.id.image_viewed);
				
				
			}
		});
	}
}
