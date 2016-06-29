package skychat.server;

import java.util.*;
import javax.websocket.Session;

public class Channel {
	private String name;
	private Set<Session> sessions;

	public Channel(String name) {
		this.name = name;
		this.sessions = Collections.synchronizedSet(new HashSet<Session>());
	}

	public void addSession(Session session) {
		this.sessions.add(session);
	}
	
	public void removeSession(Session session) { 
		this.sessions.remove(session);
	}

	public int showNumberOfUsers() {
		return this.sessions.size();
	}
	
	public Set<Session> getSessions() {
		return this.sessions;
	}
}
