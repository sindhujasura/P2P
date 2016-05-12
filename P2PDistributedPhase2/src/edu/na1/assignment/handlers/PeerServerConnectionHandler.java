package edu.na1.assignment.handlers;

import edu.na1.assignment.peer.PeerConnectionWorker;
import edu.na1.assignment.vo.PeerVO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/* This method maintains a list of connected peers....
 * 
 */
public class PeerServerConnectionHandler extends Thread{

    private int portNumber;

    public List<PeerVO> getPeerVOs() {
        return peerVOs;
    }

    private List<PeerVO> peerVOs;

    private List<String> directoryLocationsToSearch;

    public PeerServerConnectionHandler(int portNumber, List<String> directoryLocationsToSearch, List<PeerVO> peerVOs) {
        this.portNumber = portNumber;
        this.peerVOs = peerVOs;
        this.directoryLocationsToSearch = directoryLocationsToSearch;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void run() {
        Socket connectionSocket;

        try {
            ServerSocket serverSocket = new ServerSocket(getPortNumber());
            //Accept new connections
            connectionSocket = serverSocket.accept();

            //Start a new Thread to serve the Requests for connectionSocket
            PeerVO peerVO = new PeerVO();
            peerVO.setSocket(connectionSocket);

            try {
                PrintWriter writer = new PrintWriter(connectionSocket.getOutputStream(), true);
                BufferedOutputStream outputStream = new BufferedOutputStream(connectionSocket.getOutputStream());
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                peerVO.setOutputWriter(writer);
                peerVO.setOutputStream(outputStream);
                peerVO.setInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            PeerConnectionWorker peerConnectionWorker = new PeerConnectionWorker(peerVO, directoryLocationsToSearch);
            peerVO.setPeerConnectionWorker(peerConnectionWorker);

            if (getPeerVOs() == null) {
                peerConnectionWorker.start();
            } else {
                //Add it to List
                getPeerVOs().add(peerVO);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
