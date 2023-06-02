import io.jsonwebtoken.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket server=null;
        try {
            server = new Socket("127.0.0.1", 8000);
        } catch (IOException | java.io.IOException exception) {
            exception.printStackTrace();
        }
        //check token here
        String token = null;

        //if null you go to sign up or log in else show menu


        //here sign up or log in is done

        //

        Menu menu=new Menu(token,server);
        menu.startMenu();
    }


}
class Menu{
    String token;
    Socket server;

    public Menu(String token, Socket server) {
        this.token = token;
        this.server = server;
    }
    public void startMenu(){
        Scanner scanner=new Scanner(System.in);
        String choice;
        while (true){
            System.out.println("1-show timeline\n2-        \n10-exit");
            choice=scanner.nextLine();
            if(choice.equals(1)){

            }







           else if(choice.equals("10")){
                break;
            }
        }
    }
}