package edu.na1.assignment.peer;

import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.FileUtils;
import edu.na1.assignment.utils.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
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
	
    protected Thread runningThread= null;

    private String downloadLocationOfFiles;

    private List<String> directoryLocationsToSearch;

    private int peerPortNumber;

    private String peerID;
    
    // Creating the instances of the variables declared above.....


    public Peer(int peerPortNumber, String serverIpAddress, int serverPortNumber,
                String downloadLocationOfFiles, List<String> directoryLocationsToSearch, String peerID) {
		this.serverIpAddress = serverIpAddress;
		this.serverPortNumber = serverPortNumber;
		this.downloadLocationOfFiles = downloadLocationOfFiles;
        this.directoryLocationsToSearch = directoryLocationsToSearch;
        this.peerPortNumber = peerPortNumber;
        if (StringUtils.hasText(peerID))
            this.peerID = peerID;
        else
            this.peerID = String.valueOf(this.peerPortNumber);
	}

    public List<String> getDirectoryLocationsToSearch() {
        return directoryLocationsToSearch;
    }

    public int getPeerPortNumber() {
        return peerPortNumber;
    }


    /**
     * 
     * Peer is started and tries to get serverIpAddress and serverPortNumber and sends the connection request to server.
     *
     */
	public void start() throws IOException
    {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        List<Socket> peersLst = Collections.synchronizedList(new ArrayList<Socket>());
        PrintWriter outputPrintWriter;
        BufferedReader inputStream = null;
        try {
            //Initialize Server socket, so that it can accept new requests from peers.
            PeerServerConnectionHandler peerServerConnectionHandler = new PeerServerConnectionHandler(getPeerPortNumber(), getDirectoryLocationsToSearch(), peersLst);
            peerServerConnectionHandler.setName(this.peerID);
            peerServerConnectionHandler.start();

            //Connect to Server
            Socket serverSocket = new Socket(getServerIpAddress(), getServerPortNumber());
            System.out.println(String.format("Peer Connected to IPAddress: %s , Port: %d ", getServerIpAddress(), getServerPortNumber()));
            PeerClientConnectionHandler peerClientConnectionHandler = new PeerClientConnectionHandler(serverSocket, getDirectoryLocationsToSearch(), getPeerPortNumber());
            peerClientConnectionHandler.setName(this.peerID);
            peerClientConnectionHandler.start();

            inputStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));   //Receiving
            outputPrintWriter = new PrintWriter(serverSocket.getOutputStream(), true); //Sending to Server

            //Prompt User's for entering file name so that it Calls the server and it checks against the connected members.
            //Connected Clients are tracked using clientSockets list.
            System.out.println("Enter the file name you want to search. Other Enter 'QUIT' to exit.");

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()) {
                String fileName = scanner.next().trim();


                if (Constants.QUIT.equalsIgnoreCase(fileName)) {
                    System.out.println("Searching file functionality exited.");
                    break;
                }

                //SEARCH_FILE#hello.txt
                outputPrintWriter.println(Constants.QUERY + Constants.RESULTS_TOKEN  + fileName);

                String response = inputStream.readLine();

                if (response == null)
                    response = "";

                final int tokenIndex = response.indexOf("#");

                Map<String,String> resultMap = StringUtils.getResultMap(response.substring(tokenIndex + 1, response.length()));

                boolean fileExists = Boolean.FALSE;

                if (resultMap.containsKey(Constants.QUERY_HIT)) {
                    fileExists = Boolean.valueOf(resultMap.get(Constants.QUERY_HIT));
                }

                String outputMsg = "";

                if (fileExists) {
                    outputMsg = String.format("FILE is available at IP Address: %s and Port Number: '%s' with ABS Path: '%s'",
                                                resultMap.get(Constants.IP_ADDRESS),
                                                resultMap.get(Constants.PORT_NUM),
                                                resultMap.get(Constants.FILE_ABS_PATH));

                    System.out.println(outputMsg);

                    System.out.println("If you want to Download, Enter YES otherwise Enter any key to continue.");

                    String fileDownload = scanner.next();
                    if ("YES".equalsIgnoreCase(fileDownload)) {
                        downloadFile(resultMap.get(Constants.IP_ADDRESS),
                                Integer.valueOf(resultMap.get(Constants.PORT_NUM)), fileName,
                                resultMap.get(Constants.FILE_ABS_PATH), getDownloadLocationOfFiles());
                    }
                } else {
                    outputMsg = String.format("File: '%s' is not found.",fileName);
                    System.out.println(outputMsg);
                }

                 System.out.println("Enter the file name you want to search. Other Enter 'QUIT' to exit.");
            }


        } catch(Exception e) {
            throw e;

        } finally {

        }
    }
	
	// this method downloads the file in the specified location if the file exist in one of the connected peers

    public void downloadFile(String ipAddress, int portNumber, String fileName, String fileABSPath, String downloadDirectory) throws IOException {
        BufferedReader bufferedInputReader = null;
        Socket downloadServerSocket = null;
        PrintWriter outputWriter = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            downloadServerSocket = new Socket(ipAddress, portNumber);

            outputWriter = new PrintWriter(downloadServerSocket.getOutputStream(),true);

            bufferedInputReader = new BufferedReader(new InputStreamReader(downloadServerSocket.getInputStream()));


            outputWriter.println(Constants.DOWNLOAD_FILE + Constants.RESULTS_TOKEN +
                    Constants.FILE_NAME + Constants.STRING_EQUALS + fileName
                    + Constants.RESULTS_TOKEN +
                    Constants.FILE_ABS_PATH + fileABSPath);

            String serverResponse = bufferedInputReader.readLine();

            boolean fileExists = false;

            if (Constants.QUERY_HIT.equalsIgnoreCase(serverResponse)) {

                fileExists = true;

                String decision = "YES";

                if (Constants.YES.equalsIgnoreCase(decision)) {
                    outputWriter.println(decision);
                }

            } else {
                System.out.println("FILE NOT FOUND: "+ serverResponse);
            }


            if (fileExists) {
                bufferedInputStream = new BufferedInputStream(downloadServerSocket.getInputStream());
                Path TO = Paths.get(downloadDirectory + "/" + fileName);

                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING
                };


                Files.copy(bufferedInputStream, TO, options);
                System.out.println("File Downloaded to: "+ TO.toString());
            }


        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (bufferedInputReader != null)
                    bufferedInputReader.close();

                if (bufferedInputStream != null)
                    bufferedInputStream.close();

                if (outputWriter != null)
                    outputWriter.close();

                if (downloadServerSocket != null)
                    downloadServerSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getDownloadLocationOfFiles() {
		return downloadLocationOfFiles;
	}

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public int getServerPortNumber() {
		return serverPortNumber;
	}



}
