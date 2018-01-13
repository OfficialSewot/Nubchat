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
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null) {
			System.
		}
	}
}
