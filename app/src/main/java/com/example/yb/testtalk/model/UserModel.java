package com.example.yb.testtalk.model;

/**
 * Created by yb on 2018-01-19.
 */




public class UserModel {

    public String aausername;
    public String profileImageUrl;
    public String uid;
    public String pushToken;
    public String comment;
    public String apermission;

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}