package edu.na1.assignment.test.star;

import java.io.IOException;
import edu.na1.assignment.peer.PeerHead;

// Peer port number is initialized and file download folder is initialized
public class TestPeer_0 {

    public static final int PEER_PORT_NUMBER = 8085;

    public static void main(String[] args) throws IOException {

        //Location.. where client will download the files
        String downloadLocation = "D:/Peer1";
      
        PeerHead peerHead = new PeerHead(PEER_PORT_NUMBER,downloadLocation);

        peerHead.startServer();

	}

}
