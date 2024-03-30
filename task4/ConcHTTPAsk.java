import java.io.IOException;
import java.net.*;


public class ConcHTTPAsk {
    static int serverPort = 0;					    


    public static void main( String[] args) {
        if(args.length == 1)
        serverPort = Integer.parseInt(args[0]);


       try(ServerSocket serverSocket = new ServerSocket(serverPort)){
            while(true){
                Socket connectSocket = serverSocket.accept();

                MyRunnable runnable = new MyRunnable(connectSocket);
                Thread myThread = new Thread(runnable);
                myThread.start();
              
            }
       }catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
    }
    }
}



