package edu.na1.assignment.peer;

import edu.na1.assignment.handlers.PeerHeadConnectionHandler;
import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.StringUtils;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * 
 * @author sindhu
 *
 */

/**
This method initiates the file search query and sends the query to the connected peers to search for the file and the file is searched
in the connected peers and the result is displayed
*/

public class PeerHead {
   
	private int portNumber;
    private String downloadDirectory;

    public PeerHead(int portNumber, String downloadDirectory) {
        this.portNumber = portNumber;
        this.downloadDirectory = downloadDirectory;

    }

    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void startServer() throws IOException {
        List<Socket> clientSockets = new ArrayList<>();

        //Start a Thread where Server keeps accepting Client Sockets
        PeerHeadConnectionHandler peerHeadConnectionHandler = new PeerHeadConnectionHandler(clientSockets, getPortNumber());
        peerHeadConnectionHandler.start();

        //Prompt User's for entering file name so that it can be searched across connected clients.
        //Connected Clients are tracked using clientSockets list.

        StringUtils.printMsgToConsole("Enter the file name you want to search.");

        Scanner scanner = new Scanner(System.in);

        //Keep Accepting the file names for search

        while (scanner.hasNext()) {
            String fileName = scanner.next().trim();

            if ("QUIT".equalsIgnoreCase(fileName)) {
            	StringUtils.printMsgToConsole("Exiting File Search Process.");
                break;
            }

            //Search the file across connected Clients
            if (clientSockets.size() == 0) {
                StringUtils.printMsgToConsole("No Active Connections found at Server.");
            }

            String outputMsg = "";
            boolean fileExistInPeers = Boolean.FALSE;

            for (Socket clientSocket:clientSockets) {
                try {
                    //Send command to each client via client Socket
//                   
                    BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                    printWriter.println(Constants.SEARCH_FILE + Constants.RESULTS_TOKEN + fileName); //Forwarding Query to Peer

                    String response = inputBufferedReader.readLine();

                    if (StringUtils.hasText(response)) {
                        final int tokenIndex = response.indexOf(Constants.RESULTS_TOKEN);
                        String command = response.substring(0, tokenIndex);

                        if (Constants.RESULTS.equals(command)) {
                            Map<String,String> resultMap = StringUtils.getResultMap(response.substring(tokenIndex+1, response.length()));
                            //Read the response form Peer
                            boolean fileExists = Boolean.FALSE;

                            if (resultMap.containsKey(Constants.FILE_FOUND)) {
                                fileExists = Boolean.valueOf(resultMap.get(Constants.FILE_FOUND));
                            }

                            if (fileExists) {
                                //File is found.. don't search other peer's.
                                //Send Peer Address and Port Number.
                                outputMsg = String.format("FILE is available at IP Address: '%s' with ABS Path: '%s'",
                                                            resultMap.get(Constants.IP_ADDRESS),
                                                            resultMap.get(Constants.FILE_ABS_PATH));

                                StringUtils.printMsgToConsole(outputMsg);
                                StringUtils.printMsgToConsole("If you want to Download, Enter YES otherwise Enter any key to continue.");
                                
                                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                                String fileDownload = userInput.readLine();

                                if (Constants.YES.equalsIgnoreCase(fileDownload)) {
                                    downloadFile(resultMap.get(Constants.IP_ADDRESS),
                                                 Integer.valueOf(resultMap.get(Constants.PORT_NUM)), fileName,
                                                 resultMap.get(Constants.FILE_ABS_PATH), getDownloadDirectory());
                                }

                                fileExistInPeers = Boolean.TRUE;
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    throw e;
                }
            }

            if(!fileExistInPeers) {
                StringUtils.printMsgToConsole(String.format("File: '%s' is not found in any of the connected Peers.", fileName));
            }
            
            StringUtils.printMsgToConsole("If you want to search another file, Please enter or type QUIT to exit");

        }
    }

    public void downloadFile(String ipAddress, int portNumber, String fileName, String fileABSPath, String downloadDirectory) throws IOException {

        try {
            Socket downloadServerSocket = new Socket(ipAddress, portNumber);

            PrintWriter outputWriter = new PrintWriter(downloadServerSocket.getOutputStream(),true);

            BufferedReader bufferedInputReader = new BufferedReader(new InputStreamReader(downloadServerSocket.getInputStream()));


            outputWriter.println(Constants.DOWNLOAD_FILE + Constants.RESULTS_TOKEN +
                                                    Constants.FILE_NAME + Constants.STRING_EQUALS + fileName
                                                 + Constants.RESULTS_TOKEN +
                                                    Constants.FILE_ABS_PATH + fileABSPath);

            String serverResponse = bufferedInputReader.readLine();

            boolean fileExists = false;

            if (Constants.FILE_FOUND.equalsIgnoreCase(serverResponse)) {
                fileExists = true;
//                StringUtils.printMsgToConsole("File Exists in IP: '"+ ipAddress +"', Do you want to download it? If Yes, Enter YES");
                outputWriter.println(Constants.YES);
            } else {
                StringUtils.printMsgToConsole(serverResponse);
            }

            if (fileExists) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(downloadServerSocket.getInputStream());
                Path TO = Paths.get(downloadDirectory + "/" + fileName);

                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING
                };


                Files.copy(bufferedInputStream, TO, options);
                StringUtils.printMsgToConsole("File Downloaded: "+TO.toString());
                
                downloadServerSocket.close();                
            }


        } catch (Exception e) {
            throw e;
        }
    }

}