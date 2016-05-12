package edu.na1.assignment.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {


    private int portNumber;
    private String downloadDirectory;


    public Server(int portNumber) {
        this.portNumber = portNumber;
        

    }

    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void startServer() throws IOException {
        List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<Socket>());

        //Start a Thread where Server keeps accepting Client Sockets
        ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(clientSockets, getPortNumber());
        serverConnectionHandler.start();

    }

}