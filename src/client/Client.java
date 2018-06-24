package client;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class Client {
    public static void main(String... args) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", "za.store");
//        System.setProperty("javax.net.ssl.trustStore", "za1.store");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        System.setProperty("javax.net.ssl.keyStore", "za1.store");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        Socket socket = SSLSocketFactory.getDefault().createSocket("localhost", 4444);
        SSLSession session = ((SSLSocket) socket).getSession();
        Certificate[] cchain = session.getPeerCertificates();
        System.out.println("The Certificates used by peer");
        for (int i = 0; i < cchain.length; i++) {
            System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
        }
        System.out.println("Peer host is " + session.getPeerHost());
        System.out.println("Cipher is " + session.getCipherSuite());
        System.out.println("Protocol is " + session.getProtocol());
        System.out.println("ID is " + new BigInteger(session.getId()));
        System.out.println("Session created in " + session.getCreationTime());
        System.out.println("Session accessed in " + session.getLastAccessedTime());

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader commandPrompt = new BufferedReader(new InputStreamReader(System.in));
        String message = null;

        //wysylanie pliku na serwer
//        File file = new File("za1.store");
//        byte[] bytes = new byte[(int) file.length()];
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
//        bufferedInputStream.read(bytes, 0, bytes.length);
//        OutputStream os = socket.getOutputStream();
//        os.write(bytes);
//        os.flush();

        while (true) {
            System.out.println("Wpisz wiadomosc");
            message = commandPrompt.readLine();
            if (message.equals("quit")) {
                socket.close();
                break;
            }
            out.println(message);
            System.out.print("From Server: ");
            System.out.println(in.readLine());
        }
    }
}
