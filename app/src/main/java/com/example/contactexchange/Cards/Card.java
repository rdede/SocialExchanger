package com.example.contactexchange.Cards;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Card {
    private String name;
    private String uid;
    private ArrayList<Boolean> socials;
    private ArrayList<String> uidsAdded;

    public Card(String name, String uid, ArrayList<Boolean> socials, ArrayList<String> uidsAdded) {
        this.name = name;
        this.uid = uid;
        this.socials = socials;
        this.uidsAdded = uidsAdded;
    }

    public Card() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Boolean> getSocials() {
        return socials;
    }

    public void setSocials(ArrayList<Boolean> socials) {
        this.socials = socials;
    }

    public ArrayList<String> getUidsAdded() {
        return uidsAdded;
    }

    public void setUidsAdded(ArrayList<String> uidsAdded) {
        this.uidsAdded = uidsAdded;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
