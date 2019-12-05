package com.autorave.chatapp.Activitys;

import com.autorave.chatapp.Templates.User;

import java.util.List;

public class Group {

    private String id;
    private List<User> members;
    private String name;


    public Group() {
        // Default constructor required for calls to DataSnapshot
    }

    public Group(String id, List<User> members, String name) {
        this.id = id;
        this.members = members;
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
