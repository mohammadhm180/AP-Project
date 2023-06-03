import com.mysql.cj.exceptions.WrongArgumentException;
import io.jsonwebtoken.io.IOException;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws java.io.IOException, ClassNotFoundException {
        Socket server = null;
        String token = null;
        File file = new File("UserInfo/token.bin");
        try {
            server = new Socket("127.0.0.1", 8000);
        } catch (IOException | java.io.IOException exception) {
            exception.printStackTrace();
        }
        ObjectOutputStream OOS=new ObjectOutputStream(server.getOutputStream());
        ObjectInputStream OIS=new ObjectInputStream(server.getInputStream());
        EnterProgram enterProgram=new EnterProgram(file,OOS,OIS);
        Menu menu=new Menu(enterProgram,OOS,OIS);
        //checking if token already exist in local file
        if (file.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            token = (String) objectInputStream.readObject();
            objectInputStream.close();
        }
        if (token == null) {
            //we do not have a token in this case
            //here sign up or log in is done
            enterProgram.enter();
        } else {
            OOS.writeObject(new String("checkToken"));
            OOS.writeObject(token);
            String result=(String) OIS.readObject();
            if(result.equals("success")){
                User client=(User) OIS.readObject();
                menu.setClient(client);
                menu.setToken(token);
            } else {
                //the token is invalid
                //here sign up or log in is done
                enterProgram.enter();
            }
        }
        menu.startMenu();
    }


}

class Menu {
    String token;
    User client;
    EnterProgram enterProgram;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    public Menu(EnterProgram enterProgram,ObjectOutputStream OOS,ObjectInputStream OIS) {
        this.token = null;
        this.client = null;
        this.enterProgram=enterProgram;
        this.OIS=OIS;
        this.OOS=OOS;
        enterProgram.setMenu(this);
    }

    public void startMenu() {
        Scanner scanner = new Scanner(System.in);
        String choice;
        while (true) {
            System.out.println("1-show timeline\n2-        \n10-exit");
            choice = scanner.nextLine();
            if (choice.equals("1")) {

            } else if (choice.equals("10")) {
                break;
            }
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setClient(User client) {
        this.client = client;
    }
}
class EnterProgram{
    File file;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    Menu menu;

    public EnterProgram(File file, ObjectOutputStream OOS, ObjectInputStream OIS) {
        this.file=file;
        this.OOS=OOS;
        this.OIS=OIS;
        this.menu=null;
    }

    public void enter() throws java.io.IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (true) {
            System.out.println("1-signUp\n2-login");
            choice = scanner.nextLine();
            if (choice.equals("1")) {
                try {
                    System.out.println("enter username");
                    String username = scanner.nextLine();
                    System.out.println("enter firstname");
                    String firstname = scanner.nextLine();
                    System.out.println("enter lastname");
                    String lastname = scanner.nextLine();
                    System.out.println("enter email");
                    String email = scanner.nextLine();
                    System.out.println("enter phoneNumber");
                    String phoneNumber = scanner.nextLine();
                    System.out.println("enter password");
                    String password = scanner.nextLine();
                    System.out.println("enter your country");
                    String country = scanner.nextLine();
                    System.out.println("enter your birthdate(**** ** **)");
                    LocalDateTime birthdate = LocalDateTime.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), 0, 0);
                    scanner.nextLine();
                    LocalDateTime signUpDate = LocalDateTime.now();
                    User client = new User(username, firstname, lastname, email, phoneNumber, password, country, birthdate, signUpDate);
                    //sending request
                    OOS.writeObject(new String("signUp"));
                    //sending user
                    OOS.writeObject(client);
                    //getting result
                    String result = (String) OIS.readObject();
                    if (result.equals("success")) {
                        //getting token
                        String token = (String) OIS.readObject();
                        //update menu
                        menu.setToken(token);
                        menu.setClient(client);
                        //save token to local directory
                        file.getParentFile().mkdirs();
                        ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(file));
                        objectOutputStream.writeObject(token);
                        objectOutputStream.close();
                        break;
                    } else {
                        System.out.println(result);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("wrong input");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (java.io.IOException e){
                    e.printStackTrace();
                }
            } else if (choice.equals("2")) {
                System.out.println("enter username");
                String username = scanner.nextLine();
                System.out.println("enter password");
                String password = scanner.nextLine();
                //sending request
                OOS.writeObject(new String("signIn"));
                OOS.writeObject(username);
                OOS.writeObject(password);
                //getting result
                String result = (String) OIS.readObject();
                if (result.equals("success")) {
                    String token = (String) OIS.readObject();
                    User client = (User) OIS.readObject();
                    //update menu
                    menu.setToken(token);
                    menu.setClient(client);
                    //save token to local directory
                    file.getParentFile().mkdirs();
                    ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(file));
                    objectOutputStream.writeObject(token);
                    objectOutputStream.close();
                    break;
                } else {
                    System.out.println(result);
                }
            } else {
                System.out.println("wrong input");
            }
        }
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}