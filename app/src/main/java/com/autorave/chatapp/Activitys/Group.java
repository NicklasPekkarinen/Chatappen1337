package com.autorave.chatapp.Activitys;

import java.util.List;

public class Group {

    private String id;
    private List<String> members;

    public Group() {
        // Default constructor required for calls to DataSnapshot
    }

    public Group(String id, List<String> members) {
        this.id = id;
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
