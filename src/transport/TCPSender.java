package transport;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.file.Files;
import node.Node;
import node.PeerNode;

public class TCPSender implements Serializable {

	private ObjectOutputStream oos;
	private Socket socket;

	// constructors
	public TCPSender(Socket socket) throws IOException{
		oos = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}
	
	public TCPSender(Node node) throws IOException {
		Socket socket = new Socket(node.getHost(), node.getPort());
		oos = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}
	
	public TCPSender(String host, int port) throws IOException {
		Socket socket = new Socket(host, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}

	
	// functions
	public void sendData(int code, Node node) throws IOException {
		oos.writeInt(code);
		oos.writeObject(node);
		oos.flush();
		socket.shutdownOutput();
	}
	
	public void sendData(int code, Node node, int updateType) throws IOException {
		oos.writeInt(code);
		oos.writeObject(node);
		oos.writeInt(updateType);
		oos.flush();
		socket.shutdownOutput();
	}
	
	public void sendData(int code, Node node, int updateType, int index) throws IOException {
		oos.writeInt(code);
		oos.writeObject(node);
		oos.writeInt(updateType);
		oos.writeInt(index);
		oos.flush();
		socket.shutdownOutput();
	}
	
	
	public void sendData(int code, Node n, File file, String fileName, int key) throws IOException{
		oos.writeInt(code);
		oos.writeObject(n);
		byte[] content = Files.readAllBytes(file.toPath());
		oos.writeObject(content);
		oos.writeObject(fileName);
		oos.writeInt(key);
		oos.flush();
		socket.shutdownOutput();
	}
}
