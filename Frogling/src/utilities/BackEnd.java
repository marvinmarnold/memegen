package utilities;

import android.app.Activity;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;

public abstract class BackEnd {
	
	public static void saveToParse(String key, Object object){
		ParseObject saveMeme = new ParseObject(object.getClass().toString());
		saveMeme.put("test", "FirstInput");
		saveMeme.saveInBackground();
	}
}
