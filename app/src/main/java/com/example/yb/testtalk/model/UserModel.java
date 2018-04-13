package com.example.yb.testtalk.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yb on 2018-01-19.
 */




public class UserModel {

    public String userName;
    public String profileImageUrl;
    public String uid;
    public String pushToken;
    public String comment;
    public String permission;

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}