import io.jsonwebtoken.io.IOException;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
/**/
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
            OOS.writeObject(new String("checkSignInToken"));
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
    User user;
    EnterProgram enterProgram;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    public Menu(EnterProgram enterProgram,ObjectOutputStream OOS,ObjectInputStream OIS) {
        this.token = null;
        this.user = null;
        this.enterProgram=enterProgram;
        this.OIS=OIS;
        this.OOS=OOS;
        enterProgram.setMenu(this);
    }
    public boolean checkToken(ObjectInputStream in, ObjectOutputStream out) {
        try {
            out.writeObject("checkToken");
            String result = (String) in.readObject();
            return result.equals("valid token");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("class not found");
            return false;
        }
    }

    public int compare(Tweet t1,Tweet t2) {
        LocalDateTime d1 = t1.getTweetDate();
        LocalDateTime d2 = t2.getTweetDate();
        if (d1.getYear() > d2.getYear())
            return 1;
        else if (d1.getYear() < d2.getYear()) {
            return -1;
        } else if (d2.getMonthValue() > d1.getMonthValue()) {
            return 1;
        } else if (d2.getMonthValue() < d1.getMonthValue()) {
            return -1;
        } else if (d2.getDayOfMonth() > d1.getDayOfMonth()) {
            return 1;
        } else if (d2.getDayOfMonth() < d1.getDayOfMonth()) {
            return -1;
        } else if (d2.getHour() > d1.getHour()){
            return 1;
        } else if (d2.getHour() < d1.getHour()) {
            return -1;
        } else if (d2.getMinute() > d1.getMinute()) {
            return 1;
        } else if (d2.getMinute() < d1.getMinute()) {
            return -1;
        } else return 0;
    }

    public void sortByDate(ArrayList<Tweet> tweets) {
        for(int i=0;i<tweets.size()-1;i++){
            for(int j=0;j<tweets.size()-i-1;j++){
                if(compare(tweets.get(j),tweets.get(j+1))==-1){
                    Tweet tmp = tweets.get(j);
                    tweets.set(i,tweets.get(j+1));
                    tweets.set(i+1,tmp);
                }
            }
        }
    }
    public void startMenu() throws ClassNotFoundException, java.io.IOException {
        Scanner scanner = new Scanner(System.in);
        String choice;
        while (true) {
            System.out.println("1-show timeline\n2-tweet\n3-set avatar\n4-set header\n5-set bio\n6-show followers\n7-show followings\n8-retweet\n9-quote\n10-reply\n11-follow\n12-unfollow\n13-block\n14-unblock\n15-direct\n16-remove tweet\n17-remove retweet\n18-remove quote\n19-remove reply\n20-exit");
            choice = scanner.nextLine();


            if (choice.equals("1")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    String command = "showTimeline";
                    OOS.writeObject(command);
                    ArrayList<Tweet> timeline = (ArrayList<Tweet>) OIS.readObject();
                    for (User following : user.getFollowings()) {
                        for (Tweet tweet : following.getTweets()) {
                            timeline.add(tweet);
                        }
                    }
                    sortByDate(timeline);
                    //show tweets
                }
            } else if (choice.equals("2")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    String text, photoDirectory, videoDirectory;
                    System.out.print("text: ");
                    text = scanner.nextLine();
                    System.out.print("photo: ");
                    photoDirectory = scanner.nextLine();
                    System.out.println("video: ");
                    videoDirectory = scanner.nextLine();
                    try {
                        File photoFile = new File(photoDirectory);
                        FileInputStream in = new FileInputStream(photoFile);
                        byte[] photo = new byte[(int) photoFile.length()];
                        in.read(photo);
                        File videoFile = new File(videoDirectory);
                        in = new FileInputStream(videoFile);
                        byte[] video = new byte[(int) videoFile.length()];
                        in.read(video);
                        in.close();
                        //be ja new ArrayList bayad find hashtag seda bezani
                        Tweet tweet = new Tweet(text, photo, video, LocalDateTime.now(), token, new ArrayList<>());
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(tweet);
                        if((OIS.readObject()).equals("tweeted successfully")){
                            user.getTweets().add(tweet);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }

            } else if (choice.equals("3")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("photo directory: ");
                    String photoDirectory = scanner.nextLine();
                    File file = new File(photoDirectory);
                    try {
                        FileInputStream in = new FileInputStream(file);
                        byte[] avatar = new byte[(int) file.length()];
                        in.read(avatar);
                        String command = "setAvatar";
                        OOS.writeObject(command);
                        //change user avatar here in client side and then pass to server to change in server side
                        user.setAvatar(avatar);
                        OOS.writeObject(user);
                        OIS.readObject();
                    } catch (java.io.IOException e) {
                        System.err.println("file not found");
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }

            } else if (choice.equals("4")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("photo directory: ");
                    String photoDirectory = scanner.nextLine();
                    File file = new File(photoDirectory);
                    try {
                        FileInputStream in = new FileInputStream(file);
                        byte[] header = new byte[(int) file.length()];
                        in.read(header);

                        String command = "setHeader";
                        //change user header here in client side and then pass to server to change in server side
                        user.setHeader(header);
                        OOS.writeObject(command);
                        OOS.writeObject(user);
                        OIS.readObject();
                    }
                    catch (java.io.IOException e) {
                        System.err.println("file not found");
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("5")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("bio: ");
                    String bio = scanner.nextLine();
                    String command = "setBio";
                    try {
                        //change user bio here in client side and then pass to server to change in server side
                        user.setBio(bio);
                        OOS.writeObject(command);
                        OOS.writeObject(user);
                        OIS.readObject();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("6")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    for (User user1 : user.getFollowers()) {
                        System.out.println(user1.getUsername() + "\t" + user1.getFirstName() + " " + user1.getLastName());
                    }
                }
            } else if (choice.equals("7")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    for (User user1 : user.getFollowings()) {
                        System.out.println(user1.getUsername() + "\t" + user1.getFirstName() + " " + user1.getLastName());
                    }
                }
            } else if (choice.equals("8")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("tweetID: ");
                    String tweetID = scanner.nextLine();
                    //should find the corresponding tweet to create the retweet object
                }
            } else if (choice.equals("9")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    String text, photoDirectory, videoDirectory, referredTweetID;
                    System.out.print("tweetID: ");
                    referredTweetID = scanner.nextLine();
                    System.out.print("text: ");
                    text = scanner.nextLine();
                    System.out.print("photo: ");
                    photoDirectory = scanner.nextLine();
                    System.out.println("video: ");
                    videoDirectory = scanner.nextLine();
                    try {
                        File photoFile = new File(photoDirectory);
                        FileInputStream in = new FileInputStream(photoFile);
                        byte[] photo = new byte[(int) photoFile.length()];
                        in.read(photo);
                        File videoFile = new File(videoDirectory);
                        in = new FileInputStream(videoFile);
                        byte[] video = new byte[(int) videoFile.length()];
                        in.read(video);
                        in.close();
                        //be ja new ArrayList bayad find hashtag seda bezani
                        Quote quote = new Quote(text, photo, video, LocalDateTime.now(), user.getUsername(), new ArrayList<>(), referredTweetID);
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(quote);
                        if((OIS.readObject()).equals("tweeted successfully")){
                            user.getTweets().add(quote);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.out.println("class not found");
                    }
                }
            } else if (choice.equals("10")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    String text, photoDirectory, videoDirectory, referredTweetID;
                    System.out.print("tweetID: ");
                    referredTweetID = scanner.nextLine();
                    System.out.print("text: ");
                    text = scanner.nextLine();
                    System.out.print("photo: ");
                    photoDirectory = scanner.nextLine();
                    System.out.println("video: ");
                    videoDirectory = scanner.nextLine();
                    try {
                        File photoFile = new File(photoDirectory);
                        FileInputStream in = new FileInputStream(photoFile);
                        byte[] photo = new byte[(int) photoFile.length()];
                        in.read(photo);
                        File videoFile = new File(videoDirectory);
                        in = new FileInputStream(videoFile);
                        byte[] video = new byte[(int) videoFile.length()];
                        in.read(video);
                        in.close();
                        //be ja new ArrayList bayad find hashtag seda bezani
                        Reply reply = new Reply(text, photo, video, LocalDateTime.now(), user.getUsername(), new ArrayList<>(), referredTweetID);
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(reply);
                        if((OIS.readObject()).equals("tweeted successfully")){
                            user.getTweets().add(reply);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e){
                        System.out.println("class not found");
                    }
                }
            } else if (choice.equals("11")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("username: ");
                    String username = scanner.nextLine();
                    String command = "follow";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(user.getUsername());
                        OOS.writeObject(username);
                        OIS.readObject();
                    }
                    catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("not found");
                    }
                }
            } else if (choice.equals("12")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("username: ");
                    String username = scanner.nextLine();
                    String command = "unfollow";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(user.getUsername());
                        OOS.writeObject(username);
                        OIS.readObject();
                    }
                    catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("?");
                    }
                }
            } else if (choice.equals("13")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("username: ");
                    String username = scanner.nextLine();
                    String command = "block";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(user.getUsername());
                        OOS.writeObject(username);
                        OIS.readObject();
                    }
                    catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("?");
                    }
                }
            } else if (choice.equals("14")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("username: ");
                    String username = scanner.nextLine();
                    String command = "unblock";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(user.getUsername());
                        OOS.writeObject(username);
                        OIS.readObject();
                    }
                    catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        System.err.println("?");
                    }
                }
            } else if (choice.equals("15")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("receiver username: ");
                    String receiver = scanner.nextLine();
                    System.out.println("your message: ");
                    String text = scanner.nextLine();
                    String command = "addDirect";
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(new Message(user.getUsername(),receiver,text,LocalDateTime.now()));
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("16")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("tweetID: ");
                    String tweetID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for(Tweet tweet:user.getTweets()){
                        if(tweet.getTweetID().equals(tweetID)){
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        OIS.readObject();
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("17")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("retweetID: ");
                    String retweetID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for(Tweet tweet:user.getTweets()){
                        if(tweet.getTweetID().equals(retweetID)){
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        OIS.readObject();
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("18")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("quoteID: ");
                    String quoteID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for(Tweet tweet:user.getTweets()){
                        if(tweet.getTweetID().equals(quoteID)){
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        OIS.readObject();
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("19")) {
                if(!checkToken(OIS,OOS)){
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("replyID: ");
                    String replyID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for(Tweet tweet:user.getTweets()){
                        if(tweet.getTweetID().equals(replyID)){
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        OIS.readObject();
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("20")) {
                break;
            } else {
                System.out.println("wrong input");
            }
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setClient(User user) {
        this.user = user;
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