package utilities;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

import javax.security.auth.callback.Callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.FindCallback;
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
	public static final String IMAGE_FILE = "IMAGE_FILE.png";
	public static final String HASH_TAG = "HASH_TAG";
	public static final int PAGE_SIZE = 6;
	public static final int TAG_AMOUNT = 5;
	public static final int MAX_STACK_SIZE = 2;
	static int imageWidth = 250;
	static int imageHeight = 250;

	public static enum PopulateQueueMode {
		BLOCKING_REFRESH, BLOCKING_NEXT, BACKGROUND_NEXT
	}

	private static int pageIndex = 0;
	/* ParseQuery to load memes from, needs to be initialized */
	private static ParseQuery<ParseObject> currentQuery = null;
	private static Object[] currentlyViewedMeme = null; // The meme object to be
														// requested from the
														// project.
	private static Queue<ParseObject> memeQueue = new LinkedList<ParseObject>();

	public static Object[] getNextMeme() {
		Log.d("Inside getNextMeme", pageIndex + " is the pageIndex");
		Log.d("getNextMeme ", "found items, in queue: " + memeQueue.size());

		ParseObject memeParseObject = memeQueue.poll();
		if (memeQueue.size() < PAGE_SIZE / 2) {
			Log.d("getNextmeme()", "calling populateQueue()");
			populateQueueWithMemes(PopulateQueueMode.BACKGROUND_NEXT);
		}
		Object[] retVal = new Object[TAG_AMOUNT];
		if (memeParseObject != null) {
			try {
				retVal[0] = ((ParseFile) memeParseObject.get(IMAGE_KEY))
						.getData();
				retVal[1] = memeParseObject.get(TOP_TEXT);
				retVal[2] = memeParseObject.get(BOTTOM_TEXT);
				retVal[3] = memeParseObject.get(HASH_TAG);
				retVal[4] = memeParseObject.getCreatedAt();
				Log.d("ParsingObject", "Time:" + retVal[4]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			populateQueueWithMemes(PopulateQueueMode.BLOCKING_NEXT);
		}

		Log.d("getNextMeme ", "After return found items, in queue: "
				+ memeQueue.size());
		return retVal;
	}

	public static void populateQueueWithMemes(PopulateQueueMode populateMode) {
		currentQuery = ParseQuery.getQuery(PARSE_KEY);

		Log.d("Inside populateQueueWithMemes", pageIndex + " is the pageIndex");
		currentQuery.orderByDescending("createdAt");
		currentQuery.setLimit(PAGE_SIZE);

		switch (populateMode) {
		case BLOCKING_REFRESH:
			pageIndex = 0;
			currentQuery.setSkip(pageIndex * PAGE_SIZE);
			try {
				List<ParseObject> list = currentQuery.find();
				if (list != null && list.size() > 0) {
					memeQueue.clear();
					memeQueue.addAll(list);
					pageIndex++;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case BACKGROUND_NEXT:
			currentQuery.setSkip(pageIndex * PAGE_SIZE);
			currentQuery.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					if (objects != null && objects.size() > 0) {
						memeQueue.addAll(objects);
						pageIndex++;
						Log.d("QueryDone", "found items, in queue: "
								+ memeQueue.size());
						Log.d("IndexUpdated", pageIndex + "is the pageIndex");
					} else {
						Log.d("QueueDone", "Reached the end of meme stream.");
						pageIndex = 0;
					}
				}
			});
			break;
		}
	}

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
		if (savedImage == null) {
			return null;
		}

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
	public static void initializeFroglingBrowser(boolean isRefresh) {
		Log.d("initializeFroglingBrowser()", "calling populateQueue()");
		if (isRefresh) {
			populateQueueWithMemes(PopulateQueueMode.BLOCKING_REFRESH);
		} else {
			populateQueueWithMemes(PopulateQueueMode.BACKGROUND_NEXT);
		}
	}

	/**
	 * Converts time in millis into String form of that Date.
	 * 
	 * @param timestamp_in_mill
	 *            - time stamp in millis: Sytstem.getCurrentTimeMillis()
	 *            construct.
	 * @return a String representing the date of the given time millis. (0 --> 1
	 *         Jan 1970)
	 */
	public static String unixToDate(long timestamp_in_mill) {
		Date date = new Date(timestamp_in_mill);
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy , HH:mm");
		df.setTimeZone(TimeZone.getDefault());
		String dateStr = df.format(date);
		return dateStr;
	}

}