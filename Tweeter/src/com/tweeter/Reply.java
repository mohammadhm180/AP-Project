package com.twitter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reply extends Tweet implements Serializable {
    private String referredTweetID;

    public Reply(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag);
        this.referredTweetID = referredTweetID;
    }
    public Reply(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID,int retweetCount,int replyCount,int likeCount,String replyID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag, retweetCount, replyCount, likeCount,replyID);
        this.referredTweetID = referredTweetID;
    }

    public String getReferredTweetID() {
        return referredTweetID;
    }
}