package utilities;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public abstract class BackEnd {

	/*
	 * Constants to use as keys for saving into Parse, no other keys should be
	 * used.
	 */
	public static final String PARSE_KEY = "Meme";
	public static final String IMAGE_KEY = "IMAGE_BITMAP";
	public static final String TOP_TEXT = "TOP_TEXT";
	public static final String BOTTOM_TEXT = "BOTTOM_TEXT";
	public static final String TIME_STAMP = "TS";
	public static final String IMAGE_FILE = "IMAGE_FILE.png";

	/* ParseQuery to load memes from, needs to be initialized */
	private static ParseQuery<ParseObject> currentQuery = null;
	private static int currentIndex = 0; // The index of the meme to return.
	private static int totalFroglings = -1; // Number of Froglings stored in
											// Parse backend.

	/**
	 * Saves a meme to the Parse cloud.
	 * 
	 * @param imageBitmap
	 *            - The bitmap object of the image that the meme has.
	 * @param topText
	 *            - the caption on the top of the image, can be empty.
	 * @param bottomText
	 *            - the caption on the bottom of the image, can be empty.
	 * 
	 *            Will first create a ParseFile containing a byte[]
	 *            representation of an image: ParseFile image_file: key: object:
	 *            "IMAGE_FILE" byte[](imageBitmap)
	 * 
	 *            Will save the image in the Parse cloud in the following format
	 *            keys: ParseObject class: "Meme" key: object: "IMAGE_BITMAP"
	 *            image_file (ParseFile previously created.) "TOP_TEXT" topText
	 *            "BOTTOM_TEXT" bottom_text "TS" System.currentTimeMillis() -
	 *            current time in milliseconds.
	 * 
	 *            Will save the image in the background, not interrupting the
	 *            flow.
	 */
	public static void saveToParse(Bitmap imageBitmap, String topText,
			String bottomText) {
		//Create ParseObject:
		ParseObject saveMeme = new ParseObject(PARSE_KEY);
		//Save imageBitmap as a ParseFile to use in the ParseObject:
		ParseFile parseImage = new ParseFile(IMAGE_FILE,
				BackEnd.convertBitToByte(imageBitmap));
		parseImage.saveInBackground();
		
		//Saves all values of the meme:
		saveMeme.put(IMAGE_KEY, parseImage);
		saveMeme.put(TOP_TEXT, topText);
		saveMeme.put(BOTTOM_TEXT, bottomText);
		saveMeme.put(TIME_STAMP, System.currentTimeMillis());
		saveMeme.saveInBackground();
	}

	/**
	 * Converts a given Bitmap into a byte[] by using a stream buffer,
	 * 
	 * @param memeMap
	 *            - Bitmap to be converted.
	 * @return byte[] which represents the image.
	 */
	public static byte[] convertBitToByte(Bitmap memeMap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		memeMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	/**
	 * Converts a given byte[] into its Bitmap representation. Precondition:
	 * savedImage is not null.
	 * 
	 * @param savedImage
	 *            - Bitmap to be converted.
	 * @return a Bitmap representing the given byte[]
	 */
	public static Bitmap convertByteToBit(byte[] savedImage) {
		Bitmap retVal = BitmapFactory.decodeByteArray(savedImage, 0, savedImage.length);
		return retVal;
	}

	/**
	 * Gets the next meme from the query, then increases current (static field)
	 * by 1. If current exceeded the query size, re-initializes the query and
	 * resets current to 0.
	 * 
	 * @return the next Meme, represented by a ParseObject object, or null, if
	 *         ParseQuery not initialized or is empty.
	 */
	public static ParseObject getNextMeme() {
		//Get query:
		currentQuery = ParseQuery.getQuery(PARSE_KEY);
		try {
			//gets query size:
			totalFroglings = currentQuery.count();
			Log.d("getNextMeme()", totalFroglings+ " objects in query");
			if (totalFroglings <= 0) {
				return null;
			}
			//Orders query and gets current meme:
			currentQuery.addDescendingOrder(TIME_STAMP);
			currentQuery.setSkip(currentIndex);
			ParseObject nextMeme = currentQuery.getFirst();
			
			//Updates index:
			currentIndex++;
			//Resets index if neccassry:
			if (currentIndex >= totalFroglings) {
				currentIndex = 0;
			}
			return nextMeme;

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//if had errors, will return null
		return null;
	}
}
