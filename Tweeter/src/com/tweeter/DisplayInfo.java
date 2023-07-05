package com.twitter;

import java.io.Serializable;

public class DisplayInfo implements Serializable {
    User user;
    Tweet tweet;

    public DisplayInfo(User user, Tweet tweet) {
        this.user = user;
        this.tweet = tweet;
    }

    public User getUser() {
        return user;
    }

    public Tweet getTweet() {
        return tweet;
    }
}
