package transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import node.Node;
import node.PeerNode;
import util.Logger;

public class TCPServerThread extends Thread {

	private Node node;
	private ServerSocket serverSocket;
	private int port;

	public TCPServerThread(Node node) throws IOException {
		this.node = node;
		port = node.getPort();
		serverSocket = new ServerSocket(port);
	}


	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				TCPConnection conn = new TCPConnection(node, socket);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				continue;
			}

		}
	}

}
