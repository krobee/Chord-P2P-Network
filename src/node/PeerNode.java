package node;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import transport.Protocol;
import transport.TCPSender;
import util.Util;
import util.Config;
import util.Logger;

public class PeerNode extends Node {

	private static final long serialVersionUID = 1113799434508676095L;
	private int temp;
	private int id;
	private PeerNode pred;
	private PeerNode n1;
	private PeerNode[] ftNode;
	private PeerNode queryNode;
	private int[] ftStart;
	private int[] snapshot;
	private ArrayList<String> fileNames;
	private ArrayList<Integer> fileKeys;
	private boolean flag = false;

	public PeerNode(String host, int port) {
		super(host, port);
		pred = null;
		ftNode = new PeerNode[Config.FINGER_TABLE_SIZE];
		ftStart = new int[Config.FINGER_TABLE_SIZE];
		snapshot = new int[Config.FINGER_TABLE_SIZE];
		fileKeys = new ArrayList<>();
		fileNames = new ArrayList<>();
		id = Util.generateID();
		for (int i = 0; i < ftStart.length; i++) {
			int value = (int) (id + Math.pow(2, i));
			ftStart[i] = Util.convertToCircleID(value);
		}
	}

	public PeerNode(String host, int port, int ID) {
		super(host, port);
		pred = null;
		ftNode = new PeerNode[Config.FINGER_TABLE_SIZE];
		ftStart = new int[Config.FINGER_TABLE_SIZE];
		snapshot = new int[Config.FINGER_TABLE_SIZE];
		fileKeys = new ArrayList<>();
		fileNames = new ArrayList<>();
		this.id = ID;
		for (int i = 0; i < ftStart.length; i++) {
			int value = (int) (ID + Math.pow(2, i));
			ftStart[i] = Util.convertToCircleID(value);
		}
	}

	public PeerNode findSucc(PeerNode n, int id) {
		n = query(findPred(n, id));
		return n.ftNode[0];
	}

	public PeerNode findPred(PeerNode n, int id) {
		PeerNode p = n;
		PeerNode checkNode = null;
		while (!isBetween(id, p.id, p.ftNode[0].id, false, true)) {

			if (checkNode != null) {
				if (checkNode.equals(p))
					break;
			}

			checkNode = p;
			p = findClosest(p, id);

			if (p.equals(this))
				break;
		}
		return p;
	}

	public PeerNode findClosest(PeerNode n, int id) {
		for (int i = n.ftNode.length - 1; i >= 0; i--) {
			if (n.ftNode[i] == null) {
				continue;
			}
			if (isBetween(n.ftNode[i].id, n.id, id, false, false)) {
				return n.ftNode[i];
			}
		}
		return n;
	}

	// -------------
	// OLD VERSION

	public void exit(PeerNode p){
		PeerNode succ = p.ftNode[0];
		
		// redistribute files
		
		succ.setPred(this);
		update(succ, Protocol.UPDATE_PRED);
		
		for(int i = 0; i < Config.FINGER_TABLE_SIZE; i++){
			if(ftNode[i].id == p.id){
				ftNode[i] = succ;
			}
		}
		
		Logger.info(PeerNode.class, getNickname() + " updated");
		printNodeInfo();
	}
	
	public void join(PeerNode p) {
		
		if (!equals(p)) {
			p = query(p);
			initFingerTable(p);
			updateOthers(p);
		}

		// this is the only node in the network
		else {
			for (int i = 0; i < ftNode.length; i++) {
				ftNode[i] = this;
			}
			setPred(this);
		}

		Logger.info(PeerNode.class, getNickname() + " updated");
		printNodeInfo();

		redistribute();
		Logger.info(PeerNode.class, getNickname() + " redistributing files");
		printNodeInfo();

	}

	public void initFingerTable(PeerNode p) {
		ftNode[0] = query(findSucc(p, ftStart[0]));
		snapshot[0] = ftNode[0].id;

		PeerNode succ = ftNode[0];
		setPred(succ.pred);
		succ.setPred(this);
		update(succ, Protocol.UPDATE_PRED);

		for (int i = 0; i < ftNode.length - 1; i++) {
			if (isBetween(ftStart[i + 1], id, ftNode[i].id, true, false)) {
				ftNode[i + 1] = ftNode[i];
			} else {
				ftNode[i + 1] = query(findSucc(p, ftStart[i + 1]));
				if (isBetween(id, ftStart[i + 1], ftNode[i + 1].id, true, false) && id < ftNode[i + 1].id) {
					ftNode[i + 1] = this;
				}
			}
			snapshot[i + 1] = ftNode[i + 1].id;
		}
		Logger.info(PeerNode.class, getNickname() + " initialized");
		printNodeInfo();
	}

