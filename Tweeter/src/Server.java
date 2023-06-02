import io.jsonwebtoken.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static void main(String[] args) {
        //initializing server and database
        Database database=new Database();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8000);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        ExecutorService service= Executors.newCachedThreadPool();
        //getting client
        while (true){
            try {
                Socket client = serverSocket.accept();
                service.execute(new ClientThread(client,database));
            } catch (IOException | java.io.IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}