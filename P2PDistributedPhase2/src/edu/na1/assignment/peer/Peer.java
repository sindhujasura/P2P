package edu.na1.assignment.peer;

import edu.na1.assignment.handlers.PeerServerConnectionHandler;
import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.FileUtils;
import edu.na1.assignment.utils.StringUtils;
import edu.na1.assignment.vo.PeerVO;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


/**
 * 
 * @author sindhu
 *
 */
public class Peer
{
	private String serverIpAddress;
	
	private int serverPortNumber;

   private List<String> directoryLocationsToSearch;

    private int peerPortNumber;

    private int downloadPort;

    private String peerId;

    public Peer(int peerPortNumber, String serverIpAddress, int serverPortNumber,
                List<String> directoryLocationsToSearch, String peerId, int downloadPort) {
		this.serverIpAddress = serverIpAddress;
		this.serverPortNumber = serverPortNumber;
        this.directoryLocationsToSearch = directoryLocationsToSearch;
        this.peerPortNumber = peerPortNumber;
        this.downloadPort = downloadPort;
        if (StringUtils.hasText(peerId))
            this.peerId = peerId;
        else
            this.peerId = String.valueOf(peerPortNumber);
	}

    public int getDownloadPort() {
        return downloadPort;
    }

    public List<String> getDirectoryLocationsToSearch() {
        return directoryLocationsToSearch;
    }

    public int getPeerPortNumber() {
        return peerPortNumber;
    }

    /**
     * 
     * Client is started and tries to get serverIpAddress and serverPortNumber and sends the connection request to server.
     *
     */
	public void start() throws IOException
    {
        Socket serverSocket = null;
        PrintWriter outputPrintWriter;
        FileOutputStream  outputStream = null;
        BufferedReader inputStream = null;
        List<PeerVO> peerVOList = Collections.synchronizedList(new ArrayList<PeerVO>());
        try {
            //Initialize Server socket, so that it can accept new requests.
            PeerServerConnectionHandler peerServerConnectionHandler = new PeerServerConnectionHandler(getPeerPortNumber(), getDirectoryLocationsToSearch(), peerVOList);
            peerServerConnectionHandler.start();

            //Initialize Server socket for serving downloads, so that it can accept new requests.
            PeerServerConnectionHandler downloadConnectionHandler = new PeerServerConnectionHandler(getDownloadPort(), getDirectoryLocationsToSearch(), null);
            downloadConnectionHandler.start();

            //Connect to Server
            serverSocket = new Socket(getServerIpAddress(), getServerPortNumber());

            StringUtils.printMsgToConsole(String.format("Peer Connected to IPAddress: %s , Port: %d ", getServerIpAddress(), getServerPortNumber()));

            inputStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            outputPrintWriter = new PrintWriter(serverSocket.getOutputStream(), true);

            while (true) {
                //Now Server will tell us if file exists or not, based on the message decide whether to store the file or display not found
                String serverRequestCommand = inputStream.readLine();

                if (serverRequestCommand != null
                        && serverRequestCommand.trim().length() > 0) {

                    final int tokenIndex = serverRequestCommand.indexOf(Constants.RESULTS_TOKEN);

                    String actionName = serverRequestCommand.substring(0, tokenIndex);

                    if (Constants.SEARCH_FILE.equals(actionName)) {
                        String fileName = serverRequestCommand.substring(tokenIndex+1, serverRequestCommand.length());

                        File file = searchFile(fileName, getDirectoryLocationsToSearch());
                        
                        if (file != null) {
                        	StringUtils.printMsgToConsole(String.format("File Name: '%s' found at Peer: '%s'", fileName, this.peerId));
                        } else {
                        	StringUtils.printMsgToConsole(String.format("File Name: '%s' not found at Peer: '%s'", fileName, this.peerId));
                        }

                        if (file == null) {
                            //File Not found, Check file in connected peers.
                            if (peerVOList != null
                                    && peerVOList.size() > 0) {
                                boolean fileExist = Boolean.FALSE;
                                String response = "";

                                for (PeerVO peerVO : peerVOList) {
                                    if (peerVO.getSocket() != null
                                            && peerVO.getSocket().isConnected()) {

                                        BufferedReader peerReader = peerVO.getInputStream();
                                        PrintWriter peerWriter = peerVO.getOutputWriter();
                                        
                                        peerWriter.println(serverRequestCommand); //Forwarding the query to connected peers
                                        response = peerReader.readLine();

                                        if (StringUtils.hasText(response)
                                                && response.contains(Constants.RESULTS)) {

                                            final int responseTokenIndex = response.indexOf(Constants.RESULTS_TOKEN);

                                            Map<String, String> responseMap = StringUtils.getResultMap(response.substring(responseTokenIndex + 1,
                                                                                                       response.length()));

                                            //file found in the socket.. then return, otherwise continue
                                            if (responseMap.containsKey(Constants.FILE_FOUND)
                                                    && Boolean.valueOf(responseMap.get(Constants.FILE_FOUND)) == Boolean.TRUE) {
                                                fileExist = Boolean.TRUE;
                                            }

                                            if (fileExist) {
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (fileExist) {
                                    outputPrintWriter.println(response);
                                } else {
                                    sendFileNotFoundMsg(outputPrintWriter);
                                }
                            } else {
                                sendFileNotFoundMsg(outputPrintWriter);
                            }

                        } else {
                            //Return to Server saying file is found at Server and Port number
                            sendFileFoundMsg(outputPrintWriter, file);                         
                        }
                    }
                }
            }
        } catch(Exception e) {
            throw e;
        } finally {

        }
    }
/*
 * File found message will be displayed indicating the path of the file
 */
	

    private void sendFileFoundMsg(PrintWriter outputPrintWriter, File file) throws UnknownHostException {
        outputPrintWriter.println(Constants.RESULTS+
                Constants.RESULTS_TOKEN +
                Constants.FILE_FOUND + Constants.STRING_EQUALS + Boolean.TRUE.toString() +
                Constants.RESULTS_TOKEN +
                Constants.FILE_ABS_PATH + Constants.STRING_EQUALS +
                file.getAbsolutePath() +
                Constants.RESULTS_TOKEN +
                Constants.IP_ADDRESS + Constants.STRING_EQUALS +
                InetAddress.getLocalHost().getHostAddress() +
                Constants.RESULTS_TOKEN +
                Constants.PORT_NUM + Constants.STRING_EQUALS + getDownloadPort());
    }
    /*
     * File not found message will be displayed
     */

    private void sendFileNotFoundMsg(PrintWriter outputPrintWriter) {
        outputPrintWriter.println(Constants.RESULTS + Constants.RESULTS_TOKEN + Constants.FILE_FOUND + Constants.STRING_EQUALS + Boolean.FALSE.toString());
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

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public int getServerPortNumber() {
		return serverPortNumber;
	}
}
