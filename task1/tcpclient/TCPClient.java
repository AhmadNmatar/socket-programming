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
        int dataLength;
        while((dataLength = serverSegment.read(fromServerBuffer)) != -1){
            dataFromServer.write(fromServerBuffer, 0, dataLength);
        }

        clientSocket.close();
        return dataFromServer.toByteArray();
    }
}