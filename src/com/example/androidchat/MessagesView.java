package com.example.androidchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessagesView extends ViewGroup {
	private TextView screen;
	private Button send;
	private SocketHandler socket;
	private EditText txt;

	public static int extra = 0;

	public MessagesView(final Activity context) {
		super(context);
		extra = context.getWindowManager().getDefaultDisplay().getHeight() / 100;
		context.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init(context);
		addView(screen);
		addView(txt);
		addView(send);

		// LinearLayout.LayoutParams llp = new
		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// llp.setMargins(400, 0, 520,520); // llp.setMargins(left, top, right,
		// bottom);
		// screen.setLayoutParams(llp);
		screen.setMovementMethod(new ScrollingMovementMethod());
		screen.setVerticalScrollBarEnabled(true);
		screen.setGravity(Gravity.BOTTOM);
		screen.setBackgroundColor(Color.YELLOW);
		// screen.setEnabled(false);
		txt.setGravity(Gravity.CENTER | Gravity.BOTTOM);
	}

	private void init(final Activity context) {
		screen = new TextView(context);
		txt = new EditText(context);

		send = new Button(context);
		//send.setText("Send");
		send.setBackgroundResource(R.drawable.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMSG(context);
			}
		});
		txt.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					sendMSG(context);
					return true;
				}
				return false;
			}
		});
	}

	private void sendMSG(Activity context) {
		if (Chat.selectedUsernameForPrivateContact.equals("Public")) {
			socket.sendMessage(txt.getText().toString());
		} else {
			socket.sendPrivateMessage(txt.getText().toString(),
					Chat.selectedUsernameForPrivateContact);
		}

		if (extra > 0) {
			screen.scrollTo(0, extra);
		}
		txt.setText("");
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		int middleValue = getHeight() / 8;
		int maxValue = getHeight();
		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);
			if (v == screen) {
				v.layout(0, 0, getWidth(), maxValue - middleValue);
			} else if (v == txt) {
				v.layout(0, maxValue - middleValue,
						getWidth() - getWidth() / 6, maxValue);
			} else if (v == send) {
				v.layout(getWidth() - getWidth() / 6, maxValue - middleValue,
						getWidth(), maxValue);
			}
		}

	}

	public TextView getScreen() {
		return screen;
	}

	public void setSocketHundler(SocketHandler socket) {
		this.socket = socket;
	}

}
