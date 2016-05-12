package edu.na1.assignment.test.star;


/**
 * 
 * Peer1 instance is created and File Search folder is created to search for the file
 *
 */

import edu.na1.assignment.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// server port number and IPaddress is initialized
public class TestPeer_1 {

    public static final int PEER_PORT_NUMBER = 8086;

    public static void main(String[] args) throws IOException {

        int serverPortNumber = 8085;
        int downloadPort = 9001;
        String serverIpAddress = "127.0.0.1";

        List<String> locationsToSearch = new ArrayList<>();
        locationsToSearch.add("D:/FileSearch1");

        Peer client = new Peer(PEER_PORT_NUMBER, serverIpAddress, serverPortNumber, locationsToSearch, "PEER#1", downloadPort);

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
