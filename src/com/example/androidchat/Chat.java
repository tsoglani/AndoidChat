package com.example.androidchat;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;

public class Chat extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	private MessagesView sv;
	private SocketHandler sh;
	private SignInView signIn;
	private boolean isSignIn = false;
	public static String selectedUsernameForPrivateContact = "Public";
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private FrameLayout fl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		// //jbh
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// / mine

		sv = new MessagesView(this);
		sh = new SocketHandler();
		signIn = new SignInView(this);
		sh.setScreenView(sv);
		sh.setActivity(this);
		sv.setSocketHundler(sh);
		signIn.setSocketHandler(sh);
		fl = (FrameLayout) findViewById(R.id.container);
		fl.addView(signIn);

	}

	public void successLogIn() {
		isSignIn = true;
		sh.sendReseiveUserRequest();
		fl = (FrameLayout) findViewById(R.id.container);
		fl.removeAllViews();
		fl.addView(sv);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		// Log.e("onNavigationDrawerItemSelected chattttt",
		// "onNavigationDrawerItemSelected");
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();

		newMessageView(position);
	}

	public void newMessageView(final int position) {
		if (!isSignIn) {
			return;
		}

		selectedUsernameForPrivateContact = SocketHandler.interactiveUsers
				.get(position);
		// Log.e("position =  "+Integer.toString(position),Integer.toString(SocketHandler.list.size()));
		// Iterator iter = SocketHandler.list.keySet().iterator();
		// while (iter.hasNext()) {
		// String userName = (String) iter.next();
		// ArrayList<String> messages = SocketHandler.list.get(userName);
		// if (userName.equals(selectedUsernameForPrivateContact)) {
		// if(!userName.equals("Public")){
		// sv.getScreen().setText("Private speek With user " + userName);
		// }else{
		// sv.getScreen().setText("Public Speak");
		// }
		//
		//
		// }
		// }

		if (selectedUsernameForPrivateContact.equals("Sign Out")) {
			System.exit(1);
			System.gc();
		}
		sh.updateText();

	}

	public void onSectionAttached(int number) {

		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		default:
			mTitle = getString(R.string.title_section2);

			// mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			sh.sendReseiveUserRequest();

			getMenuInflater().inflate(R.menu.chat, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);

			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_chat, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((Chat) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public boolean isSignIn() {
		return isSignIn;
	}

	public void setSignIn(boolean isSignIn) {
		this.isSignIn = isSignIn;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Toast.makeText(Chat.this, "Long press to Exit",
					Toast.LENGTH_SHORT).show();
			return true;
		}else if(event.getRepeatCount()>1){
			System.exit(1);
		} 
		
			return super.onKeyDown(keyCode, event);
	}

	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// // super.onBackPressed();
	// if (!isSignIn) {
	// super.onBackPressed();
	// }
	// super.onPause();
	// }
}
