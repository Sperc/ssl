package server;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String... args) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "za.store");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        ServerSocket serverSocket = ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault()).createServerSocket(4444);
        System.out.println("Server ready...");
        while (true) {
            new ServerThread(serverSocket.accept()).start();
        }
    }
}
