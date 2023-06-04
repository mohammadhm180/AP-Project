import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Retweet extends Tweet implements Serializable {
    private String referredTweetID;

    public Retweet(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag);
        this.referredTweetID = referredTweetID;
    }

    public void setReferredTweetID(String referredTweetID) {
        this.referredTweetID = referredTweetID;
    }

    public Retweet(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID, int retweetCount, int replyCount, int likeCount,String retweetID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag,replyCount,retweetCount,likeCount,retweetID);
        this.referredTweetID = referredTweetID;
    }

    public String getReferredTweetID() {
        return referredTweetID;
    }
}