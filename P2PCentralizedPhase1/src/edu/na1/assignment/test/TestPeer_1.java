package edu.na1.assignment.test;


/**
 * 
 * Peer1 instance is created and sends request to the server and destination folder for the Peer1 is set to store the files received from server
 *
 */

import edu.na1.assignment.peer.Peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// server port number and IPaddress is initialized and the destination file storage folder for peer1  is set and search folder for peer1 is set
public class TestPeer_1 {

	public static void main(String[] args) throws IOException {

        String downloadLocationOfFiles = "C:/Peer1";
        int serverPortNumber = 8085;
        String serverIpAddress = "127.0.0.1";

        List<String> locationsToSearch = new ArrayList<>();
        locationsToSearch.add("C:/FileSearch1");

        Peer client = new Peer(8086, serverIpAddress, serverPortNumber, downloadLocationOfFiles, locationsToSearch, "PEER 1");

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
