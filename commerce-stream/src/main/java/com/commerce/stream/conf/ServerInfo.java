package com.commerce.stream.conf;

import java.util.UUID;

public class ServerInfo {

	private UUID serverId;

	private String serverName;

	public ServerInfo(String serverName) {
		this.serverName = serverName;
		this.serverId = UUID.randomUUID();
	}

	public UUID getServerId() {
		return serverId;
	}

	public void setServerId(UUID serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerInfo [serverId=");
		builder.append(serverId);
		builder.append(", serverName=");
		builder.append(serverName);
		builder.append("]");
		return builder.toString();
	}
	
}
