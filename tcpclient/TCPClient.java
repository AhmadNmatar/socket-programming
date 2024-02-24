package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
     
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {


        Socket clientSocket = new Socket(hostname, port);
        OutputStream clientSegment = clientSocket.getOutputStream();


        clientSegment.write(toServerBytes);


        InputStream serverSegment =  clientSocket.getInputStream();
        byte [] fromServerBuffer = new byte[2048];
        ByteArrayOutputStream dataFromServer = new ByteArrayOutputStream();
        while((serverSegment.read(fromServerBuffer)) != -1){
            dataFromServer.write(fromServerBuffer);
        }

        byte[] response = dataFromServer.toByteArray();

        // Close the streams and socket
        //dataFromServer.close();
        //serverSegment.close();
        //clientSegment.close();


        clientSocket.close();

        return   dataFromServer.toByteArray();
    }

    public byte[] askServer(String hostname, int port) throws IOException{
        return askServer(hostname, port, new byte[1024]);
    }
}