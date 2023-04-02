package com.example.jerrysprendimai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObjectMessage {

    private String firstName, uname, userId, content, date, time, mills, userLv;
    private String[] tmp;
    private ArrayList<ObjectMessage.User> userSeen;
    public String key;
    public String picUrl, picUri, picName;
    public boolean deleted;
    public MyAdapterMessage.MessageHolder holder;
    public HashMap<String, String> users;
    public int picturePosition;
    public boolean dateSeparator;

    public ObjectMessage(String firstName, String uname, String userId, String content, String date, String time, String mills, String userLv, String picUrl, String picUri, String picName, boolean deleted, HashMap users) {
        this.firstName = firstName;
        this.uname = uname;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.time = time;
        this.mills = mills;
        this.userLv = userLv;
        this.picUrl = picUrl;
        this.picUri = picUri;
        this.picName = picName;
        this.deleted = deleted;
        this.users = users;
    }

    public ObjectMessage() {
    }

    public ObjectMessage(Map<String, String> map) {
        this.firstName = map.get("firstName");
        this.uname = map.get("uname");
        this.userId = map.get("userId");
        this.content = map.get("content");
        this.date = map.get("date");
        this.time = map.get("time");
        this.mills = map.get("mills");
        this.userLv = map.get("userLv");
        this.picUrl = map.get("picUrl");
        this.picUri = map.get("picUri");
        this.picName = map.get("picName");
        this.deleted = Boolean.parseBoolean(map.get("deleted"));
        //this.users = map.get("users");
    }

    public ObjectMessage(Object fieldsObj) {
        this.setFirstName((String) ((HashMap<?, ?>) fieldsObj).get("firstName"));
        this.setUname((String) ((HashMap<?, ?>) fieldsObj).get("uname"));
        this.setUserId((String) ((HashMap<?, ?>) fieldsObj).get("userId"));
        this.setContent((String) ((HashMap<?, ?>) fieldsObj).get("content"));
        this.setDate((String) ((HashMap<?, ?>) fieldsObj).get("date"));
        this.setTime((String) ((HashMap<?, ?>) fieldsObj).get("time"));
        this.setMills((String) ((HashMap<?, ?>) fieldsObj).get("mills"));
        this.setUserLv((String) ((HashMap<?, ?>) fieldsObj).get("userLv"));
        this.setPicUrl((String) ((HashMap<?, ?>) fieldsObj).get("picUrl"));
        this.setPicUri((String) ((HashMap<?, ?>) fieldsObj).get("picUri"));
        this.setPicName((String) ((HashMap<?, ?>) fieldsObj).get("picName"));
        this.setDeleted((Boolean) ((HashMap<?, ?>) fieldsObj).get("deleted"));
        this.setUsers((HashMap) ((HashMap<?, ?>) fieldsObj).get("users"));
    }

    public static class User{
        String userId;
        boolean seen;

        public User(String userId, boolean seen) {
            this.userId = userId;
            this.seen = seen;
        }
        public String getUserId() {            return userId;        }
        public void setUserId(String userId) { this.userId = userId;        }
        public boolean isSeen() {              return seen;        }
        public void setSeen(boolean seen) {    this.seen = seen;        }
    }

    public String getUserLv() {   return userLv;    }
    public void setUserLv(String userLv) {   this.userLv = userLv;    }
    public String getFirstName() { return firstName;   }
    public void setFirstName(String firstName) {    this.firstName = firstName;   }
    public String getUname() {    return uname;   }
    public void setUname(String uname) {    this.uname = uname;    }
    public String getUserId() {   return userId;    }
    public void setUserId(String userId) {    this.userId = userId;    }
    public String getContent() {   return content;    }
    public void setContent(String content) {    this.content = content;    }
    public String getDate() {   return date;    }
    public void setDate(String date) {    this.date = date;   }
    public String getTime() {   return time;    }
    public void setTime(String time) {    this.time = time;    }
    public String getMills() {  return mills;    }
    public void setMills(String mills) {    this.mills = mills;    }
    public String getKey() {    return key;    }
    public void setKey(String key) {     this.key = key;    }
    public boolean isDeleted() {    return deleted;    }
    public void setDeleted(boolean deleted) {    this.deleted = deleted;    }
    public String getPicUrl() {   return picUrl;    }
    public void setPicUrl(String picUrl) {   this.picUrl = picUrl;    }
    public MyAdapterMessage.MessageHolder getHolder() {    return holder;    }
    public void setHolder(MyAdapterMessage.MessageHolder holder) {    this.holder = holder;    }
    public String getPicUri() {        return picUri;    }
    public void setPicUri(String picUri) {        this.picUri = picUri;    }
    public String getPicName() {        return picName;    }
    public void setPicName(String picName) {        this.picName = picName;    }
    public HashMap getUsers() {        return users;    }
    public void setUsers(HashMap users) {        this.users = users;    }
    public int getPicturePosition() {        return picturePosition;    }
    public void setPicturePosition(int picturePosition) {        this.picturePosition = picturePosition;    }
    public boolean isDateSeparator() {        return dateSeparator;    }
    public void setDateSeparator(boolean dateSeparator) {        this.dateSeparator = dateSeparator;    }
}
