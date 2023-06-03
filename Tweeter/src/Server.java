import io.jsonwebtoken.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws SQLException {
        //initializing server and database
        Database database=new Database();
        database.createTables();
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