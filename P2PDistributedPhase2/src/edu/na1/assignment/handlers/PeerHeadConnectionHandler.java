package edu.na1.assignment.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// This method creates a socket which can accept connections
/**
 * 
 * @author sindhu
 *
 */
public class PeerHeadConnectionHandler extends Thread {

    private List<Socket> sockets;
    private int portNumber;


    /**
     * 
     * @param sockets
     * @param portNumber
     * Creating the instances of the variables declared above
     */
    public PeerHeadConnectionHandler(List<Socket> sockets, int portNumber) {
        this.sockets = sockets;
        this.portNumber = portNumber;
    }

    public List<Socket> getSockets() {
        return sockets;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void run() {
        try {
            ServerSocket socketListener = new ServerSocket(getPortNumber());
            Socket clientSocket;

            while (true) {
                    //Accept new connections
                clientSocket = socketListener.accept();
                getSockets().add(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
