import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private Connection conn;

    public Database() {
        this.conn = MySQLConnection.getConnection();
    }
    public void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users(username VARCHAR(50),firstName VARCHAR(50),lastName VARCHAR(50),email VARCHAR(60),phoneNumber VARCHAR(16),password VARCHAR(25),country VARCHAR(40),avatar LONGBLOB,header LONGBLOB,bio VARCHAR(160),location VARCHAR(150),webAddress VARCHAR(100),birthYear INT,birthMonth INT,birthDay INT,signUpYear INT,signUpMonth INT,signUpDay INT,signUpHour INT,signUpMinute INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tweets(tweetID VARCHAR(100),text VARCHAR(280),photo LONGBLOB,video LONGBLOB,retweetCount INT ,replyCount INT ,authorUsername VARCHAR(50),tweetYear INT,tweetMonth INT,tweetDay INT,tweetHour INT,tweetMinute INT,likeCount INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS followInfo(followerUN VARCHAR(50),followingUN VARCHAR(50))");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS votes(voteID VARCHAR(100),ownerUserName VARCHAR(50),question VARCHAR(200),option1 VARCHAR(100),option2 VARCHAR(100),option3 VARCHAR(100),option4 VARCHAR(100),option1Count INT,option2Count INT,option3Count INT,option4Count INT,voteYear INT,voteMonth INT,voteDay INT,voteHour INT,voteMinute INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS retweets(retweetID VARCHAR(100),text VARCHAR(280),photo LONGBLOB,video LONGBLOB,retweetCount INT ,replyCount INT ,authorUsername VARCHAR(50),retweetYear INT,retweetMonth INT,retweetDay INT,retweetHour INT,retweetMinute INT,referredTweetID VARCHAR(100),likeCount INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS quotes(quoteID VARCHAR(100),text VARCHAR(280),photo LONGBLOB,video LONGBLOB,retweetCount INT ,replyCount INT ,authorUsername VARCHAR(50),quoteYear INT,quoteMonth INT,quoteDay INT,quoteHour INT,quoteMinute,referredTweetID VARCHAR(100),likeCount INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS replies(replyID VARCHAR(100),text VARCHAR(280),photo LONGBLOB,video LONGBLOB,retweetCount INT ,replyCount INT ,authorUsername VARCHAR(50),replyYear INT,replyMonth INT,replyDay INT,replyHour INT,replyMinute INT,referredTweetID VARCHAR(100),likeCount INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS directs(sender VARCHAR(50),receiver VARCHAR(50),text VARCHAR(300),directYear INT,directMonth INT,directDay INT,directHour INT,directMinute INT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS blockInfo(blocker VARCHAR(50),blocked VARCHAR(50))");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hashtagInfo(tweetID VARCHAR(100),hashtagName VARCHAR(30),hashtagYear INT,hashtagMonth INT,hashtagDay INT,hashtagHour INT,hashtagMinute INT)");
    }
    public void addUser(User user) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("INSERT INTO users (username,firstName,lastName,email,phoneNumber,password,country,avatar,header,bio,location,webAddress,birthYear,birthMonth,birthDay) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        stm.setString(1,user.getUsername());
        stm.setString(2,user.getFirstName());
        stm.setString(3,user.getLastName());
        stm.setString(4,user.getPhoneNumber());
        stm.setString(5,user.getPassword());
        stm.setString(6,user.getCountry());
        stm.setBytes(7,user.getAvatar());
        stm.setBytes(8,user.getHeader());
        stm.setString(9,user.getBio());
        stm.setString(10,user.getLocation());
        stm.setString(11,user.getWebAddress());
        stm.setInt(12,user.getBirthDate().getYear());
        stm.setInt(13,user.getBirthDate().getMonthValue());
        stm.setInt(14,user.getBirthDate().getDayOfMonth());
        stm.executeUpdate();
        stm.close();
    }
    public User fetchClient(String username) throws SQLException {
        //filling profile info
        PreparedStatement profileStm=conn.prepareStatement("SELECT * FROM users WHERE username=?");
        profileStm.setString(1,username);
        ResultSet rsProfile=profileStm.executeQuery();
        User user=new User(username,rsProfile.getString("firstName"),rsProfile.getString("lastName"),rsProfile.getString("email"),rsProfile.getString("phoneNumber"),rsProfile.getString("password"),rsProfile.getString("country"),LocalDateTime.of(rsProfile.getInt("birthYear"),rsProfile.getInt("birthMonth"),rsProfile.getInt("birthDay"),0,0),LocalDateTime.of(rsProfile.getInt("signUpYear"),rsProfile.getInt("signUpMonth"),rsProfile.getInt("signUpDay"),0,0));
        user.setBio(rsProfile.getString("bio"));
        user.setLocation(rsProfile.getString("location"));
        user.setWebAddress(rsProfile.getString("webAddress"));
        user.setAvatar(rsProfile.getBytes("avatar"));
        user.setHeader(rsProfile.getBytes("header"));
        profileStm.close();
        //filling followers
        ArrayList<User> followers=user.getFollowers();
        PreparedStatement followerInfoStm=conn.prepareStatement("SELECT * FROM followInfo WHERE followingUN=?");
        followerInfoStm.setString(1,username);
        ResultSet followerRs=followerInfoStm.executeQuery();
        while (followerRs.next()){
            followers.add(incompleteUserFetch(followerRs.getString("followerUN")));
        }
        //filling followings
        ArrayList<User> followings=user.getFollowings();
        PreparedStatement followingInfoStm=conn.prepareStatement("SELECT * FROM followInfo WHERE followerUN=?");
        followingInfoStm.setString(1,username);
        ResultSet followingRs=followingInfoStm.executeQuery();
        while (followingRs.next()){
            followings.add(incompleteUserFetch(followingRs.getString("followingUN")));
        }
        for (User following:followings){
            setTweetsOfUser(following.getUsername(),following.getTweets());
        }
        followingInfoStm.close();
        followerInfoStm.close();
        //filling votes
        ArrayList<Vote> votes=user.getVotes();
        PreparedStatement voteStm=conn.prepareStatement("SELECT * FROM votes WHERE ownerUsername=?");
        voteStm.setString(1,username);
        ResultSet voteRs=voteStm.executeQuery();
        while (voteRs.next()){
            votes.add(new Vote(voteRs.getString("ownerUsername"),voteRs.getString("question"),LocalDateTime.of(voteRs.getInt("year"),voteRs.getInt("month"),voteRs.getInt("day"),voteRs.getInt("hour"),voteRs.getInt("minute")),voteRs.getString("option1"),voteRs.getString("option2"),voteRs.getString("option3"),voteRs.getString("option4"),voteRs.getInt("option1Count"),voteRs.getInt("option2Count"),voteRs.getInt("option3Count"),voteRs.getInt("option4Count")));
        }
        voteStm.close();
        //filling tweets of user
        ArrayList<Tweet> tweets=user.getTweets();
        setTweetsOfUser(username,tweets);
        //filling retweets of user
        setRetweetsOfUser(username,tweets);
        //filling quotes of user
        setQuotesOfUser(username,tweets);
        //filling direct
        ArrayList<Message> sentMessages=new ArrayList<>();
        ArrayList<Message> receivedMessages=new ArrayList<>();
        PreparedStatement sendDirectStm= conn.prepareStatement("SELECT * FROM directs WHERE sender=?");
        sendDirectStm.setString(1,username);
        ResultSet sendRs=sendDirectStm.executeQuery();
        while (sendRs.next()){
            sentMessages.add(new Message(username,sendRs.getString("receiver"),sendRs.getString("text"),LocalDateTime.of(sendRs.getInt("directYear"),sendRs.getInt("directMonth"),sendRs.getInt("directDay"),sendRs.getInt("directHour"),sendRs.getInt("directMinute"))));
        }
        sendDirectStm.close();
        PreparedStatement receiveDirectStm= conn.prepareStatement("SELECT * FROM directs WHERE receiver=?");
        receiveDirectStm.setString(1,username);
        ResultSet receiveRs=receiveDirectStm.executeQuery();
        while (receiveRs.next()){
            receivedMessages.add(new Message(sendRs.getString("sender"),username,sendRs.getString("text"),LocalDateTime.of(sendRs.getInt("directYear"),sendRs.getInt("directMonth"),sendRs.getInt("directDay"),sendRs.getInt("directHour"),sendRs.getInt("directMinute"))));
        }
        receiveDirectStm.close();
        //filling blocked users list
        ArrayList<String> blockedUsers=user.getBlockedUsers();
        PreparedStatement blockInfoStm= conn.prepareStatement("SELECT * FROM blockInfo WHERE blocker=?");
        blockInfoStm.setString(1,username);
        ResultSet blockRs=blockInfoStm.executeQuery();
        while (blockRs.next()){
            blockedUsers.add(blockRs.getString("blocked"));
        }
        blockInfoStm.close();
        return user;
    }
    public User incompleteUserFetch(String username) throws SQLException {
        //incomplete filling of profile
        PreparedStatement profileStm=conn.prepareStatement("SELECT * FROM users WHERE username=?");
        profileStm.setString(1,username);
        ResultSet rsProfile=profileStm.executeQuery();
        User user=new User(username,rsProfile.getString("firstName"),rsProfile.getString("lastName"),null,null,null,null,null,null);
        user.setAvatar(rsProfile.getBytes("avatar"));
        profileStm.close();
        return user;
    }
    public User fetchUser(String username) throws SQLException {
        //filling profile
        PreparedStatement profileStm=conn.prepareStatement("SELECT * FROM users WHERE username=?");
        profileStm.setString(1,username);
        ResultSet rsProfile=profileStm.executeQuery();
        User user=new User(username,rsProfile.getString("firstName"),rsProfile.getString("lastName"),null,null,null,rsProfile.getString("country"),null,null);
        user.setAvatar(rsProfile.getBytes("avatar"));
        user.setHeader(rsProfile.getBytes("header"));
        user.setBio(rsProfile.getString("bio"));
        user.setLocation(rsProfile.getString("location"));
        user.setWebAddress(rsProfile.getString("webAddress"));
        profileStm.close();
        //filling followers
        ArrayList<User> followers=user.getFollowers();
        PreparedStatement followerInfoStm=conn.prepareStatement("SELECT * FROM followInfo WHERE followingUN=?");
        followerInfoStm.setString(1,username);
        ResultSet followerRs=followerInfoStm.executeQuery();
        while (followerRs.next()){
            followers.add(incompleteUserFetch(followerRs.getString("followerUN")));
        }
        //filling followings
        ArrayList<User> followings=user.getFollowings();
        PreparedStatement followingInfoStm=conn.prepareStatement("SELECT * FROM followInfo WHERE followerUN=?");
        followingInfoStm.setString(1,username);
        ResultSet followingRs=followingInfoStm.executeQuery();
        while (followingRs.next()){
            followings.add(incompleteUserFetch(followingRs.getString("followingUN")));
        }
        followerInfoStm.close();
        followingInfoStm.close();
        //filling votes
        ArrayList<Vote> votes=user.getVotes();
        PreparedStatement voteStm=conn.prepareStatement("SELECT * FROM votes WHERE ownerUsername=?");
        voteStm.setString(1,username);
        ResultSet voteRs=voteStm.executeQuery();
        while (voteRs.next()){
            votes.add(new Vote(voteRs.getString("ownerUsername"),voteRs.getString("question"),LocalDateTime.of(voteRs.getInt("year"),voteRs.getInt("month"),voteRs.getInt("day"),voteRs.getInt("hour"),voteRs.getInt("minute")),voteRs.getString("option1"),voteRs.getString("option2"),voteRs.getString("option3"),voteRs.getString("option4"),voteRs.getInt("option1Count"),voteRs.getInt("option2Count"),voteRs.getInt("option3Count"),voteRs.getInt("option4Count")));
        }
        voteStm.close();
        //filling tweets of user
        ArrayList<Tweet> tweets=user.getTweets();
        setTweetsOfUser(username,tweets);
        //filling retweets of user
        setRetweetsOfUser(username,tweets);
        //filling quotes of user
        setQuotesOfUser(username,tweets);
        return user;
    }
    public Tweet getTweet(String tweetID) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM tweets WHERE tweetID=?");
        stm.setString(1,tweetID);
        ResultSet tweetRs=stm.executeQuery();
        tweetRs.next();
        Tweet tweet=new Tweet(tweetRs.getString("text"),tweetRs.getBytes("photo"),tweetRs.getBytes("video"),LocalDateTime.of(tweetRs.getInt("year"),tweetRs.getInt("month"),tweetRs.getInt("day"),tweetRs.getInt("hour"),tweetRs.getInt("minute")),tweetRs.getString("authorUsername"),getHashtagsOfTweet(tweetID),tweetRs.getInt("replyCount"),tweetRs.getInt("retweetCount"),tweetRs.getInt("likeCount"));
        stm.close();
        return tweet;
    }
    public Retweet getRetweet(String retweetID) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM retweets WHERE retweetID=?");
        stm.setString(1,retweetID);
        ResultSet retweetRs=stm.executeQuery();
        retweetRs.next();
        Retweet retweet= new Retweet(retweetRs.getString("text"),retweetRs.getBytes("photo"),retweetRs.getBytes("video"),LocalDateTime.of(retweetRs.getInt("year"),retweetRs.getInt("month"),retweetRs.getInt("day"),retweetRs.getInt("hour"),retweetRs.getInt("minute")),retweetRs.getString("authorUsername"),getHashtagsOfTweet(retweetID),retweetRs.getString("referredTweetID"),retweetRs.getInt("replyCount"),retweetRs.getInt("retweetCount"),retweetRs.getInt("likeCount"));
        stm.close();
        return retweet;
    }
    public Quote getQuote(String quoteID) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM quotes WHERE quoteID=?");
        stm.setString(1,quoteID);
        ResultSet quoteRs=stm.executeQuery();
        quoteRs.next();
        Quote quote=new Quote(quoteRs.getString("text"),quoteRs.getBytes("photo"),quoteRs.getBytes("video"),LocalDateTime.of(quoteRs.getInt("year"),quoteRs.getInt("month"),quoteRs.getInt("day"),quoteRs.getInt("hour"),quoteRs.getInt("minute")),quoteRs.getString("authorUsername"),getHashtagsOfTweet(quoteID),quoteRs.getString("referredTweetID"),quoteRs.getInt("replyCount"),quoteRs.getInt("retweetCount"),quoteRs.getInt("likeCount"));
        stm.close();
        return quote;
    }
    public ArrayList<Reply> getReplies(String tweetID) throws SQLException {
        ArrayList<Reply> replies=new ArrayList<>();
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM replies WHERE referredTweetID=?");
        stm.setString(1,tweetID);
        ResultSet replyRs=stm.executeQuery();
        while (replyRs.next()){
            replies.add(new Reply(replyRs.getString("text"),replyRs.getBytes("photo"),replyRs.getBytes("video"),LocalDateTime.of(replyRs.getInt("year"),replyRs.getInt("month"),replyRs.getInt("day"),replyRs.getInt("hour"),replyRs.getInt("minute")),replyRs.getString("authorUsername"),getHashtagsOfTweet(replyRs.getString("replyID")),replyRs.getString("referredTweetID"),replyRs.getInt("replyCount"),replyRs.getInt("retweetCount"),replyRs.getInt("likeCount")));
        }
        stm.close();
        return replies;
    }
    public void setTweetsOfUser(String authorUsername,ArrayList<Tweet> tweets) throws SQLException {
        PreparedStatement tweetStm=conn.prepareStatement("SELECT * FROM tweets WHERE authorUsername=?");
        tweetStm.setString(1,authorUsername);
        ResultSet tweetRs=tweetStm.executeQuery();
        while (tweetRs.next()){
            tweets.add(getTweet(tweetRs.getString("tweetID")));
        }
        tweetStm.close();
    }
    public void setQuotesOfUser(String authorUsername,ArrayList<Tweet> tweets) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("SELECT * FROM quotes WHERE authorUsername=?");
        stm.setString(1,authorUsername);
        ResultSet quoteRs=stm.executeQuery();
        while (quoteRs.next()){
            tweets.add(getQuote(quoteRs.getString("quoteID")));
        }
        stm.close();
    }
    public void setRetweetsOfUser(String authorUsername,ArrayList<Tweet> tweets) throws SQLException {
        PreparedStatement tweetStm=conn.prepareStatement("SELECT * FROM retweets WHERE authorUsername=?");
        tweetStm.setString(1,authorUsername);
        ResultSet retweetRs=tweetStm.executeQuery();
        while (retweetRs.next()){
            tweets.add(getRetweet(retweetRs.getString("retweetID")));
        }
        tweetStm.close();
    }
    public ArrayList<String> getHashtagsOfTweet(String tweetID) throws SQLException {
        ArrayList<String> hashtags=new ArrayList<>();
        PreparedStatement hashtagInfoStm=conn.prepareStatement("SELECT * FROM hashtagInfo WHERE tweetID=?");
        hashtagInfoStm.setString(1,tweetID);
        ResultSet rs=hashtagInfoStm.executeQuery();
        while (rs.next()){
            hashtags.add(rs.getString("hashtagName"));
        }
        hashtagInfoStm.close();
        return hashtags;
    }
    public void updateUser(User user) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("UPDATE users SET username=? ,firstName=? ,lastName=? , email=?,phoneNumber=? ,password=? , country=? , avatar=? , header=? ,bio=? ,location=? ,webAddress=? ,birthYear=? ,birthMonth=? ,birthDay=? ,  WHERE username=?  ");
        stm.setString(1,user.getUsername());
        stm.setString(2,user.getFirstName());
        stm.setString(3,user.getLastName());
        stm.setString(4,user.getPhoneNumber());
        stm.setString(5,user.getPassword());
        stm.setString(6,user.getCountry());
        stm.setBytes(7,user.getAvatar());
        stm.setBytes(8,user.getHeader());
        stm.setString(9,user.getBio());
        stm.setString(10,user.getLocation());
        stm.setString(11,user.getWebAddress());
        stm.setInt(12,user.getBirthDate().getYear());
        stm.setInt(13,user.getBirthDate().getMonthValue());
        stm.setInt(14,user.getBirthDate().getDayOfMonth());
        stm.executeUpdate();
        stm.close();
    }
    public void addFollowInfo(String followerUN, String followingUN) throws SQLException {
        PreparedStatement stm= conn.prepareStatement("INSERT INTO followInfo (followrUN,followingUN) VALUES (?,?)");
        stm.setString(1,followerUN);
        stm.setString(2,followingUN);
        stm.executeUpdate();
        stm.close();
    }
    public void removeFollowInfo(String followerUN, String followingUN) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("DELETE FROM followInfo WHERE followrUN=? AND followingUN=?");
        stm.setString(1,followerUN);
        stm.setString(2,followingUN);
        stm.executeUpdate();
        stm.close();
    }

    public void addVote(Vote vote) throws SQLException {
        PreparedStatement stm=conn.prepareStatement("INSERT INTO votes (voteID,ownerUsername,question,option1,option2,option3,option4,option1Count,option2Count,option3Count,option4Count,voteYear,voteMonth,voteDay,voteHour,voteMinute) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        stm.setString(1, UUID.randomUUID().toString());
        stm.setString(2,vote.getOwnerUsername());
        stm.setString(3,vote.getQuestion());
        stm.setString(4,vote.getOption1());
        stm.setString(5,vote.getOption2());
        stm.setString(6,vote.getOption3());
        stm.setString(7,vote.getOption4());
        stm.setInt(8,vote.getOption1Count());
        stm.setInt(9,vote.getOption2Count());
        stm.setInt(10,vote.getOption3Count());
        stm.setInt(11,vote.getOption4Count());
        stm.setInt(12,vote.getVoteDate().getYear());
        stm.setInt(13,vote.getVoteDate().getMonthValue());
        stm.setInt(14,vote.getVoteDate().getDayOfMonth());
        stm.setInt(15,vote.getVoteDate().getHour());
        stm.setInt(16,vote.getVoteDate().getMinute());
        stm.executeUpdate();
        stm.close();
    }
    public void vote(String voteID,int optionNumber) throws SQLException {
        if(optionNumber==1){
            PreparedStatement stm= conn.prepareStatement("UPDATE votes SET option1count=option1count + 1 WHERE voteID=?");
            stm.setString(1,voteID);
            stm.executeUpdate();
            stm.close();
        }
        if(optionNumber==2){
            PreparedStatement stm= conn.prepareStatement("UPDATE votes SET option2count=option2count + 1 WHERE voteID=?");
            stm.setString(1,voteID);
            stm.executeUpdate();
            stm.close();
        }
        if(optionNumber==3){
            PreparedStatement stm= conn.prepareStatement("UPDATE votes SET option3count=option3count + 1 WHERE voteID=?");
            stm.setString(1,voteID);
            stm.executeUpdate();
            stm.close();
        }
        if(optionNumber==4){
            PreparedStatement stm= conn.prepareStatement("UPDATE votes SET option4count=option4count + 1 WHERE voteID=?");
            stm.setString(1,voteID);
            stm.executeUpdate();
            stm.close();
        }
    }




    public void addTweet(Tweet tweet) throws SQLException {
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO tweets(tweetID,text,photo,video,retweetCount,replyCount,authorUsername,hashtag,tweetYear,tweetMonth,tweetDay) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        addStatement.setString(1, tweet.getTweetID());
        addStatement.setString(2, tweet.getText());
        addStatement.setBytes(3, tweet.getPhoto());
        addStatement.setBytes(4, tweet.getVideo());
        addStatement.setInt(5, tweet.getRetweetCount());
        addStatement.setInt(6, tweet.getReplyCount());
        addStatement.setString(7, tweet.getAuthorUsername());
        addStatement.setInt(8, tweet.getTweetDate().getYear());
        addStatement.setInt(9, tweet.getTweetDate().getMonthValue());
        addStatement.setInt(10, tweet.getTweetDate().getDayOfMonth());
        addStatement.setInt(11,tweet.getTweetDate().getHour());
        addStatement.setInt(12,tweet.getTweetDate().getMinute());
        addStatement.executeUpdate();
        addStatement.close();
    }

    public void removeTweet(String tweetID) throws SQLException {
        //removing all retweets of the tweet
        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM retweets WHERE referredTweetID = ?");
        deleteStatement.setString(1, tweetID);
        deleteStatement.executeUpdate();

        //removing all quotes of the tweet
        deleteStatement = conn.prepareStatement("DELETE FROM quotes WHERE referredTweetID = ?");
        deleteStatement.setString(1, tweetID);
        deleteStatement.executeUpdate();

        //removing all replies of the tweet
        deleteStatement = conn.prepareStatement("DELETE FROM replies WHERE referredTweetID = ?");
        deleteStatement.setString(1, tweetID);
        deleteStatement.executeUpdate();

        //removing tweet itself
        deleteStatement = conn.prepareStatement("DELETE FROM tweets WHERE tweetID = ?");
        deleteStatement.setString(1, tweetID);
        deleteStatement.executeUpdate();
        deleteStatement.close();
    }

    public void addRetweet(Retweet retweet) throws SQLException {
        //adding retweet
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO retweets(retweetID,text,photo,video,retweetCount,replyCount,authorUsername,hashtag,retweetYear,retweetMonth,retweetDay,referredTweetID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        addStatement.setString(1, retweet.getTweetID());
        addStatement.setString(2, retweet.getText());
        addStatement.setBytes(3, retweet.getPhoto());
        addStatement.setBytes(4, retweet.getVideo());
        addStatement.setInt(5, retweet.getRetweetCount());
        addStatement.setInt(6, retweet.getReplyCount());
        addStatement.setString(7, retweet.getAuthorUsername());
        addStatement.setInt(8, retweet.getTweetDate().getYear());
        addStatement.setInt(9, retweet.getTweetDate().getMonthValue());
        addStatement.setInt(10, retweet.getTweetDate().getDayOfMonth());
        addStatement.setInt(11,retweet.getTweetDate().getHour());
        addStatement.setInt(12,retweet.getTweetDate().getMinute());
        addStatement.setString(13, retweet.getReferredTweetID());
        addStatement.executeUpdate();
        addStatement.close();
        //increase retweetCount in ReferredTweetID
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE tweets SET retweetCount = retweetCount + 1 WHERE tweetID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if tweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE retweets SET retweetCount = retweetCount + 1 WHERE retweetID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if retweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE quotes SET retweetCount = retweetCount + 1 WHERE quoteID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeRetweet(Retweet retweet) throws SQLException {
        String retweetID=retweet.getTweetID();
        //removing all retweets of the retweet
        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM retweets WHERE referredTweetID=?");
        deleteStatement.setString(1, retweetID);
        deleteStatement.executeUpdate();

        //removing all quotes of the retweet
        deleteStatement = conn.prepareStatement("DELETE FROM quotes WHERE referredTweetID = ?");
        deleteStatement.setString(1, retweetID);
        deleteStatement.executeUpdate();

        //removing all replies of the retweet
        deleteStatement = conn.prepareStatement("DELETE FROM replies WHERE referredTweetID = ?");
        deleteStatement.setString(1, retweetID);
        deleteStatement.executeUpdate();

        //removing retweet itself
        deleteStatement = conn.prepareStatement("DELETE FROM retweets WHERE retweetID = ?");
        deleteStatement.setString(1, retweetID);
        deleteStatement.executeUpdate();
        deleteStatement.close();
        //decrease retweetCount in ReferredTweetID
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE tweets SET retweetCount = retweetCount - 1 WHERE tweetID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if tweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE retweets SET retweetCount = retweetCount - 1 WHERE retweetID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if retweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE quotes SET retweetCount = retweetCount - 1 WHERE quoteID = ?");
            tweetStmt.setString(1,retweet.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addQuote(Quote quote) throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO quotes(quoteID,text,photo,video,retweetCount,replyCount,authorUsername,hashtag,quoteYear,quoteMonth,quoteDay) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        addStatement.setString(1, quote.getTweetID());
        addStatement.setString(2, quote.getText());
        addStatement.setBytes(3, quote.getPhoto());
        addStatement.setBytes(4, quote.getVideo());
        addStatement.setInt(5, quote.getRetweetCount());
        addStatement.setInt(6, quote.getReplyCount());
        addStatement.setString(7, quote.getAuthorUsername());
        addStatement.setInt(8, quote.getTweetDate().getYear());
        addStatement.setInt(9, quote.getTweetDate().getMonthValue());
        addStatement.setInt(10, quote.getTweetDate().getDayOfMonth());
        addStatement.setInt(11,quote.getTweetDate().getHour());
        addStatement.setInt(12,quote.getTweetDate().getMinute());
        addStatement.setString(13, quote.getReferredTweetID());
        addStatement.executeUpdate();
        addStatement.close();
        //increase retweetCount in ReferredTweetID
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE tweets SET retweetCount = retweetCount + 1 WHERE tweetID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if tweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE retweets SET retweetCount = retweetCount + 1 WHERE retweetID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if retweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE quotes SET retweetCount = retweetCount + 1 WHERE quoteID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeQuote(Quote quote) throws SQLException{
        String quoteID=quote.getTweetID();
        //removing all retweets of the quote
        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM retweets WHERE referredTweetID=?");
        deleteStatement.setString(1, quoteID);
        deleteStatement.executeUpdate();

        //removing all quotes of the quote
        deleteStatement = conn.prepareStatement("DELETE FROM quotes WHERE referredTweetID = ?");
        deleteStatement.setString(1, quoteID);
        deleteStatement.executeUpdate();

        //removing all replies of the quote
        deleteStatement = conn.prepareStatement("DELETE FROM reply WHERE referredTweetID = ?");
        deleteStatement.setString(1, quoteID);
        deleteStatement.executeUpdate();

        //removing quote itself
        deleteStatement = conn.prepareStatement("DELETE FROM quotes WHERE quoteID = ?");
        deleteStatement.setString(1, quoteID);
        deleteStatement.executeUpdate();
        deleteStatement.close();
        //decrease retweetCount in ReferredTweetID
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE tweets SET retweetCount = retweetCount - 1 WHERE tweetID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if tweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE retweets SET retweetCount = retweetCount - 1 WHERE retweetID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            // Ignore exception if retweetID not found in tweet table
        }
        try {
            PreparedStatement tweetStmt = conn.prepareStatement("UPDATE quotes SET retweetCount = retweetCount - 1 WHERE quoteID = ?");
            tweetStmt.setString(1,quote.getReferredTweetID());
            tweetStmt.executeUpdate();
            tweetStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReply(Reply reply) throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO replies(replyID,text,photo,video,retweetCount,replyCount,authorUsername,hashtag,replyYear,replyMonth,replyDay,replyHour,replyMinute,referredTweetID,likeCount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        addStatement.setString(1, reply.getTweetID());
        addStatement.setString(2, reply.getText());
        addStatement.setBytes(3, reply.getPhoto());
        addStatement.setBytes(4, reply.getVideo());
        addStatement.setInt(5, reply.getRetweetCount());
        addStatement.setInt(6, reply.getReplyCount());
        addStatement.setString(7, reply.getAuthorUsername());
        addStatement.setInt(8, reply.getTweetDate().getYear());
        addStatement.setInt(9, reply.getTweetDate().getMonthValue());
        addStatement.setInt(10, reply.getTweetDate().getDayOfMonth());
        addStatement.setInt(11,reply.getTweetDate().getHour());
        addStatement.setInt(12,reply.getTweetDate().getMinute());
        addStatement.setString(13, reply.getReferredTweetID());
        addStatement.setInt(14,reply.getLikeCount());
        addStatement.executeUpdate();
        addStatement.close();
        for(String hashtag:reply.getHashtag()){
            addHashtag(reply.getAuthorUsername(),new HashtagInfo(hashtag,0,reply.getTweetDate()));
        }
    }

    public void removeReply(String replyID) throws SQLException{
        //removing all retweets of the reply
        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM retweets WHERE referredTweetID=?");
        deleteStatement.setString(1, replyID);
        deleteStatement.executeUpdate();

        //removing all quotes of the reply
        deleteStatement = conn.prepareStatement("DELETE FROM quotes WHERE referredTweetID = ?");
        deleteStatement.setString(1, replyID);
        deleteStatement.executeUpdate();

        //removing all replies of the reply
        deleteStatement = conn.prepareStatement("DELETE FROM reply WHERE referredTweetID = ?");
        deleteStatement.setString(1, replyID);
        deleteStatement.executeUpdate();

        //removing retweet itself
        deleteStatement = conn.prepareStatement("DELETE FROM reply WHERE quoteID = ?");
        deleteStatement.setString(1, replyID);
        deleteStatement.executeUpdate();
        deleteStatement.close();
    }

    public void addDirect(Message message) throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO directs(sender,receiver,text,directYear,directMonth,directDay,directHour,directMinute) VALUES (?,?,?,?,?,?,?,?)");
        addStatement.setString(1,message.getUsername());
        addStatement.setString(2,message.getReceiver());
        addStatement.setString(3,message.getText());
        addStatement.setInt(4,message.getDate().getYear());
        addStatement.setInt(5,message.getDate().getDayOfMonth());
        addStatement.executeUpdate();
        addStatement.close();
    }
    public void block(String blocker,String blocked) throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO blockInfo(blocker,blocked)VALUES (?,?)");
        addStatement.setString(1,blocker);
        addStatement.setString(2,blocked);
        addStatement.executeUpdate();
        addStatement.close();
    }

    public void unBlock(String blocker,String blocked) throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("DELETE FROM blockInfo WHERE blocker=? AND blocked=?");
        addStatement.setString(1,blocker);
        addStatement.setString(2,blocked);
        addStatement.executeUpdate();
        addStatement.close();
    }


    public void addHashtag(String username,HashtagInfo hashtag)throws SQLException{
        PreparedStatement addStatement = conn.prepareStatement("INSERT INTO hashtag(tweetID,hashtagName,hashtagYear,hashtagMonth,hashtagDay,hashtagHour,hashtagMinute)VALUES (?,?,?,?,?,?,?)");
        addStatement.setString(1,username);
        addStatement.setString(2,hashtag.hashtag);
        addStatement.setInt(3,hashtag.getDate().getYear());
        addStatement.setInt(4,hashtag.getDate().getMonthValue());
        addStatement.setInt(5,hashtag.getDate().getDayOfMonth());
        addStatement.setInt(6,hashtag.getDate().getHour());
        addStatement.setInt(7,hashtag.getDate().getMinute());
        addStatement.executeUpdate();
        addStatement.close();
    }

    public void like(Tweet post)throws SQLException{
        if(post instanceof Retweet){
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE retweets SET likeCount=? WHERE retweetID=?");
            updateStatement.setInt(1,post.getLikeCount()+1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        } else if (post instanceof Quote) {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE quotes SET likeCount=? WHERE quoteID=?");
            updateStatement.setInt(1,post.getLikeCount()+1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        } else if (post instanceof Reply) {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE replies SET likeCount=? WHERE retweetID=?");
            updateStatement.setInt(1,post.getLikeCount()+1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        }else {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE tweets SET likeCount=? WHERE tweetID=?");
            updateStatement.setInt(1,post.getLikeCount()+1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        }
    }

    public void unLike(Tweet post) throws SQLException{
        if(post instanceof Retweet){
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE retweets SET likeCount=? WHERE retweetID=?");
            updateStatement.setInt(1,post.getLikeCount()-1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        } else if (post instanceof Quote) {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE quotes SET likeCount=? WHERE quoteID=?");
            updateStatement.setInt(1,post.getLikeCount()-1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        } else if (post instanceof Reply) {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE replies SET likeCount=? WHERE retweetID=?");
            updateStatement.setInt(1,post.getLikeCount()-1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        }else {
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE tweets SET likeCount=? WHERE tweetID=?");
            updateStatement.setInt(1,post.getLikeCount()-1);
            updateStatement.setString(2,post.getTweetID());
            updateStatement.executeUpdate();
            updateStatement.close();
        }
    }
}
