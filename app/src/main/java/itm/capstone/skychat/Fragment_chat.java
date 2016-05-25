package itm.capstone.skychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.androidhive.webgroupchat.R;
import itm.capstone.skychat.other.Message;
import itm.capstone.skychat.other.Utils;
import itm.capstone.skychat.other.WsConfig;

/**
 * Created by User on 2016-05-14.
 */
public class Fragment_Chat extends Fragment {

    // LogCat tag
    private static final String TAG = Fragment_Chat.class.getSimpleName();

    Context ctx;
    // Chat messages list adapter
    private Adapter_MessagesList msgadapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    private Utils utils;

    // Client name
    private String name = null;

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    private Button btnSend;
    private EditText inputMsg;

    private WebSocketClient client;

    int mStackLevel = 0;
    public static final int DIALOG_FRAGMENT = 1;

    public Fragment_Chat() {

    }

    public static Fragment_Chat newInstance(Context ctx) {
        Fragment_Chat f = new Fragment_Chat();
        f.ctx = ctx;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mStackLevel = savedInstanceState.getInt("level");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        inputMsg = (EditText) view.findViewById(R.id.inputMsg);
        listViewMessages = (ListView) view.findViewById(R.id.list_view_messages);

        utils = new Utils(ctx);

        // Getting the person name from previous screen
        Intent i = getActivity().getIntent();
        name = "test";
        //name = i.getStringExtra("name");

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

        msgadapter = new Adapter_MessagesList(ctx, listMessages);
        listViewMessages.setAdapter(msgadapter);

        if(client == null) {
            /**
             * Creating web socket client. This will have callback methods
             * */
            try {
                client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET
                        + URLEncoder.encode(name, "UTF-8")), new WebSocketClient.Listener() {
                    @Override
                    public void onConnect() {

                    }

                    /**
                     * On receiving the message from web socket server
                     */
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
                     */
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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("TAG", client.toString());
        }
        Log.d("TAG", client.toString());
        client.connect();
        Log.d("TAG", client.toString());

        ImageView imageView = (ImageView) view.findViewById(R.id.emoticons);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_FRAGMENT);
            }
        });
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        return view;
    }

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
    public void onStop() {
        super.onStop();
        if(client != null & client.isConnected()){
            client.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(client != null & client.isConnected()){
            client.disconnect();
        }
    }

    /**
     * Appending message to list view
     * */
    private void appendMessage(final Message m) {
        getActivity().runOnUiThread(new Runnable() {

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

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ctx, message,
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
            Ringtone r = RingtoneManager.getRingtone(ctx,
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

    void showDialog(int type) {

        mStackLevel++;

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        switch (type) {

            case DIALOG_FRAGMENT:

                DialogFragment dialogFrag = Dialog_Emoticons.newInstance(123);
                dialogFrag.setTargetFragment(this, DIALOG_FRAGMENT);
                dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");

                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }

                break;
        }
    }
}