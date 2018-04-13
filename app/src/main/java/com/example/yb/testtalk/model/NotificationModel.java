package com.example.yb.testtalk.model;

/**
 * Created by yb on 2018-01-20.
 */

public class NotificationModel {
    public String to;

    public Notification notification = new Notification();
    public Data data = new Data();
    public static class Notification {
        public String title;
        public String text;
    }
    public static class Data{
        public String title;
        public String text;
    }

}