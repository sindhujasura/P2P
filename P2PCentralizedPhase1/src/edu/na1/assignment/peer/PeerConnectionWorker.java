package edu.na1.assignment.peer;

import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.FileUtils;
import edu.na1.assignment.utils.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * This class will serve the required function's for incoming connections to Peer (As a Server)
 * For Example: It will be used for transferring the file.
 */
public class PeerConnectionWorker extends Thread{

    private Socket connectionSocket;
    private List<String> directoryLocationsToSearch;

    PrintWriter outputPrintWriter = null;
    BufferedOutputStream outputStream = null;
    BufferedReader inputStream = null;
    private Thread runningThread;


    public PeerConnectionWorker(Socket connectionSocket, List<String> directoryLocationsToSearch) {
        this.connectionSocket = connectionSocket;
        this.directoryLocationsToSearch = directoryLocationsToSearch;

        try {
            outputStream = new BufferedOutputStream(connectionSocket.getOutputStream());

            outputPrintWriter = new PrintWriter(connectionSocket.getOutputStream(), true);

            inputStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        try {

            System.out.println("Connection established with IP: " + connectionSocket.getRemoteSocketAddress());

 //Now Server will tell us if file exists or not, based on the message decide whether to store the file or display not found
            String serverRequestCommand = inputStream.readLine();

            if (serverRequestCommand == null) {
                serverRequestCommand = "";
            }

            final int tokenIndex = serverRequestCommand.indexOf(Constants.RESULTS_TOKEN);

            final String commandName = serverRequestCommand.substring(0, tokenIndex);

            if (Constants.DOWNLOAD_FILE.equals(commandName)) {

                final Map<String,String> requestMap = StringUtils.getResultMap(serverRequestCommand.substring(tokenIndex + 1,
                                                                                 serverRequestCommand.length()));

                final String fileName = requestMap.get(Constants.FILE_NAME);
                final String fileABSPath = requestMap.get(Constants.FILE_ABS_PATH);

                File file;

                if (StringUtils.hasText(fileABSPath)) {
                    //Use File ABS PATH
                    file = new File(fileABSPath);
                } else {
                    //Search file w.r.t fileName
                    file = FileUtils.searchDirectoriesForFile(this.directoryLocationsToSearch, fileName);
                }


                //First Send a notification to client saying File is found
                boolean fileFound = false;
                String msg = null;

                if (file != null && file.isFile()) {
                    fileFound = true;
                    msg = Constants.QUERY_HIT;
                } else {
                    msg = String.format("File: '%s' not found in server, Do you want to search in other clients? If Yes, Enter YES", fileName);

                }

                outputPrintWriter.println(msg);

                String sendFile = inputStream.readLine();
                //if client sends acceptance to download the file the file will be download to the respective file.

                if (Constants.YES.equalsIgnoreCase(sendFile)) {

                    if (fileFound) {

                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] buf = new byte[1024];
                        int read;
                        while ((read = fileInputStream.read(buf, 0, 1024)) != -1) {
                            outputStream.write(buf, 0, read);
                            outputStream.flush();
                        }
                        fileInputStream.close();
                        System.out.println("File transferred");
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Accept failed");
            System.exit(1);
        } finally {
            try {
                if (connectionSocket != null) {
                    connectionSocket.close();
                }

                if (outputPrintWriter != null) {
                    outputPrintWriter.flush();
                    outputPrintWriter.close();
                }

                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close()
                ;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
