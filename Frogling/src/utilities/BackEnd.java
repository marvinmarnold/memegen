package utilities;

import java.io.ByteArrayOutputStream;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.Toast;

import com.example.frogling.R;
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
	public static final String HASH_TAG = "HASH_TAG";
	private static final int TAG_AMOUNT = 4;
	static int imageWidth = 250;
	static int imageHeight = 250;

	/* ParseQuery to load memes from, needs to be initialized */
	private static ParseQuery<ParseObject> currentQuery = null;
	private static int currentIndex = -1; // The index of the meme to return.
	private static int totalFroglings = -1; // Number of Froglings stored in
											// Parse backend.
	private static Object[] currentlyViewedMeme = null; // The meme object to be
														// requested from the
														// project.

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
			String bottomText, String hashtag) {
		// Create ParseObject:
		ParseObject saveMeme = new ParseObject(PARSE_KEY);
		// Save imageBitmap as a ParseFile to use in the ParseObject:
		ParseFile parseImage = new ParseFile(IMAGE_FILE,
				BackEnd.convertBitToByte(imageBitmap));
		parseImage.saveInBackground();

		// Saves all values of the meme:
		saveMeme.put(IMAGE_KEY, parseImage);
		saveMeme.put(TOP_TEXT, topText);
		saveMeme.put(BOTTOM_TEXT, bottomText);
		saveMeme.put(TIME_STAMP, System.currentTimeMillis());
		saveMeme.put(HASH_TAG, hashtag);
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
		Bitmap compressedMemeMap = resize(memeMap);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		compressedMemeMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
		Bitmap retVal = BitmapFactory.decodeByteArray(savedImage, 0,
				savedImage.length);
		return retVal;
	}

	private static Bitmap resize(Bitmap originalBitmap) {
		float factorH = imageHeight / (float) originalBitmap.getHeight();
		float factorW = imageWidth / (float) originalBitmap.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap,
				(int) (originalBitmap.getWidth() * factorToUse),
				(int) (originalBitmap.getHeight() * factorToUse), false);
		return resizedBitmap;
	}

	/**
	 * Initializes a Meme item ParseQuery into currentQuery, then counts the
	 * amount of memes in query and resets currentIndex to 0.
	 */
	public static void initializeFroglingBrowser() {
		currentQuery = ParseQuery.getQuery(PARSE_KEY);
		try {
			totalFroglings = currentQuery.count();
			Log.d("BrowserInit)", totalFroglings + " objects in query");
			currentQuery.addDescendingOrder(TIME_STAMP);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentIndex = 0;
		currentlyViewedMeme = getNextMeme();
	}

	/**
	 * Gets the next meme from the query, then increases current (static field)
	 * by 1. If current exceeded the query size, re-initializes the query and
	 * resets current to 0.
	 * 
	 * @return the next Meme, represented by a Object[] object, or null, if
	 *         ParseQuery not initialized or is empty. Object[] structure:
	 *         Object[0] - Image Bitmap data. Object[1] - Top text Object[2] -
	 *         Bottom text Object[3] - Hashtag.
	 * 
	 */
	public static Object[] getNextMeme() {
		try {
			currentQuery.setSkip(currentIndex);
			ParseObject nextMeme = currentQuery.getFirst();
			currentIndex++;
			if (currentIndex >= totalFroglings) {
				currentIndex = 0;
			}
			Object[] retVal = new Object[TAG_AMOUNT];

			retVal[0] = ((ParseFile) nextMeme.get(IMAGE_KEY)).getData();
			retVal[1] = nextMeme.get(TOP_TEXT);
			retVal[2] = nextMeme.get(BOTTOM_TEXT);
			retVal[3] = nextMeme.get(HASH_TAG);
			
			currentlyViewedMeme = retVal;
			return retVal;

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// if had errors, will return null
		return null;
	}
	
	/**
	 * Getter for currentlyViewedMeme.
	 * @return the currentlyViewedMeme field.
	 */
	public static Object[] getCurrentMeme(){
		return currentlyViewedMeme;
	}
	
	
}