package server;

import com.sun.jndi.cosnaming.ExceptionMapper;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) throws Throwable {
        this.socket = socket;
    }

    public void vertificateCert() throws Throwable {
        KeyStore trustStore = KeyStore.getInstance("jks");
        FileInputStream trustKeyStoreFile = new FileInputStream(new File("za1.store"));
        trustStore.load(trustKeyStoreFile, "password".toCharArray());
        java.security.cert.Certificate certificate = trustStore.getCertificate(trustStore.aliases().nextElement());

        KeyStore identityStore = KeyStore.getInstance("jks");
        FileInputStream identityKeyStoreFile = new FileInputStream(new File("za.store"));
        identityStore.load(identityKeyStoreFile, "password".toCharArray());
        Certificate identityCert = identityStore.getCertificate(identityStore.aliases().nextElement());


        try {
            certificate.verify(identityCert.getPublicKey());
        } catch (Exception ex) {
            this.finalize();
        }
        System.out.println(certificate.toString());
    }


    @Override
    public void run() {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//            byte[] mybytearray = new byte[1024 * 100];
//            InputStream is = socket.getInputStream();
//            FileOutputStream fos = new FileOutputStream("client.store");
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            int bytesRead = is.read(mybytearray, 0, mybytearray.length);
//            bos.write(mybytearray, 0, bytesRead);

            vertificateCert();

            while (true) {
                printWriter.println(bufferedReader.readLine());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = "
                + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = "
                + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = " + ss.getCipherSuite());
        System.out.println("   Protocol = " + ss.getProtocol());
    }
}
