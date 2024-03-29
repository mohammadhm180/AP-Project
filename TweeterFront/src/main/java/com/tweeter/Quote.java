package com.tweeter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Quote extends Tweet implements Serializable {
    private String referredTweetID;
    private Tweet referredTweet;

    public Quote(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag);
        this.referredTweetID = referredTweetID;
    }

    public void setReferredTweetID(String referredTweetID) {
        this.referredTweetID = referredTweetID;
    }

    public Quote(String text, byte[] photo, byte[] video, LocalDateTime tweetDate, String authorUsername, ArrayList<String> hashtag, String referredTweetID, int retweetCount, int replyCount, int likeCount, Tweet referredTweet, String quoteID) {
        super(text, photo, video, tweetDate, authorUsername, hashtag,retweetCount, replyCount, likeCount,quoteID);
        this.referredTweetID = referredTweetID;
        this.referredTweet=referredTweet;
    }

    public String getReferredTweetID() {
        return referredTweetID;
    }

    public Tweet getReferredTweet() {
        return referredTweet;
    }

    public void setReferredTweet(Tweet referredTweet) {
        this.referredTweet = referredTweet;
    }
}