package edu.na1.assignment.peer;

import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.FileUtils;
import edu.na1.assignment.utils.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * This class will be responsible for connecting to Server Socket and it will receive requests from Server for File Searching.
 *
 */
public class PeerClientConnectionHandler extends Thread{

    private List<String> directoryLocationsToSearch;
    private int peerPortNumber;
    private Socket socket;
    private PrintWriter outputPrintWriter = null;
    private BufferedReader inputStream = null;
    private Thread runningThread;

// peers are connected to server
    public PeerClientConnectionHandler(Socket socket,
                                       List<String> directoryLocationsToSearch,
                                       int peerPortNumber) throws IOException {
        this.socket = socket;
        this.directoryLocationsToSearch = directoryLocationsToSearch;
        this.peerPortNumber = peerPortNumber;

        this.outputPrintWriter = new PrintWriter(this.socket.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public List<String> getDirectoryLocationsToSearch() {
        return directoryLocationsToSearch;
    }

    public int getPeerPortNumber() {
        return peerPortNumber;
    }


    @Override
    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        String resultComputed = null;
        String output = null;
        try {

            while (true) {
                //Now Server will tell us if file exists or not, based on the message decide whether to store the file or display not found
                String serverCommand = this.inputStream.readLine();

                if (StringUtils.hasText(resultComputed)
                        && resultComputed.equals(output)) {
                    this.outputPrintWriter.println(output);

                }

                if (serverCommand != null
                        && serverCommand.trim().length() > 0) {

                    final int tokenIndex = serverCommand.indexOf(Constants.RESULTS_TOKEN);

                    String actionName = serverCommand.substring(0, tokenIndex);

                    if (Constants.QUERY.equals(actionName)) {

                        final String fileName = serverCommand.substring(tokenIndex+1, serverCommand.length());

                        final File file = searchFile(fileName, getDirectoryLocationsToSearch());

                        if (file == null) {
                            //File Not found
                            output = "RESULT" + Constants.RESULTS_TOKEN + Constants.QUERY_HIT + Constants.STRING_EQUALS + Boolean.FALSE.toString();
                            resultComputed = output;
                            this.outputPrintWriter.println(output);
                        } else {
                            //Return to Server saying file is found at Server and Port number
                            output = "RESULT" +
                                    Constants.RESULTS_TOKEN +
                                    Constants.QUERY_HIT + Constants.STRING_EQUALS + Boolean.TRUE.toString() +
                                    Constants.RESULTS_TOKEN +
                                    Constants.FILE_ABS_PATH + Constants.STRING_EQUALS +
                                    file.getAbsolutePath() +
                                    Constants.RESULTS_TOKEN +
                                    Constants.IP_ADDRESS + Constants.STRING_EQUALS +
                                    InetAddress.getLocalHost().getHostAddress() +
                                    Constants.RESULTS_TOKEN +
                                    Constants.PORT_NUM + Constants.STRING_EQUALS + getPeerPortNumber();
                            resultComputed = output;
                            this.outputPrintWriter.println(output);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.outputPrintWriter.close();
        }
    }

    private File searchFile(String fileName, List<String> directories) {

        File requestedFile = null;

        try {
            requestedFile = FileUtils.searchDirectoriesForFile(directories, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out
                    .println("Exception occurred while searching files in directories");
        }
        return requestedFile;
    }
}
