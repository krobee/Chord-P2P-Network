package node;

import java.io.Serializable;

public class Node implements Serializable {
	
	private String host;
	private int port;
	
	public Node(){
		
	}
	
	public Node(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public String toString(){
		return "[" + host + ":" + port + "]";
	}
	
	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
	
	public boolean equals(Node node){
		return host.equalsIgnoreCase(node.getHost()) && port == node.getPort();
	}
	
	public String getNickname(){
		return host + "-" + String.valueOf(port);
	}
	

}
