import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import tcpclient.TCPClient;


public class MyRunnable implements Runnable {
    boolean shutdown = false;             // True if client should shutdown connection
	Integer timeout = null;			     // Max time to wait for data from server (null if no limit)
    Integer limit = null;			     // Max no. of bytes to receive from server (null if no limit)
	String hostname = null;			     // Domain name of server
	int port = 0;
    Socket connectSocket;
    byte[] userInputBytes = new byte[0];
    String responseStatus = "200 OK\r\n";  
    String httpResponse = null;


    public MyRunnable (Socket connectionSocket){
        this.connectSocket = connectionSocket;

    }
   
   
    @Override
    public void run() {
         byte[] fromBrowserBuffer = new byte[2048];
        int fromBrowserLength;
        try {
            fromBrowserLength = connectSocket.getInputStream().read(fromBrowserBuffer);
            String httpRequest =  new String(fromBrowserBuffer, 0, fromBrowserLength, StandardCharsets.UTF_8);

        String[] request = httpRequest.split("\r\n");
        String[] requestLine =  request[0].split(" ");

        
        String httpVersion = requestLine[2];
        URI uri = new URI(requestLine[1]);


        if(!requestLine[0].equals("GET") || !httpVersion.equals("HTTP/1.1") ){
            responseStatus = "400 Bad Request\r\n";
            httpResponse = "HTTP/1.1" + " " + responseStatus
                        + "Content-Type: text/plain\r\n"
                        + "\r\n";

        } else if(requestLine[0].equals("GET") && uri.getPath().equals("/ask")){
                parseUrl(uri.getQuery());
                try {
                TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                byte[] serverBytes  = tcpClient.askServer(hostname, port, userInputBytes);
                String serverOutput = new String(serverBytes);
                httpResponse = buildResponse(serverOutput, serverBytes.length, httpVersion);
                } catch(IOException ex) {
                    responseStatus = "404 Not Found\r\n";
                     String fail = "404 page not found";
            
                    httpResponse = httpVersion + responseStatus
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + ex;
                        
                        connectSocket.getOutputStream().write(httpResponse.getBytes());
                        connectSocket.getOutputStream().flush();
                        connectSocket.close();
                }
        }else{
            responseStatus = "404 Not Found\r\n";
            String fail = "404 page not found";
            
            httpResponse = httpVersion+ " " +responseStatus
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + fail;
        }
    
        connectSocket.getOutputStream().write(httpResponse.getBytes());
        connectSocket.getOutputStream().flush();
            
        connectSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        
    }

    private String buildResponse(String serverOutput, int outputLength, String httpVersion){

        String response =  httpVersion + " " + responseStatus 
                            +"Content-Type: text/plain\r\n"
                            +"Content-Length:" + outputLength + "\r\n" 
                            + "\r\n"
                            + serverOutput;
        return response;
    }


    private void parseUrl(String query) throws URISyntaxException{

        String[] keyValues = query.split("&");

       for(String keyValue : keyValues){

        String[] result = keyValue.split("=");

        if(result[0].equals("hostname")){
            hostname = result[1];
        }else if(result[0].equals("port")){
            port = Integer.parseInt(result[1]);
        }else if(result[0].equals("limit")){
            limit = Integer.parseInt(result[1]);
        }
        else if(result[0].equals("timeout")){
            timeout = Integer.parseInt(result[1]);
        }else if(result[0].equals("shutdown")){
            shutdown = true;
        }else if(result[0].equals("string")){
            StringBuilder inputToServer = new StringBuilder(result[1]);
            inputToServer.append("\n");
            userInputBytes = inputToServer.toString().getBytes();
        }
       }
    }

    
    
}
