package com.autorave.chatapp;

    public class User {

        private String username;
        private String id;

        public User() {
            // Default constructor required for calls to DataSnapshot
        }

        public User(String username, String id) {
            this.username = username;
            this.id = id;
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
    }


