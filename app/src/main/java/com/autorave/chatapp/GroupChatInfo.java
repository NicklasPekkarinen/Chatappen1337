package com.autorave.chatapp;

import java.util.List;

public class GroupChatInfo {

    private String sender;
    private List<String> receivers;
    private String message;
    private boolean isseen;

    public GroupChatInfo(String sender, List<String> receivers, String message, boolean isseen) {
        this.sender = sender;
        this.receivers = receivers;
        this.message = message;
        this.isseen = isseen;
    }

    public GroupChatInfo() {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

}
