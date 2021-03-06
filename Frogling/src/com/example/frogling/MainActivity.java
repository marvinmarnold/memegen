package com.example.frogling;

import utilities.BackEnd;
import utilities.BackEnd.PopulateQueueMode;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
//import android.widget.Toast;

/**
 * This demonstrates the use of action bar tabs and how they interact with other
 * action bar features. Code adapted from:
 * http://developer.android.com/reference
 * /android/app/ActionBar.html#newTab%28%29
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "AyjtKEZBNR17lzzKJ5LBAQrnb86hNFUNe6eAJ55T",
				"9kpSKrmsdyCXfu7Q68nEpRe9qLBOUTNsdeZFeQqV");
		ParseAnalytics.trackAppOpened(getIntent());
		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		bar.addTab(bar
				.newTab()
				.setText("Create")
				.setTabListener(
						new TabListener<CreateMemeFragment>(this, "create",
								CreateMemeFragment.class)));
		
		// Start a background job to fetch a few memes for the explore tab.
		BackEnd.populateQueueWithMemes(PopulateQueueMode.REFRESH);
		
		bar.addTab(bar
				.newTab()
				.setText("Explore")
				.setTabListener(
						new TabListener<ViewSingleFragment>(this, "explore",
								ViewSingleFragment.class)));

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(Activity activity, String tag, Class<T> clz,
				Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(),
						mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
//			Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
		}

	}
	
	public boolean hasConnection(){
		ConnectivityManager cm = (ConnectivityManager) 
				getSystemService(this.CONNECTIVITY_SERVICE);
		Log.d("CheckConnextion", "connection status online:" +(cm.getActiveNetworkInfo() != null));
		return cm.getActiveNetworkInfo() != null;
	}
}