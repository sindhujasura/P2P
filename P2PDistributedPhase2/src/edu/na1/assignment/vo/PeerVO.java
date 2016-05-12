package edu.na1.assignment.vo;

import edu.na1.assignment.peer.PeerConnectionWorker;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;


public class PeerVO {

    private String id;

    private Socket socket;

    private PrintWriter outputWriter;

    private BufferedOutputStream outputStream;

    private BufferedReader inputStream;

    private PeerConnectionWorker peerConnectionWorker;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PrintWriter getOutputWriter() {
        return outputWriter;
    }

    public void setOutputWriter(PrintWriter writer) {
        this.outputWriter = writer;
    }

    public BufferedOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(BufferedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    public void setInputStream(BufferedReader inputStream) {
        this.inputStream = inputStream;
    }

    public Socket getSocket() {

        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PeerConnectionWorker getPeerConnectionWorker() {
        return peerConnectionWorker;
    }

    public void setPeerConnectionWorker(PeerConnectionWorker peerConnectionWorker) {
        this.peerConnectionWorker = peerConnectionWorker;
    }
}
