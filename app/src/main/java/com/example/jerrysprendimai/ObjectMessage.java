package com.example.jerrysprendimai;

import java.util.ArrayList;

public class ObjectMessage {

    private String firstName, uname, userId, content, date, time, mills, userLv;
    private String[] tmp;
    private ArrayList<ObjectMessage.User> userSeen;
    public String key;
    public String picUrl, picUri, picName;
    public boolean deleted;
    public MyAdapterMessage.MessageHolder holder;

    public ObjectMessage(String firstName, String uname, String userId, String content, String date, String time, String mills, String userLv, String picUrl, String picUri, String picName, boolean deleted) {
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
    }

    public ObjectMessage() {
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

}
