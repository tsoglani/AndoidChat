package com.example.androidchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class SocketHandler {
	private Socket socket;
	private static int port = 2000;
	private PrintWriter out;
	private BufferedReader in;
	public static boolean isNotClosing = true;
	private MessagesView screen;
	public  static String IP = "147.229.214.43";
	private Activity activity;
	public static String name = null;
	boolean hasExeption=false;
	public static final ArrayList<String> interactiveUsers = new ArrayList<String>() {
		public boolean add(String s) {
			if (contains(s)) {
				return false;
			}
			return super.add(s);
		}
	};
	public static final Hashtable<String, ArrayList<String>> list = new Hashtable<String, ArrayList<String>>() {

		public ArrayList<String> put(String key, ArrayList<String> value) {
			if (containsKey(key)) {
				return null;
			}
			return super.put(key, value);
		}

	};

	
	
	public SocketHandler() {

		new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					out = new PrintWriter(socket.getOutputStream(), true);
					receiver();
					
				} catch (Exception e) {
					hasExeption=true;
					e.printStackTrace();
				}
			}

		}.start();

	}
	
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(final Activity activity) {
		this.activity = activity;
		
		if (hasExeption) {
			AlertDialog.Builder alert = new AlertDialog.Builder(activity);

			alert.setTitle("IP");
			alert.setMessage("Enter external IP");

			// Set an EditText view to get user input
			final EditText input = new EditText(activity);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							if (!value.equals("")) {
								IP = value;
							
								new Thread(){public void run(){
									     Log.e("."+IP+".",IP);
											try {
												socket = new Socket(IP, port);
												out = new PrintWriter(socket.getOutputStream(), true);
												receiver();
											}catch (Exception e) {
												hasExeption = true;
												e.printStackTrace();
											}
										
									}}.start();
                                  
								
							}

						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();
			
		}
	}

	public MessagesView getScreen() {
		return screen;
	}

	public void setScreenView(MessagesView screen) {
		this.screen = screen;
	}

	private void receiver() {
		new Thread() {
			public void run() {
				try {
					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					while (isNotClosing) {
						try {
							Thread.sleep(100);
							try {
								String inputString = in.readLine();
								stringReceiverHandler(inputString);

							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e1) {

					e1.printStackTrace();
				} finally {
					try {
						in.close();
						out.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}.start();

	}

	private void send(String outputString) {
		try {

			out.println(outputString);
			// out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendName(String name) {
		out.println("Name:" + name);
	}

	public void sendMessage(String msg) {
		out.println("Text:User " + name + " Says: " + msg);
	}

	public void sendReseiveUserRequest() {
		if (name == null) {
			return;
		}
		out.println("Reseive_Users_Request:" + name);

	}

	public void sendPrivateMessage(String msg, String person) {
		if (msg.equals("@@To@@") || person.equals("@@To@@")) {
			activity.runOnUiThread(new Thread() {
				public void run() {
					Toast.makeText(
							activity,
							"You have to enter another name to do private conversation",
							Toast.LENGTH_LONG).show();
				}
			});
			return;
		}
		out.println("PrivateMessageFrom:" + name + "@@To@@" + person + "@@To@@"
				+ msg);
	}

	private void stringReceiverHandler(String input) {

		if (input.startsWith("AllUsers:")) {
			final String input2 = input = input.substring(9, input.length());
			// interactiveUsers.remove(name);

			if (name == null) {
				return;
			}

			activity.runOnUiThread(new Thread() {
				public void run() {
					// Toast.makeText(
					// activity,
					// input2 + ""
					// + Integer.toString(interactiveUsers.size()),
					// Toast.LENGTH_LONG).show();
					interactiveUsers.removeAll(interactiveUsers);
					// list.clear();
					// interactiveUsers = new
					// ArrayList<String>();//Arrays.asList(input.split("@@"))
					interactiveUsers.add("Public");
					String[] pinax = input2.split("@@");
					for (int i = 0; i < pinax.length; i++) {
						if (!name.equals(pinax[i])) {
							interactiveUsers.add(pinax[i]);
						}
					}
					interactiveUsers.add("Sign Out");
					final String[] menuText = new String[pinax.length + 2];

					for (int i = 0; i < interactiveUsers.size(); i++) {
						menuText[i] = interactiveUsers.get(i);
						list.put(interactiveUsers.get(i),
								new ArrayList<String>());
					}
					NavigationDrawerFragment.mDrawerListView
							.setAdapter(new ArrayAdapter<String>(activity
									.getActionBar().getThemedContext(),
									android.R.layout.simple_list_item_1,
									android.R.id.text1, menuText));

				}
			});

			// ///////////////////////////////////////// kalw tin privale panel

			return;
		} else if (input.equals("Name:null")) {
			activity.runOnUiThread(new Thread() {
				public void run() {
					Toast.makeText(activity, "Enter another Name",
							Toast.LENGTH_LONG).show();
				}
			});

			return;
		} else if (input.startsWith("Name:")) {
			activity.runOnUiThread(new Thread() {
				public void run() {
					Toast.makeText(activity, "Succes LogIn", Toast.LENGTH_LONG)
							.show();
					Chat chat = (Chat) activity;
					chat.successLogIn();
				}
			});

			name = input.replace("Name:", "");
			input = name + " Just logIn";
			return;
		} else if (input.startsWith("Text:")) {
			input = input.replace("Text:", "");
		} else if (input.startsWith("PrivateMessageFrom:")) {
			input = input.replace("PrivateMessageFrom:", "");
			String[] pinax = input.split("@@To@@");
			Iterator iter = list.keySet().iterator();
			while (iter.hasNext()) {
				String userName = (String) iter.next();

				// /////////////////////////////////////////////
				Log.e("pinax[0] = " + pinax[0], " pinax[1]= " + pinax[1]);
				Log.e("Cha =" + Chat.selectedUsernameForPrivateContact,
						"userName= " + userName);
				if ((pinax[0].equals(userName) || pinax[1].equals(userName))
						) {
					ArrayList<String> messages = list.get(userName);
					messages.add("From " + pinax[0] + " To " + pinax[1] + " : "
							+ pinax[2]);
					// Log.e("",messages.get(0));
					// Log.e("userName : "+userName,"name : "+name);
					// Log.e("pinax[1] = "+pinax[1],"pinax[0] = "+pinax[0]);
				}

				// ///////////////////////////////////////////////////////
			}
			activity.runOnUiThread(new Thread() {
				public void run() {

					updateText();
				}
			});
			return;
		}
		final String msg = input;
		activity.runOnUiThread(new Thread() {
			public void run() {
				if (list.isEmpty()) {
					list.put("Public", new ArrayList<String>());
				}
				list.get("Public").add(msg);
				updateText();
			}
		});

	}

	public void updateText() {

		activity.runOnUiThread(new Thread() {
			public void run() {
				if (!((Chat) (activity)).isSignIn()) {
					return;
				}
				if (Chat.selectedUsernameForPrivateContact.equals("Public"))
					screen.getScreen().setText("Public Speak");
				else {
					screen.getScreen().setText(
							"Private speek With user "
									+ Chat.selectedUsernameForPrivateContact);
				}
			}
		});

		Iterator iter = list.keySet().iterator();
		while (iter.hasNext()) {
			final String userName = (String) iter.next();
			final ArrayList<String> messages = list.get(userName);

			// /////////////////////////////////////////////

			if (userName.equals(Chat.selectedUsernameForPrivateContact)) {
				// Log.e("userName= "+userName,"name= "+name);
				for (int i = 0; i < messages.size(); i++) {
					// Log.e(" messages.size()= ",Integer.toString(
					// messages.size()));
					final int fi = i;
					activity.runOnUiThread(new Thread() {
						public void run() {
							// Log.e(Chat.selectedUsernameForPrivateContact,userName);
							screen.getScreen().append("\n" + messages.get(fi));
						}
					});

				}
			}

			// ///////////////////////////////////////////////////////
		}
	}
}
