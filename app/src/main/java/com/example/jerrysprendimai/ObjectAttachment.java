package com.example.jerrysprendimai;

import android.content.Intent;

import org.json.JSONObject;

public class ObjectAttachment {

    Integer emailId;
    String name, url, mimeType;

    public ObjectAttachment() {
        this.emailId = -1;
        this.name = "";
        this.url = "";
        this.mimeType = "";
    }

    public ObjectAttachment(JSONObject obj, Integer emailId) {
        this.emailId = emailId;
        try {
            this.name     = obj.getString("fileName");
            this.url      = obj.getString("url");
            this.mimeType = obj.getString("contentType");
            //this.name   = MCrypt.decryptSingle(obj.getString("fileName"));
            //this.url    = MCrypt.decryptSingle(obj.getString("url"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getEmailId() {        return emailId;    }
    public void setEmailId(Integer emailId) {        this.emailId = emailId;    }
    public String getName() {        return name;    }
    public void setName(String name) {        this.name = name;    }
    public String getUrl() {        return url;    }
    public void setUrl(String url) {        this.url = url;    }
    public String getMimeType() {        return mimeType;    }
    public void setMimeType(String mimeType) {        this.mimeType = mimeType;    }

}
