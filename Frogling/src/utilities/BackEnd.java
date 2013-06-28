package utilities;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

import com.parse.ParseFile;
import com.parse.ParseObject;

public abstract class BackEnd {
	
	/*
	 * Constants to use as keys for saving into Parse, no other keys should be used.
	 */
	public static final String IMAGE_KEY = "IMAGE_BITMAP";
	public static final String TOP_TEXT = "TOP_TEXT";
	public static final String BOTTOM_TEXT = "BOTTOM_TEXT";
	public static final String TIME_STAMP = "TS";
	private static final String IMAGE_FILE = "IMAGE_FILE";
	
	/**
	 * Saves a meme to the Parse cloud.
	 * @param imageBitmap - The bitmap object of the image that the meme has.
	 * @param topText - the caption on the top of the image, can be empty.
	 * @param bottomText - the caption on the bottom of the image, can be empty.
	 * 
	 * Will first create a ParseFile containing a byte[] representation of an image:
	 * 	ParseFile image_file:
	 * 		key:				object:
	 * 			"IMAGE_FILE"		byte[](imageBitmap)
	 * 
	 * Will save the image in the Parse cloud in the following format keys:
	 * 	ParseObject class: "Meme"
	 * 			key:				object:
	 * 				"IMAGE_BITMAP"		image_file (ParseFile previously created.)
	 * 				"TOP_TEXT"			topText
	 * 				"BOTTOM_TEXT"		bottom_text
	 * 				"TS"				System.currentTimeMillis() - current time in milliseconds.
	 * 
	 * Will save the image in the background, not interrupting the flow.
	 */
	public static void saveToParse(Bitmap imageBitmap, String topText, String bottomText){
		ParseObject saveMeme = new ParseObject("Meme");
		ParseFile parseImage = new ParseFile(IMAGE_FILE, BackEnd.convertBitToByte(imageBitmap));
		parseImage.saveInBackground();
		
		saveMeme.put(IMAGE_KEY, parseImage);
		saveMeme.put(TOP_TEXT, topText);
		saveMeme.put(BOTTOM_TEXT, bottomText);
		saveMeme.put(TIME_STAMP, System.currentTimeMillis());
		saveMeme.saveInBackground();
	}
	
	/**
	 * Converts a given Bitmap into a byte[] by using a stream buffer,
	 * @param memeMap - Bitmap to be converted.
	 * @return byte[] which represents the image. 
	 */
	public static byte[] convertBitToByte(Bitmap memeMap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		memeMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}
