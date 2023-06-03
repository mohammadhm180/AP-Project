import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class ClientThread implements Runnable {
    private Socket client;
    private Database database;
    private Connection conn;
    private ObjectInputStream OIS;
    private ObjectOutputStream OOS;

    public ClientThread(Socket client, Database database) throws IOException {
        this.client = client;
        this.database = database;
        this.conn = MySQLConnection.getConnection();
        OIS = new ObjectInputStream(client.getInputStream());
        OOS = new ObjectOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        String choice = "";
        while (true) {
            try {
                choice = (String) OIS.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException();
            }
            if (choice.equals("signUp")) {
                try {
                    User user = (User) OIS.readObject();
                    signUp(user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("signIn")) {
                try {
                    String username = (String) OIS.readObject();
                    String password = (String) OIS.readObject();
                    signIn(username, password);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("checkToken")) {
                try {
                    String token = (String) OIS.readObject();
                    checkToken(token);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("checkSingInToken")) {
                try {
                    String token = (String) OIS.readObject();
                    checkSignInToken(token);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("block")) {
                try {
                    String blocker = (String) OIS.readObject();
                    String blocked = (String) OIS.readObject();
                    database.block(blocker, blocked);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("unblock")) {
                try {
                    String blocker = (String) OIS.readObject();
                    String blocked = (String) OIS.readObject();
                    database.unBlock(blocker, blocked);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("addDirect")) {
                try {
                    Message message = (Message) OIS.readObject();
                    database.addDirect(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("fetchUser")) {
                try {
                    String username = (String) OIS.readObject();
                    User user = database.fetchUser(username);
                    OOS.writeObject(user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("searchHashtag")) {
                try {
                    String username = (String) OIS.readObject();
                    searchHashtag(username);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            } else if (choice.equals("setBio")) {
                try {
                    User user = (User) OIS.readObject();
                    String result = setBio(user);
                    OOS.writeObject(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("follow")) {
                try {
                    String follower = (String) OIS.readObject();
                    String following = (String) OIS.readObject();
                    String result = follow(follower, following);
                    OOS.writeObject(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("unfollow")) {
                try {
                    String follower = (String) OIS.readObject();
                    String following = (String) OIS.readObject();
                    String result = unfollow(follower, following);
                    OOS.writeObject(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("addTweet")) {
                try {
                    Tweet tweet = (Tweet) OIS.readObject();
                    String result = addTweet(tweet);
                    OOS.writeObject(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("removeTweet")) {
                try {
                    Tweet tweet = (Tweet) OIS.readObject();
                    removeTweet(tweet);
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
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("setAvatar")) {
                try {
                    setAvatar();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("setHeader")) {
                try {
                    setHeader();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("getReplies")) {
                try {
                    getReplies();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("getTweet")) {
                try {
                    getTweet();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("getName")) {
                try {
                    getName();
                } catch (IOException | ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("getWriterUsername")) {
                try {
                    getWriterUsername();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (choice.equals("getFavstars")) {
                try {
                    getFavstars();
                } catch (IOException e) {
                    System.err.println("eror");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("like")) {
                try {
                    like();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("unlike")) {
                try {
                    unlike();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("vote")) {
                try {
                    vote();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void signUp(User user) throws SQLException, IOException {
        //checking if the username is duplicated
        PreparedStatement checkUsernameStm = conn.prepareStatement("SELECT * FROM users WHERE username=?");
        checkUsernameStm.setString(1, user.getUsername());
        ResultSet checkUsernameRs = checkUsernameStm.executeQuery();
        if (checkUsernameRs.next()) {
            OOS.writeObject(new String("username has been already taken"));
            checkUsernameStm.close();
            return;
        }
        checkUsernameRs.close();
        //checking password
        String passwordCheckRes = passwordChecker(user.getPassword());
        if (!passwordCheckRes.equals("valid")) {
            OOS.writeObject(new String(passwordCheckRes));
            return;
        }
        //checking email
        String emailCheckerRs = emailChecker(user.getEmail());
        if (!emailCheckerRs.equals("valid")) {
            OOS.writeObject(new String(emailCheckerRs));
            return;
        }
        //checking phone number
        Statement phoneChecker = conn.createStatement();
        ResultSet phoneRs = phoneChecker.executeQuery("SELECT phoneNumber FROM users");
        while (phoneRs.next()) {
            if (user.getPhoneNumber().equals(phoneRs.getString("phoneNumber"))) {
                OOS.writeObject(new String("another account with this phone number exists"));
                return;
            }
        }
        if (user.getPhoneNumber().length() < 7 || user.getPhoneNumber().length() > 15) {
            OOS.writeObject(new String("phone number does not seem to be fine"));
            return;
        }
        //check birthdate
        LocalDateTime startPoint = LocalDateTime.of(1900, 1, 1, 0, 0);
        if (user.getBirthDate().compareTo(startPoint) < 0 || user.getBirthDate().compareTo(LocalDateTime.now()) > 0) {
            OOS.writeObject(new String("your birthdate does not seem to be right"));
            return;
        }
        //generating token and send to client
        OOS.writeObject(new String("success"));
        String token = UserAuthenticator.generateToken(user.getUsername());
        OOS.writeObject(token);
        database.addUser(user);
    }

    public void signIn(String username, String password) throws SQLException, IOException {
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        stm.setString(1, username);
        stm.setString(2, password);
        ResultSet rs = stm.executeQuery();
        if (!rs.next()) {
            OOS.writeObject(new String("username or password is invalid"));
            return;
        }
        User user = database.fetchClient(username);
        OOS.writeObject(new String("success"));
        String token = UserAuthenticator.generateToken(user.getUsername());
        OOS.writeObject(token);
        OOS.writeObject(user);
    }

    public void checkToken(String token) throws IOException, SQLException {
        String username = UserAuthenticator.validateToken(token);
        if (username == null) {
            OOS.writeObject("invalid token");
            return;
        }
        OOS.writeObject(new String("valid token"));
    }

    //this is called when we want to get a client back
    public void checkSignInToken(String token) throws IOException, SQLException {
        String username = UserAuthenticator.validateToken(token);
        if (username == null) {
            OOS.writeObject("invalid token");
            return;
        }
        User user = database.fetchClient(username);
        OOS.writeObject(new String("success"));
        OOS.writeObject(user);
    }

    public String passwordChecker(String password) {
        if (password.length() < 8) {
            return "password must contain a minimum of 8 characters";
        }
        if (password.equals(password.toLowerCase()) || password.equals(password.toUpperCase())) {
            return "password must contain at least 1 uppercase and 1 lowercase";
        }
        return "valid";
    }

    public String emailChecker(String email) throws SQLException {
        //checking if the email is in use or not
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT email FROM users");
        while (rs.next()) {
            if ((rs.getString("email")).equals(email)) {
                stm.close();
                return "another account with this email exists";
            }
        }
        stm.close();
        //check validity
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z-Z]{2,7}$";
        boolean isValid = Pattern.matches(regex, email);
        if (!isValid) {
            return "email is not valid";
        }
        return "valid";
    }

    public void searchHashtag(String hashtagName) throws SQLException, IOException {
        ArrayList<Tweet> tweets = new ArrayList<>();
        PreparedStatement hashtagStm = conn.prepareStatement("SELECT tweetID FROM hashtagInfo WHERE hashtagName=?");
        hashtagStm.setString(1, hashtagName);
        ResultSet hashtagRs = hashtagStm.executeQuery();
        while (hashtagRs.next()) {
            String tweetID = hashtagRs.getString("tweetID");
            try {
                Tweet tweet = database.getTweet(tweetID);
                tweets.add(tweet);
                continue;
            } catch (SQLException e) {
                //ignore
            }
            try {
                Quote quote = database.getQuote(tweetID);
                tweets.add(quote);
                continue;
            } catch (SQLException e) {
                //ignore
            }
            try {
                Reply reply = database.getReply(tweetID);
                tweets.add(reply);
                continue;
            } catch (SQLException e) {
                //ignore
            }
            try {
                Vote vote = database.getVote(tweetID);
                tweets.add(vote);
                continue;
            } catch (SQLException e) {
                //ignore
            }
        }
        hashtagStm.close();
        ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
        OOS.writeObject(tweets);
    }

    //gets a username and return full name(uses while showing tweets)
    public void getName() throws IOException, ClassNotFoundException, SQLException {
        String username = (String) OIS.readObject();
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE username=?");
        stm.setString(1, username);
        ResultSet rs = stm.executeQuery();
        rs.next();
        String fullName = (rs.getString("firstName") + " " + rs.getString("lastName"));
        stm.close();
        OOS.writeObject(fullName);
    }
    public void vote() throws IOException, ClassNotFoundException, SQLException {
        String voteID=(String) OIS.readObject();
        Integer optionNumber=(Integer) OIS.readObject();
        database.vote(voteID,optionNumber);
    }

    //gets a tweetID and return username of its writer(uses when showing reply)
    public void getWriterUsername() throws IOException, ClassNotFoundException {
        String tweetID = (String) OIS.readObject();
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT authorUsername FROM tweets WHERE tweetID=?");
            stm.setString(1, tweetID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            OOS.writeObject(rs.getString("authorUsername"));
            stm.close();
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT authorUsername FROM quotes WHERE quoteID=?");
            stm.setString(1, tweetID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            OOS.writeObject(rs.getString("authorUsername"));
            stm.close();
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT authorUsername FROM votes WHERE voteID=?");
            stm.setString(1, tweetID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            OOS.writeObject(rs.getString("authorUsername"));
            stm.close();
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT authorUsername FROM replies WHERE replyID=?");
            stm.setString(1, tweetID);
            ResultSet rs = stm.executeQuery();
            rs.next();
            OOS.writeObject(rs.getString("authorUsername"));
            stm.close();
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTweet() throws IOException, ClassNotFoundException {
        String tweetID = (String) OIS.readObject();
        try {
            Tweet tweet = database.getTweet(tweetID);
            OOS.writeObject(tweet);
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Retweet retweet = database.getRetweet(tweetID);
            OOS.writeObject(retweet);
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Quote quote = database.getQuote(tweetID);
            OOS.writeObject(quote);
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Reply reply = database.getReply(tweetID);
            OOS.writeObject(reply);
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Vote vote = database.getVote(tweetID);
            OOS.writeObject(vote);
            return;
        } catch (SQLException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getReplies() throws IOException, ClassNotFoundException, SQLException {
        String tweetID = (String) OIS.readObject();
        ArrayList<Reply> replies = database.getReplies(tweetID);
        OOS.writeObject(replies);
    }

    public void like() throws SQLException, IOException, ClassNotFoundException {
        String tweetID = (String) OIS.readObject();
        try {
            Tweet tweet = database.getTweet(tweetID);
            database.like(tweet);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Quote quote = database.getQuote(tweetID);
            database.like(quote);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Reply reply = database.getReply(tweetID);
            database.like(reply);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Vote vote = database.getVote(tweetID);
            database.like(vote);
            return;
        } catch (SQLException e) {
            //ignore
        }
    }

    public void unlike() throws SQLException, IOException, ClassNotFoundException {
        String tweetID = (String) OIS.readObject();
        try {
            Tweet tweet = database.getTweet(tweetID);
            database.unLike(tweet);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Quote quote = database.getQuote(tweetID);
            database.unLike(quote);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Reply reply = database.getReply(tweetID);
            database.unLike(reply);
            return;
        } catch (SQLException e) {
            //ignore
        }
        try {
            Vote vote = database.getVote(tweetID);
            database.unLike(vote);
            return;
        } catch (SQLException e) {
            //ignore
        }
    }

    public void getFavstars() throws SQLException, IOException, ClassNotFoundException {
        ArrayList<Tweet> tweets = database.getTweetFavstars();
        OOS.writeObject(tweets);
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
            } else if (tweet instanceof Vote) {
                database.addVote((Vote) tweet);
                return "tweeted successfully";
            } else {
                database.addTweet(tweet);
                return "tweeted successfully";
            }
        }
    }

    public void removeTweet(Tweet tweet) throws SQLException {
        if (tweet instanceof Retweet) {
            database.removeRetweet((Retweet) tweet);
        } else if (tweet instanceof Reply) {
            database.removeReply((Reply) tweet);
        } else if (tweet instanceof Quote) {
            database.removeQuote((Quote) tweet);
        }else if (tweet instanceof Vote) {
            database.removeVote(tweet.getTweetID());
        } else {
            database.removeTweet(tweet.getTweetID());
        }
    }

    public void searchUser() throws SQLException {
        String word;
        try {
            word = (String) OIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
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
            if ((username.toLowerCase(Locale.ROOT).contains(word.toLowerCase())) || ((firstName.toLowerCase(Locale.ROOT).contains(word.toLowerCase()))) || (lastName.toLowerCase(Locale.ROOT).contains(word.toLowerCase())))
                result.add(database.incompleteUserFetch(username));
        }
        try {
            OOS.writeObject(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAvatar() throws SQLException {
        /*the avatar is set in frontend and the user object is passed to this method*/
        try {
            ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
            User user = (User) OIS.readObject();
            database.updateUser(user);
            OIS.close();
            String result = "avatar has been updated successfully";
            ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
            OOS.writeObject(result);
            OOS.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHeader() throws SQLException {
        /*the header is set in frontend and the user object is passed to this method*/
        try {
            ObjectInputStream OIS = new ObjectInputStream(client.getInputStream());
            User user = (User) OIS.readObject();
            database.updateUser(user);
            OIS.close();
            String result = "header has been updated successfully";
            ObjectOutputStream OOS = new ObjectOutputStream(client.getOutputStream());
            OOS.writeObject(result);
            OOS.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}


