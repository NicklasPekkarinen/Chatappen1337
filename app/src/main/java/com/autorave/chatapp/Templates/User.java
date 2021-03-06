package com.autorave.chatapp.Templates;

import java.util.List;

public class User {

        private String username;
        private String id;
        private String imageURL;
        private String email;
        private String status;
        //private List<String> groups;

        public User() {
            // Default constructor required for calls to DataSnapshot
        }

        public User(String username, String id, String email, String imageURL, String status) {
            this.username = username;
            this.id = id;
            this.email = email;
            this.imageURL = imageURL;
            this.status = status;
            //this.groups = groups;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getImageURL() {
            return imageURL;
        }

        public String getStatus() {
            return status;
        }

        /*public List<String> getGroups() {
            return groups;
        }*/
}


