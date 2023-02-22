package com.example.jerrysprendimai;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ObjectObject implements Parcelable {
    private Integer id;
    private String date, objectName, objectAddress,customerName,completeness,lockedByUserId, lockedUname, icon;

    protected ObjectObject(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        date = in.readString();
        objectName = in.readString();
        objectAddress = in.readString();
        customerName = in.readString();
        completeness = in.readString();
        lockedByUserId = in.readString();
        lockedUname = in.readString();
        icon = in.readString();
    }
    public static final Creator<ObjectObject> CREATOR = new Creator<ObjectObject>() {
        @Override
        public ObjectObject createFromParcel(Parcel in) {
            return new ObjectObject(in);
        }

        @Override
        public ObjectObject[] newArray(int size) {
            return new ObjectObject[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(date);
        dest.writeString(objectName);
        dest.writeString(objectAddress);
        dest.writeString(customerName);
        dest.writeString(completeness);
        dest.writeString(lockedByUserId);
        dest.writeString(lockedUname);
        dest.writeString(icon);
    }

    public ObjectObject(){

        this.id             = -1;
        this.date           = HelperDate.get_current_date_disply();
        this.objectName     = "";
        this.objectAddress  = "";
        this.customerName   = "";
        this.completeness   = "0.0";
        this.lockedByUserId = "";
        this.lockedUname    = "";
        this.icon           = "";
    }
    public ObjectObject(JSONObject obj, String wa){
        try {
            this.id             = Integer.parseInt(obj.getString("id"));
            this.date           = HelperDate.get_date_display(obj.getString("Date"));
            this.objectName     = obj.getString("ObjectName");
            this.customerName   = obj.getString("CustomerName");
            this.objectAddress  = obj.getString("ObjectAddress");
            this.completeness   = obj.getString("Completeness");
            this.lockedByUserId = obj.getString("LockedByUserId");
            this.lockedUname    = obj.getString("User");
            this.icon           = obj.getString("Icon");
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }
    public ObjectObject(JSONObject obj){
        try {
            this.id             = Integer.parseInt(MCrypt.decryptSingle(obj.getString("id")));
            this.date           = HelperDate.get_date_display(MCrypt.decryptSingle(obj.getString("Date")));
            this.objectName     = MCrypt.decryptSingle(obj.getString("ObjectName"));
            this.objectAddress  = MCrypt.decryptSingle(obj.getString("ObjectAddress"));
            this.customerName   = MCrypt.decryptSingle(obj.getString("CustomerName"));
            this.completeness   = MCrypt.decryptSingle(obj.getString("Completeness"));
            this.lockedByUserId = MCrypt.decryptSingle(obj.getString("LockedByUserId"));
            this.lockedUname    = MCrypt.decryptDouble(obj.getString("User"));
            this.icon           = MCrypt.decryptSingle(obj.getString("Icon"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //String decUname = Base64.encodeToString(MCrypt.decrypt(uname),  Base64.DEFAULT);
    }



    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",            this.getId().toString());
            jsonObject.put("date",          this.getDate());
            jsonObject.put("objectName",    this.getObjectName());
            jsonObject.put("objectAddress", this.getObjectAddress());
            jsonObject.put("customerName",  this.getCustomerName());
            jsonObject.put("completeness",  this.getCompleteness());
            jsonObject.put("lockedByUserId",this.getLockedByUserId());
            jsonObject.put("lockedUname",   this.getLockedUname());
            jsonObject.put("icon",          this.getIcon());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public Integer getId() {        return id;    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getLockedUname() {
        return lockedUname;
    }
    public void setLockedUname(String lockedUname) {
        this.lockedUname = lockedUname;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCompleteness() {
        return completeness;
    }
    public void setCompleteness(String completeness) {
        this.completeness = completeness;
    }
    public String getLockedByUserId() {
        return lockedByUserId;
    }
    public void setLockedByUserId(String lockedByUserId) {
        this.lockedByUserId = lockedByUserId;
    }
    public String getObjectAddress() {
        return objectAddress;
    }
    public void setObjectAddress(String objectAddress) {
        this.objectAddress = objectAddress;
    }
    public String getIcon() {        return icon;    }
    public void setIcon(String icon) {        this.icon = icon;    }


}
