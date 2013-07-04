package utilities;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
	public static final int PAGE_SIZE = 10;
	public static final int TAG_AMOUNT = 5;
	public static final int MAX_STACK_SIZE = 2;
	static int imageWidth = 250;
	static int imageHeight = 250;
	private static boolean backgroundFetchInProgress = false;
	private static boolean refreshRequestMade = false;

	public static enum PopulateQueueMode {
		REFRESH,  // Refresh lazily when the pending background fetch completes.
		BACKGROUND_NEXT
	}

	private static int pageIndex = 0;
	/* ParseQuery to load memes from, needs to be initialized */
	private static ParseQuery<ParseObject> currentQuery = null;
	private static Queue<Object[]> memeQueue = new LinkedList<Object[]>();

	public static Object[] getNextMeme() {

		Object[] parsedMeme = memeQueue.poll();
		if (memeQueue.size() < PAGE_SIZE / 2) {
//			Log.d("getNextmeme()", "calling populateQueue()");
			populateQueueWithMemes(PopulateQueueMode.BACKGROUND_NEXT);
		}
		
		if (refreshRequestMade) {
//			Log.d("getNextmeme()", "calling pop queue() as refresh req was made");
			populateQueueWithMemes(PopulateQueueMode.REFRESH);
		}

//		Log.d("getNextMeme ", "After return found items, in queue: "
//				+ memeQueue.size());
		return parsedMeme;
	}

	public static void populateQueueWithMemes(
			final PopulateQueueMode populateMode) {
//		Log.d("Inside populateQueueWithMemes", pageIndex + " is the pageIndex");

		// TODO: If the user hit refresh, we should still get the list of new memes.
		if (backgroundFetchInProgress) {
			if (populateMode == PopulateQueueMode.REFRESH) {
//				Log.d("Inside populateQueueWithMemes", "Refresh req made but pending request");
				refreshRequestMade = true;
			}
			return;
		}

		if (refreshRequestMade || populateMode == PopulateQueueMode.REFRESH) {
//			Log.d("Inside populateQueueWithMemes", "Refresh req WAS made so clearning queue.");

			refreshRequestMade = false;
			memeQueue.clear();
			pageIndex = 0;
		}
		
//		Log.d("Inside populateQueueWithMemes", "Fetching more data.");

		currentQuery = ParseQuery.getQuery(PARSE_KEY);
		currentQuery.orderByDescending("createdAt");
		currentQuery.setLimit(PAGE_SIZE);

		backgroundFetchInProgress = true;
		currentQuery.setSkip(PAGE_SIZE * pageIndex);
		currentQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objectsFromParse,
					ParseException e) {
				// TODO Auto-generated method stub
				if (objectsFromParse != null && objectsFromParse.size() > 0) {
					// Fetch the bitmap.
					addDataToQueueFromParseObjects(objectsFromParse);
					pageIndex++;

				} else {
					pageIndex = 0;
				}
				backgroundFetchInProgress = false;
			}
		});
	}

	// Takes a list of ParseObjects fetched from the backend as input and
	// adds them to memeQueue. The blocking call adds the memes in the proper
	// sorted order but the background call does not. See comments for more
	// details.
	private static void addDataToQueueFromParseObjects(
			List<ParseObject> toParseList) {
		for (ParseObject toParse : toParseList) {
			Object[] memeQueueObject = new Object[TAG_AMOUNT];
			memeQueueObject[1] = toParse.get(TOP_TEXT);
			memeQueueObject[2] = toParse.get(BOTTOM_TEXT);
			memeQueueObject[3] = toParse.get(HASH_TAG);
			memeQueueObject[4] = toParse.getCreatedAt();
			// Fetch the bitmap.

			getImageBitmapFromParseFileInBackground(toParse, memeQueueObject);
			// CAVEAT: Adding the memeQueueObject to the queue here ensures
			// that the memes are added in time sorted order. However, the
			// fetch
			// of the top and bottom text often happens before the bitmap
			// itself
			// which is a problem.
			// Show the spinner??
			// memeQueue.add(memeQueueObject);

		}
	}

	private static void getImageBitmapFromParseFileInBackground(
			ParseObject toParse, final Object[] memeQueueObject) {
		((ParseFile) (toParse.get(IMAGE_KEY)))
				.getDataInBackground(new GetDataCallback() {
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							memeQueueObject[0] = convertByteToBit((byte[]) data);
							memeQueue.add(memeQueueObject);
						} else {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * Saves a Frogling to the Parse cloud.
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
		if (isRefresh) {
			populateQueueWithMemes(PopulateQueueMode.REFRESH);
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