package transport;

public class Protocol {
	public static final int PEER_TO_DISCOVERY_JOIN = 1;
	
	public static final int DISCOVERY_TO_PEER = 3;
	public static final int QUERY_REQUEST = 4;
	public static final int QUERY_RESPOND = 5;

	// get random node for StoreData
	public static final int STORE_TO_DISCOVERY = 6;
	public static final int DISCOVERY_TO_STORE = 7;
	
	// file transfer
	public static final int FILE_TRANSFER_REQUEST = 8;
	public static final int FILE_TRANSFER_RESPOND = 9;
	
	// update
	public static final int UPDATE = 10;
	public static final int UPDATE_PRED = 11;
	public static final int UPDATE_ALL = 12;
	public static final int UPDATE_ID = 13;
	public static final int UPDATE_INDEX = 17;
	
	// redistribute
	public static final int REDIS_REQUEST = 14;
	public static final int REDIS_RESPOND = 15;
	
	// exit
	public static final int PEER_TO_DISCOVERY_EXIT = 2;
	public static final int PEER_TO_PEER_EXIT = 16;
}
