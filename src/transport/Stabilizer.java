package transport;

import node.PeerNode;
import util.Config;
import util.Logger;

public class Stabilizer extends Thread{
	
	private PeerNode p;
	
	public Stabilizer(PeerNode p){
		this.p = p;
	}
	
	public void run(){
		while(true){
			try {
				Thread.sleep(Config.interval);
			} catch (InterruptedException e) {
				Logger.error(Stabilizer.class, e.getMessage());
			}
//			p.stabilize();
//			p.fixFingers();
			
		}
	}
}
