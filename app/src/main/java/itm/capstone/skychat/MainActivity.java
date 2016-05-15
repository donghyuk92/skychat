package itm.capstone.skychat;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.androidhive.webgroupchat.R;
import itm.capstone.skychat.other.Message;
import itm.capstone.skychat.other.Utils;
import itm.capstone.skychat.other.WsConfig;

public class MainActivity extends AppCompatActivity {

	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();

	private Button btnSend;
	private EditText inputMsg;

	private WebSocketClient client;

	// Chat messages list adapter
	private MessagesListAdapter msgadapter;
	private List<Message> listMessages;
	private ListView listViewMessages;

	private Utils utils;

	// Client name
	private String name = null;

	// JSON flags to identify the kind of JSON response
	private static final String TAG_SELF = "self", TAG_NEW = "new",
			TAG_MESSAGE = "message", TAG_EXIT = "exit";

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mNavItems.add(new NavItem("Home", "Meetup destination", R.drawable.bg_messages));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.drawer_shadow));
// DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
                setTitle(mNavItems.get(position).mTitle);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        // More info: http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/

        btnSend = (Button) findViewById(R.id.btnSend);
		inputMsg = (EditText) findViewById(R.id.inputMsg);
		listViewMessages = (ListView) findViewById(R.id.list_view_messages);

		utils = new Utils(getApplicationContext());

		// Getting the person name from previous screen
		Intent i = getIntent();
		name = i.getStringExtra("name");

		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Sending message to web socket server
				sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
						.toString()));

				// Clearing the input filed once message was sent
				inputMsg.setText("");
			}
		});

		listMessages = new ArrayList<Message>();

		msgadapter = new MessagesListAdapter(this, listMessages);
		listViewMessages.setAdapter(msgadapter);

		/**
		 * Creating web socket client. This will have callback methods
		 * */
		client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET
				+ URLEncoder.encode(name)), new WebSocketClient.Listener() {
			@Override
			public void onConnect() {

			}

			/**
			 * On receiving the message from web socket server
			 * */
			@Override
			public void onMessage(String message) {
				Log.d(TAG, String.format("Got string message! %s", message));

				parseMessage(message);

			}

			@Override
			public void onMessage(byte[] data) {
				Log.d(TAG, String.format("Got binary message! %s",
						bytesToHex(data)));

				// Message will be in JSON format
				parseMessage(bytesToHex(data));
			}

			/**
			 * Called when the connection is terminated
			 * */
			@Override
			public void onDisconnect(int code, String reason) {

				String message = String.format(Locale.US,
						"Disconnected! Code: %d Reason: %s", code, reason);

				showToast(message);

				// clear the session id from shared preferences
				utils.storeSessionId(null);
			}

			@Override
			public void onError(Exception error) {
				Log.e(TAG, "Error! : " + error);

				showToast("Error! : " + error);
			}

		}, null);

		client.connect();

        getSupportActionBar().hide();
	}

    /*
* Called when a particular item from the navigation drawer
* is selected.
* */
    private void selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
	/**
	 * Method to send message to web socket server
	 * */
	private void sendMessageToServer(String message) {
		if (client != null && client.isConnected()) {
			client.send(message);
		}
	}

	/**
	 * Parsing the JSON message received from server The intent of message will
	 * be identified by JSON node 'flag'. flag = self, message belongs to the
	 * person. flag = new, a new person joined the conversation. flag = message,
	 * a new message received from server. flag = exit, somebody left the
	 * conversation.
	 * */
	private void parseMessage(final String msg) {

		try {
			JSONObject jObj = new JSONObject(msg);

			// JSON node 'flag'
			String flag = jObj.getString("flag");

			// if flag is 'self', this JSON contains session id
			if (flag.equalsIgnoreCase(TAG_SELF)) {

				String sessionId = jObj.getString("sessionId");

				// Save the session id in shared preferences
				utils.storeSessionId(sessionId);

				Log.e(TAG, "Your session id: " + utils.getSessionId());

			} else if (flag.equalsIgnoreCase(TAG_NEW)) {
				// If the flag is 'new', new person joined the room
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				// number of people online
				String onlineCount = jObj.getString("onlineCount");

				showToast(name + message + ". Currently " + onlineCount
						+ " people online!");

			} else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// if the flag is 'message', new message received
				String fromName = name;
				String message = jObj.getString("message");
				String sessionId = jObj.getString("sessionId");
				boolean isSelf = true;

				// Checking if the message was sent by you
				if (!sessionId.equals(utils.getSessionId())) {
					fromName = jObj.getString("name");
					isSelf = false;
				}

				Message m = new Message(fromName, message, isSelf);

				// Appending the message to chat list
				appendMessage(m);

			} else if (flag.equalsIgnoreCase(TAG_EXIT)) {
				// If the flag is 'exit', somebody left the conversation
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				showToast(name + message);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(client != null & client.isConnected()){
			client.disconnect();
		}
	}

	/**
	 * Appending message to list view
	 * */
	private void appendMessage(final Message m) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				listMessages.add(m);

				msgadapter.notifyDataSetChanged();

				// Playing device's notification
				playBeep();
			}
		});
	}

	private void showToast(final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	/**
	 * Plays device's default notification sound
	 * */
	public void playBeep() {

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
