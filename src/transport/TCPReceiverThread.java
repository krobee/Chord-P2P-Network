package transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import main.StoreData;
import node.Node;
import node.PeerNode;
import util.Logger;
import node.DiscoveryNode;
import transport.Protocol;

public class TCPReceiverThread extends Thread {

	private Socket socket;
	private ObjectInputStream ois;
	private Node node;

	public TCPReceiverThread(Node node, Socket socket) throws IOException {
		this.node = node;
		this.socket = socket;
		ois = new ObjectInputStream(this.socket.getInputStream());
	}

	@Override
	public void run() {
		while (socket != null) {
			try {

				int code = ois.readInt();
				PeerNode p = (PeerNode) ois.readObject();

				switch (code) {

				// peer to peer operation
				case (Protocol.PEER_TO_DISCOVERY_JOIN):
					((DiscoveryNode) node).join(p);
					break;

				case (Protocol.PEER_TO_DISCOVERY_EXIT):
					((DiscoveryNode) node).exit(p);
					break;

				case (Protocol.DISCOVERY_TO_PEER):
					((PeerNode) node).join(p);
					break;

				case (Protocol.PEER_TO_PEER_EXIT):
					((PeerNode) node).exit(p);
					break;

				case (Protocol.QUERY_REQUEST):
					((PeerNode) node).sendQuery(p);
					break;

				case (Protocol.QUERY_RESPOND):
					((PeerNode) node).getQuery(p);
					break;

				case (Protocol.UPDATE):
					int updateType = ois.readInt();
					((PeerNode) node).getUpdate(p, updateType);
					break;
					
				case (Protocol.UPDATE_INDEX):
					int index = ois.readInt();
					((PeerNode) node).getUpdate(index, p);
					break;

				// store to discovery operation
				case (Protocol.STORE_TO_DISCOVERY):
					((DiscoveryNode) node).getRandPeerNode(p);
					break;

				case (Protocol.DISCOVERY_TO_STORE):
				case (Protocol.FILE_TRANSFER_RESPOND):
					((StoreData) node).sync(p);
					break;

				// transfer files
				case (Protocol.FILE_TRANSFER_REQUEST):
					byte[] fileBytes = (byte[]) ois.readObject();
					String fileName = (String) ois.readObject();
					int key = ois.readInt();
					((PeerNode) node).writeFileS(p, fileBytes, fileName, key);
					break;

				// redistribute files
				case (Protocol.REDIS_REQUEST):
					key = ois.readInt();
					((PeerNode) node).sendFile(p, key);
					break;

				case (Protocol.REDIS_RESPOND):
					fileBytes = (byte[]) ois.readObject();
					fileName = (String) ois.readObject();
					key = ois.readInt();
					((PeerNode) node).writeFileP(p, fileBytes, fileName, key);
					break;

				default:
					Logger.error(TCPReceiverThread.class, "Unknown code");

				}
			} catch (ClassNotFoundException e) {
				Logger.error(TCPReceiverThread.class, e.getMessage());
			} catch (IOException e) {
				Logger.error(TCPReceiverThread.class, e.getMessage());
				break;
			}
		}
	}

}
