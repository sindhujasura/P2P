package edu.na1.assignment.peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// This method maintains a list of peers which are connected to server
public class PeerServerConnectionHandler extends Thread{

    private int portNumber;
    private List<Socket> peersList;
    private List<String> directoryLocationsToSearch;
    private Thread runningThread;

    public PeerServerConnectionHandler(int portNumber, List<String> directoryLocationsToSearch, List<Socket> peersList) {
        this.portNumber = portNumber;
        this.peersList = peersList;
        this.directoryLocationsToSearch = directoryLocationsToSearch;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public List<Socket> getPeersList() {
        return peersList;
    }

    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        Socket connectionSocket;

        try {
            ServerSocket serverSocket = new ServerSocket(getPortNumber());
            //Accept new connections
            connectionSocket = serverSocket.accept();

            //Start a new Thread to serve the Requests for connectionSocket
            PeerConnectionWorker peerConnectionWorker = new PeerConnectionWorker(connectionSocket, directoryLocationsToSearch);
            peerConnectionWorker.start();

            //Add it to List
            getPeersList().add(connectionSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
           
        }


    }
}
