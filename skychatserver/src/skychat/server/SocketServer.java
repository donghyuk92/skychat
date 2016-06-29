package skychat.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;

@ServerEndpoint(value="/{channel}")
public class SocketServer {

	//채널 리스트
	public static final HashMap<String, Channel> channelList = new HashMap<String, Channel>();
	
	//전체 리스트
	public static final HashMap<String, User> sessions = new HashMap<String, User>();
	private JSONUtils jsonUtils = new JSONUtils();	
	
	/**
	 * Called when a socket connection opened
	 * */
	@OnOpen
	public void onOpen(Session session, @PathParam("channel") String channel) {
		
		System.out.println(session.getId() + " has opened a connection from " + channel);
		
		Channel ch;
		
		if(!channelList.containsKey(channel)) {
			ch = new Channel(channel);
			channelList.put(channel, ch);
			System.out.println("channel create!");
		} else {
			ch = channelList.get(channel); 
		}
		
		User user = new User(session,channel);
		sessions.put(session.getId(), user);
		// Adding session to session list
		ch.addSession(session);
		
		Map<String, String> queryParams = getQueryMap(session.getQueryString());
		
		System.out.println(queryParams);

		String name = "";

		if (queryParams.containsKey("name")) {

			// Getting client name via query param
			name = queryParams.get("name");
			try {
				name = URLDecoder.decode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			user.setUsername(name);
		}

		try {
			// Sending session id to the client that just connected
			session.getBasicRemote().sendText(
					jsonUtils.getClientDetailsJson(session.getId(), "Your session details"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Notifying all the clients about new person joined
		sendMessageToAll(session.getId(), user, " joined conversation!", true,	false);

	}

	/**
	 * method called when new message received from any client
	 * 
	 * @param message
	 *            JSON message from client
	 * 
	 * */
	@OnMessage
	public void onMessage(String message, Session session) {

		System.out.println("Message from " + session.getId() + ": " + message);

		String msg = null;
		
		// Parsing the json and getting message
		try {
			JSONObject jObj = new JSONObject(message);
			msg = jObj.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		User user = sessions.get(session.getId());
		
		// Sending the message to all clients
		sendMessageToAll(session.getId(), user,	msg, false, false);
	}	
	
	/**
	 * Method called when a connection is closed
	 * */
	@OnClose
	public void onClose(Session session) {

		System.out.println("Session " + session.getId() + " has ended");

		// Getting the client name that exited
		User user = sessions.get(session.getId());
		
		System.out.println(channelList.get(user.getCurrentChannel()).getSessions().size());
		
		channelList.get(user.getCurrentChannel()).removeSession(session);
		// removing the session from sessions list
		sessions.remove(session);
		
		System.out.println(channelList.get(user.getCurrentChannel()).getSessions().size());
		
		user = null;
		
		// Notifying all the clients about person exit
		//sendMessageToAll(session.getId(), user, " left conversation!", false,true);

	}

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
	
	/**
	 * Method to send message to all clients
	 * 
	 * @param sessionId
	 * @param message
	 *            message to be sent to clients
	 * @param isNewClient
	 *            flag to identify that message is about new person joined
	 * @param isExit
	 *            flag to identify that a person left the conversation
	 * */
	private void sendMessageToAll(String sessionId, User user,
			String message, boolean isNewClient, boolean isExit) {

		String name = user.getUsername();
		Channel ch = channelList.get(user.getCurrentChannel());
		
		// Looping through all the sessions and sending the message individually
		for (Session s : ch.getSessions()) {
			String json = null;

			// Checking if the message is about new client joined
			if (isNewClient) {
				json = jsonUtils.getNewClientJson(sessionId, name, message,
						sessions.size());

			} else if (isExit) {
				// Checking if the person left the conversation
				json = jsonUtils.getClientExitJson(sessionId, name, message,
						sessions.size());
			} else {
				// Normal chat conversation message
				json = jsonUtils
						.getSendAllMessageJson(sessionId, name, message);
			}

			try {
				System.out.println("Sending Message To: " + sessionId + ", "
						+ json);

				s.getBasicRemote().sendText(json);
			} catch (IOException e) {
				System.out.println("error in sending. " + s.getId() + ", "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}
	// Getting query params
	public static Map<String, String> getQueryMap(String query) {
		Map<String, String> map = Maps.newHashMap();
		if (query != null) {
			String[] params = query.split("&");
			for (String param : params) {
				String[] nameval = param.split("=");
				map.put(nameval[0], nameval[1]);
			}
		}
		return map;
	}
}