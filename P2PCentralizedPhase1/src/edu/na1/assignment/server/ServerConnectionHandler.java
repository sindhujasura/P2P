package edu.na1.assignment.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * This class is responsible for accepting all client connections and adding them to sockets list.
 */
public class ServerConnectionHandler extends Thread {

    private List<Socket> sockets;
    private int portNumber;
    private Thread runningThread;


    public ServerConnectionHandler(List<Socket> sockets, int portNumber) {
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
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        try {
            ServerSocket socketListener = new ServerSocket(getPortNumber());
            Socket clientSocket;

            while (true) {
                    //Accept new connections
                clientSocket = socketListener.accept();

                ServerConnectionWorker serverConnectionWorker = new ServerConnectionWorker(clientSocket, getSockets());
                serverConnectionWorker.setName(clientSocket.getRemoteSocketAddress().toString());
                serverConnectionWorker.start();

                getSockets().add(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
