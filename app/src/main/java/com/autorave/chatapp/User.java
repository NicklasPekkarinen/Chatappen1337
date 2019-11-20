package com.autorave.chatapp;

    public class User {

        private String username;
        private String id;
        private String imageURL;
        private String email;

        public User() {
            // Default constructor required for calls to DataSnapshot
        }

        public User(String username, String id, String email, String imageURL) {
            this.username = username;
            this.id = id;
            this.email = email;
            this.imageURL = imageURL;
        }

        public String getUsername() {
            return username;
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
    }


