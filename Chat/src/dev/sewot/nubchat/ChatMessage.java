package dev.sewot.nubchat;

import java.io.*;

public class ChatMessage implements Serializable {
	
	protected static final long serialVersionUID = 1337;
	
	/*
	 * Different types of messages by the client.
	 * WHOISIN -> list of users
	 * MESSAGE -> normal message
	 * LOGOUT -> logout
	 */
	
	
	
	
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	
	
	
}
