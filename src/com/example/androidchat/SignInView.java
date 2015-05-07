package com.example.androidchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInView extends ViewGroup {
	private EditText username;
	private TextView label;
	private Button enter;
	private SocketHandler socketHandler;

	public SignInView(Activity context) {
		super(context);
		init(context);

	}

	private void init(final Activity context) {
		username = new EditText(context);
		
		label = new TextView(context);
		enter = new Button(context);
		enter.setText("Enter");
		label.setText("Enter Username");
		label.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		label.setTextSize(context.getWindowManager().getDefaultDisplay()
				.getWidth() / 40);
		username.setBackgroundColor(Color.WHITE);
		setBackgroundColor(Color.GRAY);
		label.setTextColor(Color.WHITE);
		addView(username);
		addView(label);
		addView(enter);
		
		username.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
		        	if (username.getText().toString().replace(" ", "").length() > 1) {
						socketHandler.sendName(username.getText().toString());
					}else{
						Toast.makeText(context, "put a real nickname", Toast.LENGTH_LONG).show();
					}
		        	InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindowToken(), 0);
		        	
		          return true;
		        }
		        return false;
		    }
		});
		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (username.getText().toString().replace(" ", "").length() > 1) {
					socketHandler.sendName(username.getText().toString());
				}else{
					Toast.makeText(context, "put a real nickname", Toast.LENGTH_LONG).show();
				}

				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		});

	}

	public void setSocketHandler(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childs = getChildCount();
		for (int i = 0; i < childs; i++) {
			View v = getChildAt(i);
			if (v == label) {
				v.layout(0, 0, getWidth(), getHeight() / 6);
			} else if (v == username) {
				v.layout(0, getHeight() / 6, getWidth(), getHeight() / 3);
			} else if (v == enter) {
				v.layout(getWidth() / 2 - getWidth() / 6, getHeight() / 2,
						getWidth() / 2 + getWidth() / 6, getHeight() / 2
								+ getHeight() / 6);
			}
		}

	}
}
