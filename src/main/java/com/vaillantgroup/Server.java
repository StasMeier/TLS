package com.vaillantgroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        try {
            server.start(8082);
        } finally {
            server.stop();
        }
    }

    public void start(int port) throws IOException {

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\stmeier\\IdeaProjects\\TLS\\src\\main\\resources\\keystore.jks");
        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\stmeier\\IdeaProjects\\TLS\\src\\main\\resources\\keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "cacerts");
        System.setProperty("jdk.tls.server.protocols", "TLSv1.3");

        serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;
        String tlsVersion = ((SSLSocket) clientSocket).getSession().getProtocol();
        System.out.println(tlsVersion);
        while ((message = in.readLine()) != null) {
            System.out.println(message);
            out.println(message);
        }
    }

    public static SSLServerSocketFactory getSSLSocketFactory(KeyStore trustKey, String sslAlgorithm) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustKey);

            SSLContext context = SSLContext.getInstance(sslAlgorithm);//"SSL" "TLS"
            context.init(null, tmf.getTrustManagers(), null);

            return context.getServerSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}