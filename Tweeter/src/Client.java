import io.jsonwebtoken.io.IOException;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

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
        ObjectOutputStream OOS = new ObjectOutputStream(server.getOutputStream());
        ObjectInputStream OIS = new ObjectInputStream(server.getInputStream());
        EnterProgram enterProgram = new EnterProgram(file, OOS, OIS);
        Menu menu = new Menu(enterProgram, OOS, OIS);
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
            String result = (String) OIS.readObject();
            if (result.equals("success")) {
                User client = (User) OIS.readObject();
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

    public Menu(EnterProgram enterProgram, ObjectOutputStream OOS, ObjectInputStream OIS) {
        this.token = null;
        this.user = null;
        this.enterProgram = enterProgram;
        this.OIS = OIS;
        this.OOS = OOS;
        enterProgram.setMenu(this);
    }

    public void startMenu() throws ClassNotFoundException, java.io.IOException {
        Scanner scanner = new Scanner(System.in);
        String choice;
        while (true) {
            System.out.println("1-show timeline\n2-tweet\n3-set avatar\n4-set header\n5-set bio\n6-show followers\n7-show followings\n8-retweet\n9-quote\n10-reply\n11-follow\n12-unfollow\n13-block\n14-unblock\n15-direct\n16-remove tweet\n17-remove retweet\n18-remove quote\n19-remove reply\n20-show replies\n21-like\n22-unlike\n23-add vote\n24-vote\n25-removeVote");
            choice = scanner.nextLine();


            if (choice.equals("1")) {
                //getfavstars here
                //first we call fetch client to get new following or new tweets or followings
                //check not showing blocked users favstar

            } else if (choice.equals("2")) {
                if (!checkToken(OIS, OOS)) {
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
                        byte[] photo = null;
                        if (photoFile.exists()) {
                            FileInputStream in = new FileInputStream(photoFile);
                            photo = new byte[(int) photoFile.length()];
                            in.read(photo);
                        }

                        File videoFile = new File(videoDirectory);
                        byte[] video = null;
                        if (videoFile.exists()) {
                            FileInputStream in = new FileInputStream(videoFile);
                            video = new byte[(int) videoFile.length()];
                            in.read(video);
                            in.close();
                        }
                        Tweet tweet = new Tweet(text, photo, video, LocalDateTime.now(), user.getUsername(), extractHashtags(text));
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(tweet);
                        if ((OIS.readObject()).equals("tweeted successfully")) {
                            user.getTweets().add(tweet);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.err.println("class not found");
                    }
                }

            } else if (choice.equals("3")) {
                if (!checkToken(OIS, OOS)) {
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
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else if (choice.equals("4")) {
                if (!checkToken(OIS, OOS)) {
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
                    } catch (java.io.IOException e) {
                        System.err.println("file not found");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("5")) {
                if (!checkToken(OIS, OOS)) {
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
                    } catch (ClassNotFoundException e) {
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("6")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    for (User user1 : user.getFollowers()) {
                        System.out.println(user1.getUsername() + "\t" + user1.getFirstName() + " " + user1.getLastName());
                    }
                }
            } else if (choice.equals("7")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    for (User user1 : user.getFollowings()) {
                        System.out.println(user1.getUsername() + "\t" + user1.getFirstName() + " " + user1.getLastName());
                    }
                }
            } else if (choice.equals("8")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("tweetID: ");
                    String tweetID = scanner.nextLine();
                    OOS.writeObject(new String("getTweet"));
                    OOS.writeObject(tweetID);
                    Tweet tweet=(Tweet) OIS.readObject();
                    Retweet retweet=new Retweet(tweet.getText(),tweet.getPhoto(),tweet.getVideo(),tweet.getTweetDate(),user.getUsername(),tweet.getHashtag(),tweet.getTweetID(),tweet.getRetweetCount(),tweet.getReplyCount(),tweet.getLikeCount());
                    OOS.writeObject(new String("addTweet"));
                    OOS.writeObject(retweet);
                    if ((OIS.readObject()).equals("tweeted successfully")) {
                        user.getTweets().add(retweet);
                    }
                }
            } else if (choice.equals("9")) {
                if (!checkToken(OIS, OOS)) {
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
                        byte[] photo = null;
                        if (photoFile.exists()) {
                            FileInputStream in = new FileInputStream(photoFile);
                            photo = new byte[(int) photoFile.length()];
                            in.read(photo);
                        }

                        File videoFile = new File(videoDirectory);
                        byte[] video = null;
                        if (videoFile.exists()) {
                            FileInputStream in = new FileInputStream(videoFile);
                            video = new byte[(int) videoFile.length()];
                            in.read(video);
                            in.close();
                        }
                        Quote quote = new Quote(text, photo, video, LocalDateTime.now(), user.getUsername(),extractHashtags(text), referredTweetID);
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(quote);
                        if ((OIS.readObject()).equals("tweeted successfully")) {
                            user.getTweets().add(quote);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.out.println("class not found");
                    }
                }
            } else if (choice.equals("10")) {
                if (!checkToken(OIS, OOS)) {
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
                        byte[] photo = null;
                        if (photoFile.exists()) {
                            FileInputStream in = new FileInputStream(photoFile);
                            photo = new byte[(int) photoFile.length()];
                            in.read(photo);
                        }

                        File videoFile = new File(videoDirectory);
                        byte[] video = null;
                        if (videoFile.exists()) {
                            FileInputStream in = new FileInputStream(videoFile);
                            video = new byte[(int) videoFile.length()];
                            in.read(video);
                            in.close();
                        }
                        Reply reply = new Reply(text, photo, video, LocalDateTime.now(), user.getUsername(),extractHashtags(text), referredTweetID);
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(reply);
                        if ((OIS.readObject()).equals("tweeted successfully")) {
                            user.getTweets().add(reply);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.out.println("class not found");
                    }
                }
            } else if (choice.equals("11")) {
                if (!checkToken(OIS, OOS)) {
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
                        //adding to user followings
                        OOS.writeObject(new String("fetchUser"));
                        OOS.writeObject(username);
                        User following = (User) OIS.readObject();
                        user.getFollowings().add(following);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.err.println("not found");
                    }
                }
            } else if (choice.equals("12")) {
                if (!checkToken(OIS, OOS)) {
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
                        //remove from users following
                        Iterator<User> following = user.getFollowings().iterator();
                        while (following.hasNext()) {
                            if (following.next().getUsername().equals(username)) {
                                following.remove();
                                break;
                            }
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.err.println("?");
                    }
                }
            } else if (choice.equals("13")) {
                if (!checkToken(OIS, OOS)) {
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
                        user.getBlockedUsers().add(username);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("14")) {
                if (!checkToken(OIS, OOS)) {
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
                        user.getBlockedUsers().remove(username);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("15")) {
                if (!checkToken(OIS, OOS)) {
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
                        OOS.writeObject(new Message(user.getUsername(), receiver, text, LocalDateTime.now()));
                        //add to user directs
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("16")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("tweetID: ");
                    String tweetID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for (Tweet tweet : user.getTweets()) {
                        if (tweet.getTweetID().equals(tweetID)) {
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        //remove from user
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("17")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("retweetID: ");
                    String retweetID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for (Tweet tweet : user.getTweets()) {
                        if (tweet.getTweetID().equals(retweetID)) {
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        //remove from user
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("18")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("quoteID: ");
                    String quoteID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for (Tweet tweet : user.getTweets()) {
                        if (tweet.getTweetID().equals(quoteID)) {
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        //remove from user
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("19")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("replyID: ");
                    String replyID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for (Tweet tweet : user.getTweets()) {
                        if (tweet.getTweetID().equals(replyID)) {
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        //remove from user
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("20")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.println("enter tweetID to show replies:");
                    String tweetID = scanner.nextLine();
                    OOS.writeObject(tweetID);
                    ArrayList<Reply> replies = (ArrayList<Reply>) OIS.readObject();
                    for (Reply reply : replies)
                        showTweet(reply);
                }
            } else if (choice.equals("21")) {
                String command = "like";
                System.out.print("tweetID: ");
                String tweetID = scanner.nextLine();
                OOS.writeObject(command);
                OOS.writeObject(tweetID);
            } else if (choice.equals("22")) {
                String command = "unlike";
                System.out.print("tweetID: ");
                String tweetID = scanner.nextLine();
                OOS.writeObject(command);
                OOS.writeObject(tweetID);
            } else if (choice.equals("23")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    String text, photoDirectory, videoDirectory;
                    System.out.print("text: ");
                    text = scanner.nextLine();
                    System.out.print("photo: ");
                    photoDirectory = scanner.nextLine();
                    System.out.println("video: ");
                    System.out.print("option 1: ");
                    String option1 = scanner.nextLine();
                    System.out.print("option 2: ");
                    String option2 = scanner.nextLine();
                    System.out.print("option 3: ");
                    String option3 = scanner.nextLine();
                    System.out.print("option 4: ");
                    String option4 = scanner.nextLine();
                    videoDirectory = scanner.nextLine();
                    try {
                        File photoFile = new File(photoDirectory);
                        byte[] photo = null;
                        if (photoFile.exists()) {
                            FileInputStream in = new FileInputStream(photoFile);
                            photo = new byte[(int) photoFile.length()];
                            in.read(photo);
                        }

                        File videoFile = new File(videoDirectory);
                        byte[] video = null;
                        if (videoFile.exists()) {
                            FileInputStream in = new FileInputStream(videoFile);
                            video = new byte[(int) videoFile.length()];
                            in.read(video);
                            in.close();
                        }
                        Vote vote = new Vote(text, photo, video, LocalDateTime.now(), user.getUsername(), extractHashtags(text), option1, option2, option3, option4);
                        String command = "addTweet";
                        OOS.writeObject(command);
                        OOS.writeObject(vote);
                        if ((OIS.readObject()).equals("tweeted successfully")) {
                            user.getTweets().add(vote);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("file not found");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.err.println("class not found");
                    }
                }
            } else if (choice.equals("24")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.println("voteID:");
                    String voteID = scanner.nextLine();
                    System.out.println("option number:");
                    Integer optionNumber = scanner.nextInt();
                    scanner.nextLine();
                    OOS.writeObject("vote");
                    OOS.writeObject(voteID);
                    OOS.writeObject(optionNumber);
                }
            } else if (choice.equals("25")) {
                if (!checkToken(OIS, OOS)) {
                    System.out.println("invalid token");
                    enterProgram.enter();
                } else {
                    System.out.print("voteID: ");
                    String voteID = scanner.nextLine();
                    String command = "removeTweet";
                    Tweet t = null;
                    for (Tweet tweet : user.getTweets()) {
                        if (tweet.getTweetID().equals(voteID)) {
                            t = tweet;
                            break;
                        }
                    }
                    try {
                        OOS.writeObject(command);
                        OOS.writeObject(t);
                        //remove from user
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choice.equals("n")) {
                break;
            } else {
                System.out.println("wrong input");
            }
        }
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

    public ArrayList<String> extractHashtags(String text) {
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split("\\s+")));
        ArrayList<String> hashtags = new ArrayList<String>();
        for (String word : words) {
            if (word.startsWith("#")) {
                hashtags.add(word);
            }
        }
        return hashtags;
    }

    public void sortByDate(ArrayList<Tweet> tweets) {
        for (int i = 0; i < tweets.size() - 1; i++) {
            for (int j = 0; j < tweets.size() - i - 1; j++) {
                if (tweets.get(j).getTweetDate().compareTo(tweets.get(j + 1).getTweetDate()) < 0) {
                    Tweet tmp = tweets.get(j);
                    tweets.set(i, tweets.get(j + 1));
                    tweets.set(i + 1, tmp);
                }
            }
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setClient(User user) {
        this.user = user;
    }

    public void showTweet(Tweet tweet) throws java.io.IOException, ClassNotFoundException {
        if (tweet instanceof Retweet) {
            //getting referred tweet
            OOS.writeObject(new String("getTweet"));
            OOS.writeObject(new String(((Retweet) tweet).getReferredTweetID()));
            Tweet referredTweet = (Tweet) OIS.readObject();
            //getting retweet owner name
            OOS.writeObject("getName");
            OOS.writeObject(tweet.getAuthorUsername());
            String name = (String) OIS.readObject();
            System.out.println(name + " retweeted");
            //showing referred tweet
            showTweet(referredTweet);
        } else if (tweet instanceof Quote) {
            //getting referred tweet
            OOS.writeObject(new String("getTweet"));
            OOS.writeObject(new String(((Quote) tweet).getReferredTweetID()));
            Tweet referredTweet = (Tweet) OIS.readObject();
            //getting quote owner
            OOS.writeObject("getName");
            OOS.writeObject(tweet.getAuthorUsername());
            String quoteOwnerName = (String) OIS.readObject();
            //getting referred tweet owner names
            OOS.writeObject("getName");
            OOS.writeObject(referredTweet.getAuthorUsername());
            String referredOwnerName = (String) OIS.readObject();
            //show result
            System.out.println(tweet.getTweetID());
            System.out.println();
            System.out.println(quoteOwnerName + "  " + "@" + tweet.getAuthorUsername());
            System.out.println(tweet.getText());
            //show referred tweet
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println(referredTweet.getTweetID());
            System.out.println();
            System.out.println(referredOwnerName + "  " + "@" + referredTweet.getAuthorUsername());
            System.out.println(tweet.getText());
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("replies:" + tweet.getReplyCount() + "  retweets:" + tweet.getRetweetCount() + "  likes:" + tweet.getLikeCount());
            System.out.println();
        } else if (tweet instanceof Reply) {
            OOS.writeObject("getName");
            OOS.writeObject(tweet.getAuthorUsername());
            String replyOwnerName = (String) OIS.readObject();
            OOS.writeObject("getWriterUsername");
            OOS.writeObject(new String(((Reply) tweet).getReferredTweetID()));
            String referredOwnerUsername = (String) OIS.readObject();
            //show reply
            System.out.println(tweet.getTweetID());
            System.out.println();
            System.out.println(replyOwnerName+"  @"+tweet.getAuthorUsername());
            System.out.println("replying to  @" + referredOwnerUsername);
            System.out.println(tweet.getText());
            System.out.println("replies:" + tweet.getReplyCount() + "  retweets:" + tweet.getRetweetCount() + "  likes:" + tweet.getLikeCount());
            System.out.println();
        } else if(tweet instanceof Vote){
            float allVotesCount=((Vote) tweet).getOption1Count()+ ((Vote) tweet).getOption2Count()+ ((Vote) tweet).getOption3Count()+ ((Vote) tweet).getOption4Count();
            float option1percent=0;
            float option2percent=0;
            float option3percent=0;
            float option4percent=0;
            if(allVotesCount!=0){
                 option1percent=((Vote) tweet).getOption1Count()/allVotesCount;
                 option2percent=((Vote) tweet).getOption2Count()/allVotesCount;
                 option3percent=((Vote) tweet).getOption3Count()/allVotesCount;
                 option4percent=((Vote) tweet).getOption4Count()/allVotesCount;
            }
            OOS.writeObject("getName");
            OOS.writeObject(tweet.getAuthorUsername());
            String tweetOwnerName = (String) OIS.readObject();
            System.out.println(tweet.getTweetID());
            System.out.println();
            System.out.println(tweetOwnerName + "  " + "@" + tweet.getAuthorUsername());
            System.out.println(tweet.getText());
            System.out.println(((Vote) tweet).getOption1()+": "+option1percent);
            System.out.println(((Vote) tweet).getOption2()+": "+option2percent);
            System.out.println(((Vote) tweet).getOption3()+": "+option3percent);
            System.out.println(((Vote) tweet).getOption4()+": "+option4percent);
            System.out.println("replies:" + tweet.getReplyCount() + "  retweets:" + tweet.getRetweetCount() + "  likes:" + tweet.getLikeCount()+" votes:"+allVotesCount);
            System.out.println();
        }else {
            OOS.writeObject("getName");
            OOS.writeObject(tweet.getAuthorUsername());
            String tweetOwnerName = (String) OIS.readObject();
            System.out.println(tweet.getTweetID());
            System.out.println();
            System.out.println(tweetOwnerName + "  " + "@" + tweet.getAuthorUsername());
            System.out.println(tweet.getText());
            System.out.println("replies:" + tweet.getReplyCount() + "  retweets:" + tweet.getRetweetCount() + "  likes:" + tweet.getLikeCount());
            System.out.println();
        }
    }
}

class EnterProgram {
    File file;
    ObjectInputStream OIS;
    ObjectOutputStream OOS;
    Menu menu;

    public EnterProgram(File file, ObjectOutputStream OOS, ObjectInputStream OIS) {
        this.file = file;
        this.OOS = OOS;
        this.OIS = OIS;
        this.menu = null;
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
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
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
                } catch (java.io.IOException e) {
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
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
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