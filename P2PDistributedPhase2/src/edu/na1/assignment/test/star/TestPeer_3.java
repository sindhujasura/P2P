package edu.na1.assignment.test.star;

//server port number and IPaddress is initialized 
/**
 * 
 * Peer3 instance is created and path for file search folder is set
 *
 */

import edu.na1.assignment.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestPeer_3 {

    public static final int PEER_PORT_NUMBER = 8088;

    public static void main(String[] args) throws IOException {

        int serverPortNumber = 8085; //It is connected to TestPeer2
        int downloadPort = 9003;
        String serverIpAddress = "127.0.0.1";

        List<String> locationsToSearch = new ArrayList<>();
        locationsToSearch.add("D:/FileSearch3");


        Peer client = new Peer(PEER_PORT_NUMBER, serverIpAddress, serverPortNumber, locationsToSearch, "PEER#3", downloadPort);

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
