package edu.na1.assignment.test;

//server port number and IPaddress is initialized and the destination file storage folder for Peer2 is set and search folder for Peer 2 is set
/**
 * 
 * Client2 instance is created and sends request to the server and destination folder for the client2 is defined to receive the requested files from server.
 *
 */

import edu.na1.assignment.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestPeer_2 {

	public static void main(String[] args) throws IOException {

        String downloadLocationOfFiles = "C:/Peer2";
        int serverPortNumber = 8085;
        String serverIpAddress = "127.0.0.1";

        List<String> locationsToSearch = new ArrayList<>();
        locationsToSearch.add("C:/FileSearch2");


        Peer client = new Peer(8087, serverIpAddress, serverPortNumber, downloadLocationOfFiles, locationsToSearch, "PEER 2");

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
