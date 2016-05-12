package edu.na1.assignment.server;


import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for serving individual Connected Peer Socket to the server
 */
public class ServerConnectionWorker extends Thread{
    private Socket clientSocket;
    private List<Socket> connectedSocketsList;
    private PrintWriter outputPrintWriter = null;
    private BufferedReader inputStream = null;
    private Thread runningThread;


    public ServerConnectionWorker(Socket clientSocket, List<Socket> sockets) throws IOException {
        this.clientSocket = clientSocket;
        this.connectedSocketsList = sockets;

        this.outputPrintWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }


    public List<Socket> getConnectedSocketsList() {
        return connectedSocketsList;
    }

    @Override
    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        try {

            while (true) {

                String request = inputStream.readLine();


                if (StringUtils.hasText(request)
                        && request.contains(Constants.QUERY)) {

                    //Search the file across connected Clients
                    if (this.connectedSocketsList == null
                            || this.connectedSocketsList.size() == 0) {
                        this.outputPrintWriter.println(Constants.RESULTS + Constants.RESULTS_TOKEN + "NO_ACTIVE_CONNECTIONS_FOUND");
                    }

                    String fileName = StringUtils.getFileName(request);

                    List<Socket> socketList = getConnectedSocketsList();

                    for (int i = 0; i < socketList.size(); i++) {

                        Socket socket = socketList.get(i);

                        if (socket != null
                                && socket.isConnected()
                                && !socket.equals(this.clientSocket) //make sure it don't search the client from which request is originated
                                && StringUtils.hasText(fileName)) {

                            //Send command to each client via client Socket
                            System.out.println("calling Client: " + clientSocket.getRemoteSocketAddress().toString() + " for searching file: "+fileName);

                            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            //Sending Request to socket
                            printWriter.println(Constants.QUERY + "#" + fileName);

                            //Reading response from socket
                            request = inputBufferedReader.readLine();

                            if (StringUtils.hasText(request)
                                    && request.contains(Constants.RESULTS)) {
                                verifyResultsAndSend(request, Boolean.FALSE);
                            }

                            if (i == socketList.size()-1){
                                verifyResultsAndSend(request, Boolean.TRUE);
                            }
                        }
                    }
                }

                if (StringUtils.hasText(request)
                        && request.contains(Constants.RESULTS)) {
                    verifyResultsAndSend(request, Boolean.TRUE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {

        }

    }

    private void verifyResultsAndSend(String request, Boolean sendResults) {
        boolean fileExists = false;
        final int tokenIndex = request.indexOf("#");

        String command = request.substring(0, tokenIndex);

        if (Constants.RESULTS.equalsIgnoreCase(command)) {
            Map<String,String> resultMap = StringUtils.getResultMap(request.substring(tokenIndex + 1, request.length()));

            //Read the response form Peer
            if (resultMap.containsKey(Constants.QUERY_HIT)) {
                fileExists = Boolean.valueOf(resultMap.get(Constants.QUERY_HIT));
            }

            if (fileExists || sendResults) {
                outputPrintWriter.println(request);
            }
        }
    }

}
