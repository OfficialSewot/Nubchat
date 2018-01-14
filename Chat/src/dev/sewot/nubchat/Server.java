package dev.sewot.nubchat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	//unique id for all connections
	private static int uniqueId;
	//ArrayList with clients
	private ArrayList<ClientThread> al;
	//GUI
	private ServerGUI sg;
	//display time
	private SimpleDateFormat sdf;
	//port
	private int port;
	//boolean for server on and off
	private boolean keepGoing;
	
	
	
	//Server constructor that listen on the port
	
	public Server(int port){
		this(port, null);
	}



	public Server(int port, ServerGUI sg) {
		this.sg = sg;
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	
	public void start(){
		keepGoing = true;
		//create socket server and wait for connection requests
		try{
			//socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			
			//infinite loop to wait for connections
			while(keepGoing){
				//message that we are waiting
				display("Server waiting for Clients on port " + port );
				
				//accept connection
				Socket socket = serverSocket.accept();
				
				//if stop was asked
				if(!keepGoing)
					break;
				
				//make thread
				ClientThread t = new ClientThread(socket); 
				
				//save it in the ArrayList
				al.add(t);
				
				t.start();
			}
			//i was asked to stop
			try{
				serverSocket.close();
				for(int i = 0; i < al.size(); i++){
					ClientThread tc = al.get(i);
					try{
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					}
					catch(IOException ioE){
						
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}	
		}
		catch(IOException e){
			String msg = sdf.format(new Date()) + "Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	protected void stop(){
		keepGoing = false;
		try{
			new Socket("localhost", port);
		}
		catch(Exception e){
			
		}
		
	
	}
	
	//Shows events in the Console/GUI
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null) 
			System.out.println(time);
		else 
			sg.appendEvent(time + "\n"); 
		
	}
	
	//Broadcast a message to all clients
	
	private synchronized void broadcast(String msg) {
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		if(sg == null)
			System.out.println(messageLf);
		else
			sg.appendRoom(messageLf);
		
		//Loop is in reverse if we need to remove a client (don't really know why but not my code lul)
		
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			//try to write to client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
	
	synchronized void remove(int id) {
		//scan the array list until we found the id
		for(int i = 0; i < al.size(); ++i) {
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/*
	 * To run as a coinsole app just open a console window and:
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */
	
	public static void main(String[] args) {
		int portNumber = 1500;
		switch(args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			}
			catch(Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
		case 0:
			break;
		default:
			System.out.print("Usage is: > java Server [portNumber]");
			return;
		}
		Server server = new Server(portNumber);
		server.start();
	}
	
	class ClientThread extends Thread{
		
		//the socket where to listen
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		//my unique id
		int id;
		//Username of client
		String username;
		//Message
		ChatMessage cm;
		//the date i connect
		String date;
		
		//Constructor
		ClientThread(Socket socket){
			//a unique id
			id = ++uniqueId;
			this.socket = socket;
			
			//creating both streams
			System.out.println("Thread trying to create Object Input/Output Streams");
			
			try {
				//create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput= new ObjectInputStream(socket.getInputStream());
				//read username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e){
				System.out.println("Error creating Input/Output Streams: " + e);
				return;
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
}
