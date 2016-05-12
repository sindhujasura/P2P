package edu.na1.assignment.test;

import java.io.IOException;

import edu.na1.assignment.server.Server;

// Port number for server is initialized and instance for server class is created and server is started.
public class TestServer {

    public static final int SERVER_PORT_NUMBER = 8085;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

       
         Server server = new Server(SERVER_PORT_NUMBER );

        server.startServer();

	}

}
