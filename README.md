# [Distributed System] Chord P2P Network
This project is for CS555 Distributed System. It uses peer-to-peer distributed hash table to store query and retrieve data from corresponding node. 
## Details
### Components
* discovery node is for registering and maintaining information about the list of peers in the system
* each peer node has a finger table to traverse the overlay efficiently
* StoreData is for ensuring the storage of the content on the correct peer in the system
### Optimization
* applied multi-threading for nodes communication
* utilized heartbeat mechanism to decrease finger table updating time
