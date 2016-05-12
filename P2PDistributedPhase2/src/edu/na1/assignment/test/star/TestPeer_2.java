package edu.na1.assignment.test.star;


/**
 * 
 * Peer2 instance is created and path for File Search folder is set
 *
 */

import edu.na1.assignment.peer.Peer;
//server port number and IPaddress is initialized

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestPeer_2 {

    public static final int PEER_PORT_NUMBER = 8087;

    public static void main(String[] args) throws IOException {

        int serverPortNumber = 8085;
        int downloadPort = 9002;
        String serverIpAddress = "127.0.0.1";

        List<String> locationsToSearch = new ArrayList<>();
        locationsToSearch.add("D:/FileSearch2");


        Peer client = new Peer(PEER_PORT_NUMBER, serverIpAddress, serverPortNumber, locationsToSearch, "PEER#2", downloadPort);

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
