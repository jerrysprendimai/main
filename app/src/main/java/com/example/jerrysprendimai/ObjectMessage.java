package com.example.jerrysprendimai;

public class ObjectMessage {

    private String firstName, uname, userId, content, date, time, mills, userLv;

    public ObjectMessage(String firstName, String uname, String userId, String content, String date, String time, String mills, String userLv) {
        this.firstName = firstName;
        this.uname = uname;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.time = time;
        this.mills = mills;
        this.userLv = userLv;
    }

    public ObjectMessage() {
    }

    public String getUserLv() {        return userLv;    }
    public void setUserLv(String userLv) {        this.userLv = userLv;    }
    public String getFirstName() { return firstName;   }
    public void setFirstName(String firstName) {    this.firstName = firstName;   }
    public String getUname() {      return uname;   }
    public void setUname(String uname) {      this.uname = uname;    }
    public String getUserId() {        return userId;    }
    public void setUserId(String userId) {        this.userId = userId;    }
    public String getContent() {        return content;    }
    public void setContent(String content) {        this.content = content;    }
    public String getDate() {        return date;    }
    public void setDate(String date) {        this.date = date;   }
    public String getTime() {        return time;    }
    public void setTime(String time) {        this.time = time;    }
    public String getMills() {        return mills;    }
    public void setMills(String mills) {        this.mills = mills;    }
}