import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Tweet {
    private String tweetID;
    private String text;
    private byte[] photo;
    private byte[] video;
    private ArrayList<Tweet> replies;
    private int retweetCount;
    private int replyCount;
    private LocalDateTime tweetDate;
    private String authorUsername;
    private ArrayList<String> hashtag;
    private int likeCount;

    public Tweet(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag) {
        this.tweetID = UUID.randomUUID().toString();
        this.text = text;
        this.photo = photo;
        this.video = video;
        this.replies = new ArrayList<>();
        this.retweetCount = 0;
        this.replyCount = 0;
        this.tweetDate = tweetDate;
        this.authorUsername = authorUsername;
        this.hashtag = hashtag;
        likeCount=0;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Tweet(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, int replyCount, int retweetCount, int likeCount) {
        this.tweetID = UUID.randomUUID().toString();
        this.text = text;
        this.photo = photo;
        this.video = video;
        this.replies = new ArrayList<>();
        this.retweetCount = retweetCount;
        this.replyCount = replyCount;
        this.tweetDate = tweetDate;
        this.authorUsername = authorUsername;
        this.hashtag = hashtag;
        this.likeCount=likeCount;
    }


    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getTweetID() {
        return tweetID;
    }

    public String getText() {
        return text;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public byte[] getVideo() {
        return video;
    }

    public ArrayList<Tweet> getReplies() {
        return replies;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public LocalDateTime getTweetDate() {
        return tweetDate;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public ArrayList<String> getHashtag() {
        return hashtag;
    }
}

