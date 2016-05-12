package edu.na1.assignment.peer;

import edu.na1.assignment.utils.Constants;
import edu.na1.assignment.utils.FileUtils;
import edu.na1.assignment.utils.StringUtils;
import edu.na1.assignment.vo.PeerVO;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * This class will serve the required function's for incoming connections to Peer (As a Server)
 * 
 * Searches for the file in the peer 
 */
public class PeerConnectionWorker extends Thread{

    private Socket connectionSocket;
    private List<String> directoryLocationsToSearch;

    private PrintWriter writer;

    private BufferedOutputStream outputStream;

    private BufferedReader inputStream;


    public PeerConnectionWorker(PeerVO peerVO, List<String> directoryLocationsToSearch) {
        this.connectionSocket = peerVO.getSocket();

        this.directoryLocationsToSearch = directoryLocationsToSearch;

        this.writer = peerVO.getOutputWriter();

        this.inputStream = peerVO.getInputStream();

        this.outputStream = peerVO.getOutputStream();
    }

    @Override
    public void run() {
        try {

           StringUtils.printMsgToConsole("Connection established with IP: " + connectionSocket.getRemoteSocketAddress());


            //Now Server will tell us if file exists or not, based on the message decide whether to store the file or display not found
            String serverRequestCommand = inputStream.readLine();

            final int tokenIndex = serverRequestCommand.indexOf(Constants.RESULTS_TOKEN);

            final String commandName = serverRequestCommand.substring(0, tokenIndex);

            if (Constants.DOWNLOAD_FILE.equals(commandName)) {

                Map<String,String> requestMap = StringUtils.getResultMap(serverRequestCommand.substring(tokenIndex + 1,
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
                    msg = Constants.FILE_FOUND;
                } else {
                    msg = String.format("File: '%s' not found in server, Do you want to search in other clients? If Yes, Enter YES", fileName);

                }

                writer.println(msg);

                String sendFile = inputStream.readLine();
                //if client sends acceptance to download the file the file will be download to the respective file.

                if (Constants.YES.equalsIgnoreCase(sendFile)) {

                    if (fileFound) {

                        FileInputStream fis = new FileInputStream(file);
                        byte[] buf = new byte[1024];
                        int read;
                        while ((read = fis.read(buf, 0, 1024)) != -1) {
                            outputStream.write(buf, 0, read);
                            outputStream.flush();
                        }

                        fis.close();
                        outputStream.close();
                        inputStream.close();
                        connectionSocket.close();
                       StringUtils.printMsgToConsole("File transferred");
                    }
                }
            }


        } catch (Exception e) {
           StringUtils.printMsgToConsole("Accept failed");
            System.exit(1);
        }
    }
}
