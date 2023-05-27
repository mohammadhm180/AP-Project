import java.time.LocalDateTime;
import java.util.ArrayList;

public class Quote extends Tweet{
    private String referredTweetID;

    public Quote(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag);
        this.referredTweetID = referredTweetID;
    }

    public Quote(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID,int retweetCount,int replyCount,int likeCount,ArrayList<Tweet>replies) {
        super(text, photo, video, tweetDate, authorUsername, hashtag,retweetCount, replyCount, likeCount,replies);
        this.referredTweetID = referredTweetID;
    }

    public String getReferredTweetID() {
        return referredTweetID;
    }
}
