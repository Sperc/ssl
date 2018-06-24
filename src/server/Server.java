package server;

import javax.net.ssl.*;
import javax.security.cert.Certificate;
import java.io.*;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Server {
    public static void main(String... args) throws Throwable {
        System.setProperty("javax.net.ssl.keyStore", "za.store");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

//        System.setProperty("javax.net.ssl.trustStore", "za1.store");
//        System.setProperty("javax.net.ssl.trustStore", "za1.store");
//        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
//        System.setProperty("javax.net.ssl.trustStorePassword", "password");

//        BufferedReader reader = null;
//
//        File file = new File("pawelserver.pem");
//        System.out.println(file.canRead());
//        reader = new BufferedReader(new FileReader(file));
//        String text = null;
//        while ((text = reader.readLine()) != null) {
//            System.out.println(text);
//        }

//        Certificate[] certificates = identityKeyStore.getCertificateChain("");



        KeyStore trustStore = KeyStore.getInstance("jks");
        FileInputStream trustKeyStoreFile = new FileInputStream(new File("za1.store"));
        trustStore.load(trustKeyStoreFile, "password".toCharArray());

        KeyStore identityStore = KeyStore.getInstance("jks");
        FileInputStream identityKeyStoreFile = new FileInputStream(new File("za.store"));
        identityStore.load(identityKeyStoreFile, "password".toCharArray());


        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance("PKIX", "SunJSSE");
        trustManagerFactory.init(trustStore);

        X509TrustManager x509TrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManager;
                break;
            }
        }

        if (x509TrustManager == null) {
            throw new NullPointerException();
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        keyManagerFactory.init(identityStore, "password".toCharArray());

        X509KeyManager x509KeyManager = null;
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                x509KeyManager = (X509KeyManager) keyManager;
                break;
            }
        }

        if (x509KeyManager == null) {
            throw new NullPointerException();
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
// the final null means use the default secure random source
        sslContext.init(new KeyManager[]{x509KeyManager},
                new TrustManager[]{x509TrustManager}, null);

        SSLServerSocketFactory serverSocketFactory =
                sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket =
                (SSLServerSocket) serverSocketFactory.createServerSocket(4444);

//        ServerSocket serverSocket = ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault()).createServerSocket(4444);
        System.out.println("Server ready...");
        while (true) {

            new ServerThread(serverSocket.accept()).start();

        }
    }
}