	public void updateOthers(PeerNode t) {
		for (int i = 0; i < ftNode.length; i++) {
			if (id > ftStart[i] && id < snapshot[i]) {
				ftNode[i] = this;
			}
		}
		for (int i = 0; i < ftNode.length; i++) {
			int value = (int) (id - Math.pow(2, i));
			value = Util.convertToCircleID(value);

			PeerNode p = query(findPred(t, value));

			PeerNode temp = query(p.ftNode[0]);
			if (temp.id == value) {
				updateFingerTable(temp, this, i);
			}

			if (isBetween(ftStart[i], p.id, id, false, true) && ftNode[i].id == p.id) {
				ftNode[i] = this;
			}
			if (!p.equals(this) && isBetween(p.ftStart[i], p.id, id, false, true)) {
				updateFingerTable(p, this, i);
			}
		}

	}

	public void updateFingerTable(PeerNode n1, PeerNode s, int i) {

		if (isBetween(s.id, n1.id, n1.ftNode[i].id, true, false)) {
			n1.ftNode[i] = s;
			update(Protocol.UPDATE_INDEX, n1, i);
			PeerNode p = query(n1.pred);
			if (!p.equals(s)) {
				if (isBetween(s.id, p.ftStart[i], p.ftNode[i].id, true, false)) {
					updateFingerTable(p, s, i);
				}
			}
		}
	}

	// OLD VERSION
	// -------------

	// -----------------
	// STABLIZER VERSION

	// public void join(PeerNode p) {
	// if (!equals(p)) {
	// p = query(p);
	// n1 = p;
	// ftNode[0] = findSucc(p, id);
	// } else {
	// flag = true;
	// for (int i = 0; i < ftNode.length; i++) {
	// ftNode[i] = this;
	// }
	// setPred(this);
	// }
	// printNodeInfo();
	// }
	//
	// public void stabilize() {
	// if (ftNode[0] != null) {
	// if (ftNode[0].pred != null) {
	// ftNode[0] = query(ftNode[0]);
	// PeerNode x = ftNode[0].pred;
	//
	// if (isBetween(x.id, id, ftNode[0].id, false, false)) {
	// ftNode[0] = query(x);
	// }
	// }
	//
	// PeerNode succ = ftNode[0];
	// if (succ.pred == null || isBetween(id, succ.pred.id, succ.id, false,
	// false)) {
	// succ.setPred(this);
	// update(succ, Protocol.UPDATE_PRED);
	// }
	// }
	//
	// }
	//
	// public void fixFingers() {
	// int index = Util.getRandInt(1, Config.FINGER_TABLE_SIZE);
	// if (flag == true) {
	// n1 = ftNode[0];
	// }
	// if (n1 != null) {
	// n1 = query(n1);
	// if (ftNode[index] == null) {
	// ftNode[index] = findSucc(n1, ftStart[index]);
	//
	// printNodeInfo();
	// } else {
	// temp = ftNode[index].id;
	// ftNode[index] = findSucc(n1, ftStart[index]);
	// if (temp != ftNode[index].id) {
	// printNodeInfo();
	// }
	// }
	// }
	// }

	// STABLIZER VERSION
	// -----------------

	public void update(PeerNode p, int updateType) {
		try {
			TCPSender sender = new TCPSender(p);
			sender.sendData(Protocol.UPDATE, p, updateType);
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}
	}
	
	public void update(int code, PeerNode p, int index) {
		try {
			TCPSender sender = new TCPSender(p);
			sender.sendData(code, this, index);
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}
	}
	

	public void getUpdate(PeerNode p, int updateType) {
		switch (updateType) {
		case (Protocol.UPDATE_PRED):
			setPred(p.pred);
			break;
			
		case (Protocol.UPDATE_ID):
			id = p.id;
			break;
			
//		case (Protocol.UPDATE_ALL):
//			for (int i = 0; i < ftNode.length; i++) {
//				ftNode[i] = p.ftNode[i];
//			}
		}

		Logger.info(PeerNode.class, "Updated -- " + updateType);
		printNodeInfo();
	}
	
	public void getUpdate(int index, PeerNode p) {

			
			ftNode[index] = p;

		Logger.info(PeerNode.class, "Updated -- index " + index);
		printNodeInfo();
	}
	
	

	public synchronized PeerNode query(PeerNode p) {
		queryNode = null;
		try {
			TCPSender sender = new TCPSender(p);
			sender.sendData(Protocol.QUERY_REQUEST, this);
			wait();
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		} catch (InterruptedException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}
		return queryNode;
	}

	public void sendQuery(PeerNode p) {
		try {
			TCPSender sender = new TCPSender(p);
			sender.sendData(Protocol.QUERY_RESPOND, this);
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}
	}

