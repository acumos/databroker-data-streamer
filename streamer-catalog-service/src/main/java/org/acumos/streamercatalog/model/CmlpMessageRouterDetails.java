package org.acumos.streamercatalog.model;

public class CmlpMessageRouterDetails {
	private String serverName;
	private int serverPort;
	private String userName;
	private String password;
	private String topicName;
	private String serializer;
	private String deSerializer;
	
	public CmlpMessageRouterDetails() {
		super();
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getSerializer() {
		return serializer;
	}

	public void setSerializer(String serializer) {
		this.serializer = serializer;
	}

	public String getDeSerializer() {
		return deSerializer;
	}

	public void setDeSerializer(String deSerializer) {
		this.deSerializer = deSerializer;
	}
	
	
}
