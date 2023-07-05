package com.tweeter;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HashtagInfo implements Serializable {
    String hashtag;
    int count;
    LocalDateTime date;

    public HashtagInfo(String hashtag, int count, LocalDateTime date) {
        this.hashtag = hashtag;
        this.count = count;
        this.date = date;
    }

    public String getHashtag() {
        return hashtag;
    }

    public int getCount() {
        return count;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
