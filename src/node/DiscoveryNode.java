package node;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import main.StoreData;
import transport.TCPSender;
import util.Logger;
import util.Util;
import transport.Protocol;

public class DiscoveryNode extends Node {

	private ArrayList<PeerNode> peerNodeList;

	public DiscoveryNode(String host, int port) {
		super(host, port);
		peerNodeList = new ArrayList<PeerNode>();
	}

	public void join(PeerNode p) {
		// check ID collision
		while (checkCollision(p)) {
			int oldID = p.getID();
			p.regeID();
			p.update(p, Protocol.UPDATE_ID);
			Logger.info(DiscoveryNode.class, "Collision in ID " + oldID + ", new ID is " + p.getID());
		}

		// send a random node back
		try {
			Logger.info(DiscoveryNode.class, p.getNickname() + " joined the system");

			TCPSender sender = new TCPSender(p);

			if (peerNodeList.size() == 0) {
				Logger.info(DiscoveryNode.class, "Send original node " + p + " back");
				sender.sendData(Protocol.DISCOVERY_TO_PEER, p);
			} else {
				PeerNode rand = getRandPeerNode();
				Logger.info(DiscoveryNode.class, "Send random node " + rand + " back");
				sender.sendData(Protocol.DISCOVERY_TO_PEER, rand);
			}

			peerNodeList.add(p);

		} catch (UnknownHostException e) {
			Logger.error(DiscoveryNode.class, e.getMessage());
		} catch (IOException e) {
			Logger.error(DiscoveryNode.class, e.getMessage());
		}

	}

	public void exit(PeerNode p) {
		try {
			TCPSender sender = new TCPSender(p.getPred());
			sender.sendData(Protocol.PEER_TO_PEER_EXIT,	 p);
			Logger.info(DiscoveryNode.class, p.getNickname() + " exited the system");
			peerNodeList.remove(p);
		} catch (IOException e) {
			Logger.error(DiscoveryNode.class, e.getMessage());
		}
	}

	public boolean checkCollision(PeerNode pnode) {
		boolean check = false;
		for (int i = 0; i < peerNodeList.size(); i++) {
			if (peerNodeList.get(i).getID() == pnode.getID()) {
				check = true;
				break;
			}
		}
		return check;
	}

	public PeerNode getRandPeerNode() {
		int index = Util.getRandInt(0, peerNodeList.size());
		return peerNodeList.get(index);
	}

	public void getRandPeerNode(PeerNode p) {
		try {
			int index = Util.getRandInt(0, peerNodeList.size());
			PeerNode rand = peerNodeList.get(index);

			TCPSender sender = new TCPSender(p);
			sender.sendData(Protocol.DISCOVERY_TO_STORE, rand);

		} catch (IOException e) {
			Logger.error(DiscoveryNode.class, e.getMessage());
		}
	}

}
