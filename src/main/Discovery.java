package main;

import java.io.IOException;
import java.net.UnknownHostException;

import node.DiscoveryNode;
import util.Config;
import util.Logger;
import util.Util;
import transport.TCPServerThread;

public class Discovery {
	public static void main(String[] args) {

		DiscoveryNode d = null;

		try {
			d = new DiscoveryNode(Config.DIS_HOST, Config.DIS_PORT);

			TCPServerThread serverThread;
			serverThread = new TCPServerThread(d);

			Logger.info(d.getClass(), d.toString() + " listening");
			serverThread.start();
		} catch (UnknownHostException e) {
			Logger.error(Discovery.class, e.getMessage());
		} catch (IOException e) {
			Logger.error(Discovery.class, e.getMessage());
		}

	}
}
