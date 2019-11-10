package com.autorave.chatapp;

    public class User {

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String username;


        public User() {
            // Default constructor required for calls to DataSnapshot
        }

        public User(String username) {
            this.username = username;

        }

    }


