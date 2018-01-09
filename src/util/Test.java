package util;

import java.io.File;

import node.PeerNode;
import util.Util;

public class Test {
	public static void main(String[] args){
		
		PeerNode n = new PeerNode("jupiter", 8080);
		System.out.println(n.isBetween(2, 1, 1, false, false));
		
	}
}
