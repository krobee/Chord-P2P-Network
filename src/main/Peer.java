package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import util.Config;
import util.Logger;
import util.TypeConverter;
import util.Util;
import node.PeerNode;
import transport.TCPConnection;
import transport.TCPSender;
import transport.TCPServerThread;
import transport.Protocol;

public class Peer {

	public static void main(String[] args) {
		if (args.length != 2) {
			Logger.error(Peer.class, "main.Peer [PORT] [ID (-1 for random)]");
			System.exit(-1);
		}

		int port = Integer.parseInt(args[0]);
		int ID = Integer.parseInt(args[1]);
		if (ID >= Math.pow(2, Config.ID_BIT_RANGE) || ID < 0) {
			Logger.error(Peer.class, "Wrong ID space");
			System.exit(-1);
		}

		PeerNode p = null;
		try {
			String host = Util.getHostInetName();
			if (ID == -1) {
				p = new PeerNode(host, port);
			} else {
				p = new PeerNode(host, port, ID);
			}
			TCPServerThread serverThread = new TCPServerThread(p);
			serverThread.start();
			Logger.info(p.getClass(), p.getNickname() + " listening");

			// notify the discovery node to join the system
			TCPSender sender = new TCPSender(Config.DIS_HOST, Config.DIS_PORT);
			sender.sendData(Protocol.PEER_TO_DISCOVERY_JOIN, p);

			// start stabilizer
			// Stabilizer stab = new Stabilizer(p);
			// stab.start();

			// exit the system
			Scanner scan = new Scanner(System.in);
			String input = scan.next();
			while (!input.equals("q!")) {
				switch (input) {
				case ("exit"):
					sender = new TCPSender(Config.DIS_HOST, Config.DIS_PORT);
					sender.sendData(Protocol.PEER_TO_DISCOVERY_EXIT, p);
					Logger.info(p.getClass(), p.getNickname() + " exit");
					break;
				}
				input = scan.next();
			}
			System.exit(0);

		} catch (UnknownHostException e) {
			Logger.error(p.getClass(), e.getMessage());
		} catch (IOException e) {
			Logger.error(p.getClass(), e.getMessage());
		}
	}
}
