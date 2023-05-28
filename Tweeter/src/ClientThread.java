import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class ClientThread implements Runnable{
    Socket client;
    Database database;
    private Connection conn;

    public ClientThread(Socket client,Database database) throws IOException {
        this.client = client;
        this.database=database;
        this.conn=MySQLConnection.getConnection();
    }

    @Override
    public void run() {
        String choice="";
        while(true){
            try {
                ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                choice=(String)OIS.readObject();
                OIS.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException();
            }
            if(choice.equals("signUp")){
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    User user=(User)OIS.readObject();
                    OIS.close();
                    signUp(user);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("signIn")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String username=(String) OIS.readObject();
                    String password=(String) OIS.readObject();
                    OIS.close();
                    signIn(username,password);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("checkToken")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String token=(String) OIS.readObject();
                    OIS.close();
                    checkToken(token);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("block")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String blocker=(String) OIS.readObject();
                    String blocked=(String) OIS.readObject();
                    OIS.close();
                    database.block(blocker,blocked);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("unblock")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String blocker=(String) OIS.readObject();
                    String blocked=(String) OIS.readObject();
                    OIS.close();
                    database.unBlock(blocker,blocked);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("addDirect")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    Message message=(Message) OIS.readObject();
                    OIS.close();
                    database.addDirect(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("fetchUser")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String username=(String) OIS.readObject();
                    OIS.close();
                    User user=database.fetchUser(username);
                    ObjectOutputStream OOS=new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(user);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if(choice.equals("searchHashtag")){
                try {
                    ObjectInputStream OIS=new ObjectInputStream(client.getInputStream());
                    String username=(String) OIS.readObject();
                    OIS.close();
                    searchHashtag(username);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }else if (choice.equals("setBio")) {
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    User user = (User) OIS.readObject();
                    OIS.close();
                    String result = setBio(user);
                    ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(result);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("follow")) {
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    String follower = (String) OIS.readObject();
                    String following = (String) OIS.readObject();
                    OIS.close();
                    String result = follow(follower, following);
                    ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(result);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("unfollow")) {
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    String follower = (String) OIS.readObject();
                    String following = (String) OIS.readObject();
                    OIS.close();
                    String result = unfollow(follower, following);
                    ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(result);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("addTweet")) {
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    Tweet tweet = (Tweet) OIS.readObject();
                    OIS.close();
                    String result = addTweet(tweet);
                    ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(result);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("removeTweet")) {
                try {
                    ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
                    Tweet tweet = (Tweet) OIS.readObject();
                    OIS.close();
                    String result = removeTweet(tweet);
                    ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
                    OOS.writeObject(result);
                    OOS.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("searchUser")) {
                try {
                    searchUser();
                }
                catch (SQLException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void signUp(User user) throws SQLException, IOException {
        ObjectOutputStream OOS=new ObjectOutputStream(client.getOutputStream());
        //checking if the username is duplicated
        PreparedStatement checkUsernameStm= conn.prepareStatement("SELECT * FROM users WHERE username=?");
        checkUsernameStm.setString(1,user.getUsername());
        ResultSet checkUsernameRs=checkUsernameStm.executeQuery();
        if(checkUsernameRs.next()){
            OOS.writeObject(new String("username has been already taken"));
            OOS.close();
            checkUsernameStm.close();
            return;
        }
        checkUsernameRs.close();
        //checking password
        String passwordCheckRes=passwordChecker(user.getPassword());
        if(!passwordCheckRes.equals("valid")){
            OOS.writeObject(new String(passwordCheckRes));
            OOS.close();
            return;
        }
        //checking email
        String emailCheckerRs=emailChecker(user.getEmail());
        if(!emailCheckerRs.equals("valid")){
            OOS.writeObject(new String(emailCheckerRs));
            OOS.close();
            return;
        }
        //checking phone number
        Statement phoneChecker=conn.createStatement();
        ResultSet phoneRs=phoneChecker.executeQuery("SELECT phoneNumber FROM users");
        while (phoneRs.next()){
            if(user.getPhoneNumber().equals(phoneRs.getString("phoneNumber"))){
                OOS.writeObject(new String("another account with this phone number exists"));
                OOS.close();
                return;
            }
        }
        if(user.getPhoneNumber().length()<7 || user.getPhoneNumber().length()>15){
            OOS.writeObject(new String("phone number does not seem to be fine"));
            OOS.close();
            return;
        }
        //check birthdate
        LocalDateTime startPoint=LocalDateTime.of(1900,1,1,0,0);
        if(user.getBirthDate().compareTo(startPoint)<0 || user.getBirthDate().compareTo(LocalDateTime.now())>0){
            OOS.writeObject(new String("your birthdate does not seem to be right"));
            OOS.close();
        }
        //generating token and send to client
        OOS.writeObject(new String("success"));
        String token=UserAuthenticator.generateToken(user.getUsername());
        OOS.writeObject(token);
        OOS.close();
        database.addUser(user);
    }
    public void signIn(String username,String password) throws SQLException, IOException {
        ObjectOutputStream OOS=new ObjectOutputStream(client.getOutputStream());
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        stm.setString(1,username);
        stm.setString(2,password);
        ResultSet rs=stm.executeQuery();
        if(!rs.next()){
            OOS.writeObject(new String("username or password is invalid"));
            OOS.close();
            return;
        }
        User user=database.fetchClient(username);
        OOS.writeObject(new String("success"));
        String token=UserAuthenticator.generateToken(user.getUsername());
        OOS.writeObject(token);
        OOS.writeObject(user);
        OOS.close();
    }
    public void checkToken(String token) throws IOException, SQLException {
        ObjectOutputStream OOS=new ObjectOutputStream(client.getOutputStream());
        String username=UserAuthenticator.validateToken(token);
        if(username==null){
            OOS.writeObject("not allowed");
            OOS.close();
            return;
        }
        User user=database.fetchClient(username);
        OOS.writeObject(new String("success"));
        OOS.writeObject(user);
        OOS.close();
    }

    public String passwordChecker(String password){
        if(password.length()<8){
            return "password must contain a minimum of 8 characters";
        }
        if(password.equals(password.toLowerCase()) || password.equals(password.toUpperCase())){
            return "password must contain at least 1 uppercase and 1 lowercase";
        }
        return "valid";
    }
    public String emailChecker(String email) throws SQLException {
        //checking if the email is in use or not
        Statement stm=conn.createStatement();
        ResultSet rs=stm.executeQuery("SELECT email FROM users");
        while (rs.next()){
            if((rs.getString("email")).equals(email)){
                stm.close();
                return "another account with this email exists";
            }
        }
        stm.close();
        //check validity
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z-Z]{2,7}$";
        boolean isValid = Pattern.matches(regex, email);
        if(!isValid){
            return "email is not valid";
        }
        return "valid";
    }
    public void searchHashtag(String hashtagName) throws SQLException, IOException {
        ArrayList<Tweet> tweets=new ArrayList<>();
        PreparedStatement hashtagStm=conn.prepareStatement("SELECT tweetID FROM hashtagInfo WHERE hashtagName=?");
        hashtagStm.setString(1,hashtagName);
        ResultSet hashtagRs=hashtagStm.executeQuery();
        while (hashtagRs.next()){
            String tweetID=hashtagRs.getString("tweetID");
            try {
                Tweet tweet=database.getTweet(tweetID);
                tweets.add(tweet);
                continue;
            } catch (SQLException e){
                //ignore
            }
            try {
                Retweet retweet=database.getRetweet(tweetID);
                tweets.add(retweet);
                continue;
            } catch (SQLException e){
                //ignore
            }
            try {
                Quote quote=database.getQuote(tweetID);
                tweets.add(quote);
                continue;
            } catch (SQLException e){
                //ignore
            }
            try {
                Retweet retweet=database.getRetweet(tweetID);
                tweets.add(retweet);
                continue;
            } catch (SQLException e){
                //ignore
            }
        }
        hashtagStm.close();
        ObjectOutputStream OOS=new ObjectOutputStream(client.getOutputStream());
        OOS.writeObject(tweets);
        OOS.close();
    }
    public String setBio(User user) throws SQLException {
        /*the bio is set in frontend and the user object is passed to this method to check the conditions for bio*/
        if (user.getBio().length() > 160) {
            return "bio has a maximum of 160 characters";
        } else {
            database.updateUser(user);
            return "bio has been updated successfully";
        }
    }

    public String follow(String follower, String following) throws SQLException {
        database.addFollowInfo(follower, following);
        return "success";
    }

    public String unfollow(String follower, String following) throws SQLException {
        database.removeFollowInfo(follower, following);
        return "success";
    }

    public String addTweet(Tweet tweet) throws SQLException {
        if (tweet.getText().length() > 280) {
            return "tweet text has a maximum of 280 characters";
        } else {
            if (tweet instanceof Retweet) {
                database.addRetweet((Retweet) tweet);
                return "tweeted successfully";
            } else if (tweet instanceof Reply) {
                database.addReply((Reply) tweet);
                return "tweeted successfully";
            } else if (tweet instanceof Quote) {
                database.addQuote((Quote) tweet);
                return "tweeted successfully";
            } else {
                database.addTweet(tweet);
                return "tweeted successfully";
            }
        }
    }

    public String removeTweet(Tweet tweet) throws SQLException {
        if (tweet instanceof Retweet) {
            database.removeRetweet((Retweet) tweet);
            return "success";
        } else if (tweet instanceof Reply) {
            database.removeReply((Reply) tweet);
            return "success";
        } else if (tweet instanceof Quote) {
            database.removeQuote((Quote) tweet);
            return "success";
        } else {
            database.removeTweet(tweet.getTweetID());
            return "success";
        }
    }

    public void searchUser() throws SQLException {
        String word;
        try (ObjectInputStream OIS = new ObjectInputStream(client.getInputStream())){
            word = (String) OIS.readObject();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        ArrayList<User> result = new ArrayList<>();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            //result.add(database.incompleteUserFetch(rs.getString("username")));
            String username = rs.getString("username");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            if ((username.toLowerCase(Locale.ROOT).contains(word.toLowerCase()))||((firstName.toLowerCase(Locale.ROOT).contains(word.toLowerCase())))||(lastName.toLowerCase(Locale.ROOT).contains(word.toLowerCase())))
                result.add(database.incompleteUserFetch(username));
        }
        try (ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream())){
            OOS.writeObject(result);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