	public synchronized void getQuery(PeerNode p) {
		queryNode = p;
		notify();
	}

	public void writeFileS(PeerNode node, byte[] imageBytes, String fileName, int key) {
		try {
			File dir = new File("tmp");
			if (!dir.exists()) {
				dir.mkdir();
			}

			File file = new File(dir + "/" + fileName);
			Files.write(file.toPath(), imageBytes);

			fileKeys.add(key);
			fileNames.add(fileName);

			Logger.info(PeerNode.class, "File with key [" + key + "] is stored");
			printNodeInfo();

			// send respond back
			TCPSender sender = new TCPSender(node);
			sender.sendData(Protocol.FILE_TRANSFER_RESPOND, this);
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}

	}

	public synchronized void writeFileP(PeerNode node, byte[] imageBytes, String fileName, int key) {
		try {
			File dir = new File("tmp");
			if (!dir.exists()) {
				dir.mkdir();
			}

			File file = new File(dir + "/" + fileName);
			Files.write(file.toPath(), imageBytes);

			fileKeys.add(key);
			fileNames.add(fileName);

			Logger.info(PeerNode.class, "File with key [" + key + "] is stored");
			printNodeInfo();

			// send respond back
			notify();
		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}

	}

	public synchronized void redistribute() {
		if (this != ftNode[0]) {
			PeerNode p = query(ftNode[0]);
			for (int i = 0; i < p.fileKeys.size(); i++) {
				int key = p.fileKeys.get(i);
				if (isBetween(id, key, p.id, true, false) && key != p.id) {
					try {
						TCPSender sender = new TCPSender(p);
						sender.sendData(Protocol.REDIS_REQUEST, this, key);
						wait();

					} catch (IOException e) {
						Logger.error(PeerNode.class, e.getMessage());
					} catch (InterruptedException e) {
						Logger.error(PeerNode.class, e.getMessage());
					}
				}
			}
		}

	}

	public void sendFile(PeerNode p, int key) {
		try {
			int index = fileKeys.indexOf(key);
			if (index == -1) {
				Logger.error(PeerNode.class, "File not found");
				TCPSender sender = new TCPSender(p);
				sender.sendData(Protocol.QUERY_RESPOND, this);
			} else {
				String fileName = fileNames.get(index);
				File file = new File("tmp/" + fileName);

				TCPSender sender = new TCPSender(p);
				sender.sendData(Protocol.REDIS_RESPOND, this, file, fileName, key);

				if (file.delete()) {
					Logger.info(PeerNode.class, "File " + fileName + " is deleted");
				} else {
					Logger.error(PeerNode.class, "Delete operation failed");
				}

				fileKeys.remove(index);
				fileNames.remove(index);

				Logger.info(PeerNode.class, getNickname() + " Redistributing Files ...");
				printNodeInfo();
			}

		} catch (IOException e) {
			Logger.error(PeerNode.class, e.getMessage());
		}

	}

	public void printNodeInfo() {
		String str = "\n*****PEER NODE INFO*****\n";
		str += getNickname();
		str += "\nSucc: " + ftNode[0] + " | Pred: " + pred;
		str += "\nFinger Node: " + Arrays.toString(ftNode);
		str += "\nFinger Start: " + Arrays.toString(ftStart);
		str += "\nFile Keys: " + Arrays.toString(fileKeys.toArray());
		str += "\n";
		System.out.println(str);
	}

	public boolean equals(PeerNode p) {
		return getHost().equalsIgnoreCase(p.getHost()) && getPort() == p.getPort();
	}

	public String getNickname() {
		return "[" + id + "-" + getHost() + "-" + getPort() + "]";
	}

	public String toString() {
		return String.valueOf(id);
	}

	public void setPred(PeerNode p) {
		pred = p;
	}

	public void regeID() {
		id = Util.generateID();
	}

	public void setID(int newID) {
		id = newID;
	}

	public PeerNode[] getFT() {
		return ftNode;
	}

	public PeerNode getPred(){
		return pred;
	}
	
	public int getID() {
		return id;
	}

	public boolean isBetween(int id, int min, int max, boolean left, boolean right) {
		if (left == true && right == true) {
			if (min < max) {
				return (id >= min && id <= max);
			} else {
				return (id >= min || id <= max);
			}
		} else if (left == true && right == false) {
			if (min < max) {
				return (id >= min && id < max);
			} else {
				return (id >= min || id < max);
			}
		} else if (left == false && right == true) {
			if (min < max) {
				return (id > min && id <= max);
			} else {
				return (id > min || id <= max);
			}
		} else {
			if (min < max) {
				return (id > min && id < max);
			} else {
				return (id > min || id < max);
			}
		}
	}

}
