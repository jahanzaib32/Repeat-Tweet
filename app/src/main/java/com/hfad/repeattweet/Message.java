package com.hfad.repeattweet;

public class Message {
    private String text;
    private String date;


    private MemberData memberData;
    private boolean belongsToCurrentUser;

    public Message(String text, MemberData data, boolean belongsToCurrentUser, String date) {
        this.text = text;
        this.memberData = data;
        this.date = date;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getDate() {
        return date;
    }
    public String getText() {
        return text;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
