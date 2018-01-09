package transport;

import java.io.IOException;
import java.net.Socket;
import node.Node;
import util.Logger;

public class TCPConnection {
	private TCPReceiverThread receiver;
	private TCPSender sender;
	private Socket socket;

	public TCPConnection(Node node, Socket socket) throws IOException {
		this.receiver = new TCPReceiverThread(node, socket);
		this.sender = new TCPSender(socket);
		this.socket = socket;
		receiver.start();
	}
}
