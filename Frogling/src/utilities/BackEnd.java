package utilities;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Media;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.frogling.R;
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
	public static final int PAGE_SIZE = 6;
	public static final int TAG_AMOUNT = 5;
	public static final int MAX_STACK_SIZE = 2;
	static int imageWidth = 250;
	static int imageHeight = 250;
	private static int pageIndex = 0;
	/* Currently pending background requests in progress. */
	private static ArrayList<ParseQuery<ParseObject>> currentParseQueries = new ArrayList<ParseQuery<ParseObject>>();
	private static ArrayList<ParseFile> currentParseFileRequests = new ArrayList<ParseFile>();
	private static Queue<Object[]> memeQueue = new LinkedList<Object[]>();

	public static enum PopulateQueueMode {
		REFRESH, // Refresh lazily when the pending background fetch completes.
		BACKGROUND_NEXT
	}

	public static Object[] getNextMeme(PopulateQueueMode populateMode) {
		if (populateMode == PopulateQueueMode.REFRESH) {
			populateQueueWithMemes(PopulateQueueMode.REFRESH);
		} else if (populateMode == PopulateQueueMode.BACKGROUND_NEXT
				&& memeQueue.size() < PAGE_SIZE / 2) {
			populateQueueWithMemes(PopulateQueueMode.BACKGROUND_NEXT);
		}

		Object[] parsedMeme = memeQueue.poll();
		return parsedMeme;
	}

	public static void populateQueueWithMemes(
			final PopulateQueueMode populateMode) {
		// If the user hits refresh, we should clear the queue and cancel
		// any pending background requests and get the latest batch of memes.
		if (populateMode == PopulateQueueMode.REFRESH) {

			if (currentParseQueries.size() > 0) {
				// Cancel any previous requests.
				for (ParseQuery<ParseObject> currentQuery : currentParseQueries) {
					currentQuery.cancel();
				}

				for (ParseFile parseFileRequest : currentParseFileRequests) {
					parseFileRequest.cancel();
				}
				currentParseFileRequests.clear();
				currentParseQueries.clear();
			}

			memeQueue.clear();
			pageIndex = 0;
		} else if (populateMode == PopulateQueueMode.BACKGROUND_NEXT) {
			// User hit "next" but there already is a background fetch in
			// progress.
			if (currentParseQueries.size() > 0) {
				return;
			}
		}

		final ParseQuery<ParseObject> currentQuery = ParseQuery
				.getQuery(PARSE_KEY);
		currentQuery.orderByDescending("createdAt");
		currentQuery.setLimit(PAGE_SIZE);
		currentQuery.setSkip(PAGE_SIZE * pageIndex);
		currentParseQueries.add(currentQuery);

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
				// CAVEAT: It is possible for the query to have reached here
				// but it is not actually done i.e. the ParseFile data fetch
				// has not completed yet. If the memeQueue size is < PAGE_SIZE /
				// 2 and the user hit next before the ParseFile data fetch
				// completed then a new ParseQuery will be initiated. In this
				// case, the size of
				// the memeQueue can exceed PAGE_SIZE.
				currentParseQueries.remove(currentQuery);
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
			// fetch of the top and bottom text often happens before the bitmap
			// itself which is a problem. Perhaps show the spinner??
			// memeQueue.add(memeQueueObject);

		}
	}

	private static void getImageBitmapFromParseFileInBackground(
			ParseObject toParse, final Object[] memeQueueObject) {
		final ParseFile getImageDataFromFile = (ParseFile) toParse
				.get(IMAGE_KEY);
		currentParseFileRequests.add(getImageDataFromFile);

		getImageDataFromFile.getDataInBackground(new GetDataCallback() {
			public void done(byte[] data, ParseException e) {
				if (e == null) {
					memeQueueObject[0] = convertByteToBit((byte[]) data);
					memeQueue.add(memeQueueObject);
					currentParseFileRequests.remove(getImageDataFromFile);
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
	public static void saveToParse(final Context context, Bitmap imageBitmap, String topText,
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
		saveMeme.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(context, "Saved successfully!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, "Error. Check your internet connection.",
							Toast.LENGTH_LONG).show();
				}
			}
		});
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
	
	// Saves the meme to SD card and returns the URL.
	public static String saveToSdCard(Context context, RelativeLayout meme) {
		// force the refresh of the view drawing cache. meme is RelativeLayout.
		meme.setDrawingCacheEnabled(false);
		meme.setDrawingCacheEnabled(true);

		Bitmap bitmap = Bitmap.createBitmap(meme.getDrawingCache());

		// MediaStore insertImage().
		return Media.insertImage(context.getContentResolver(), bitmap,
				"Frogling", "AUTOGENERATED IMAGE FROM APPLICATIPON FROLING");
	}

}