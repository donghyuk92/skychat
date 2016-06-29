package skychat.server;

import javax.websocket.Session;

public class User {
	
	private Session session;
	private String name;
	private String currentChannel;
	private boolean login;
	
	User(Session session, String channel) {
		this.session = session;
		this.currentChannel = channel;
	}
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	public String getUsername() {
		return this.name;
	}
	
	public void setUsername(String name) {
		this.name = name;
	}

	public String getCurrentChannel() {
		return this.currentChannel;
	}

	public void setCurrentChannel(String channel) {
		this.currentChannel = channel;
	}
	
	public void setLogin(boolean b) {
		this.login = b;
	}

	public boolean isLogin() {
		return this.login;
	}
}
