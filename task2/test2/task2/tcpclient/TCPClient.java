package tcpclient;
import java.net.*;
import java.io.*;


public class TCPClient {
    boolean shutdown;
    Integer timeout;
    Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException{
        
        Socket clientSocket = null;
        OutputStream clientOutputStream = null;
        InputStream serverInputStream = null;
        ByteArrayOutputStream dataFromServer = null;

        try {
            clientSocket = new Socket(hostname, port);
            clientOutputStream = clientSocket.getOutputStream();
            clientOutputStream.write(toServerBytes);
    
            if (shutdown) {
                clientSocket.shutdownOutput();
            }
    
            serverInputStream = clientSocket.getInputStream();
            dataFromServer = new ByteArrayOutputStream();
            
            if(timeout != null)
                clientSocket.setSoTimeout(timeout);

            byte[] fromServerBuffer = new byte[2048];
            int bytesRead;
            try{
            while (((bytesRead = serverInputStream.read(fromServerBuffer)) != -1)) {
                    if (limit != null) {
                        dataFromServer.write(fromServerBuffer, 0, Math.min(limit - dataFromServer.size(), bytesRead));
                    } else {
                        dataFromServer.write(fromServerBuffer, 0, bytesRead);
                    }

                    if((this.limit != null && dataFromServer.size() >= this.limit)){
                        break;
                    }

           
        } }catch (SocketTimeoutException e){
            if (clientOutputStream != null) clientOutputStream.close();
            serverInputStream.close();
            clientSocket.close();
            return dataFromServer.toByteArray();
        }
        
        } finally {
            if (clientOutputStream != null) clientOutputStream.close();
            if (serverInputStream != null) serverInputStream.close();
            if (clientSocket != null) clientSocket.close();
        }
    
        return dataFromServer.toByteArray();
    }
}