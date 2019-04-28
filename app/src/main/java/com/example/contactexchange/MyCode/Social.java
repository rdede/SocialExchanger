package com.example.contactexchange.MyCode;

public class Social {
    private int socialId;
    private String username;
    private String id;
    private String uid;

    public Social(int socialId, String username, String id, String uid) {
        this.socialId = socialId;
        this.username = username;
        this.id = id;
        this.uid = uid;
    }

    public Social(int socialId, String username, String uid) {
        this.socialId = socialId;
        this.username = username;
        this.uid = uid;
    }

    public Social() {
    }

    public int getSocialId() {
        return socialId;
    }

    public void setSocialId(int socialId) {
        this.socialId = socialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
