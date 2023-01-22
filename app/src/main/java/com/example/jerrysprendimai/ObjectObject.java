package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectObject implements Parcelable {
    private Integer id;
    private String date, objectName,customerName,completeness,lockedByUserId, lockedUname;

    protected ObjectObject(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        date = in.readString();
        objectName = in.readString();
        customerName = in.readString();
        completeness = in.readString();
        lockedByUserId = in.readString();
        lockedUname = in.readString();
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
        dest.writeString(customerName);
        dest.writeString(completeness);
        dest.writeString(lockedByUserId);
        dest.writeString(lockedUname);
    }

    public ObjectObject(){
        this.id             = -1;
        this.date           = "";
        this.objectName     = "";
        this.customerName   = "";
        this.completeness   = "";
        this.lockedByUserId = "0";
        this.lockedUname    = "";
    }
    public ObjectObject(JSONObject obj, String wa){
        try {
            this.id             = Integer.parseInt(obj.getString("id"));
            this.date           = DateHelper.get_date_display(obj.getString("Date"));
            this.objectName     = obj.getString("ObjectName");
            this.customerName   = obj.getString("CustomerName");
            this.completeness   = obj.getString("Completeness");
            this.lockedByUserId = obj.getString("LockedByUserId");
            this.lockedUname    = obj.getString("User");
        }catch (Exception ee){
            ee.printStackTrace();
        }
    }
    public ObjectObject(JSONObject obj){
        try {
            this.id             = Integer.parseInt(MCrypt.decryptSingle(obj.getString("id")));
            this.date           = DateHelper.get_date_display(MCrypt.decryptSingle(obj.getString("Date")));
            this.objectName     = MCrypt.decryptSingle(obj.getString("ObjectName"));
            this.customerName   = MCrypt.decryptSingle(obj.getString("CustomerName"));
            this.completeness   = MCrypt.decryptSingle(obj.getString("Completeness"));
            this.lockedByUserId = MCrypt.decryptSingle(obj.getString("LockedByUserId"));
            this.lockedUname    = MCrypt.decryptDouble(obj.getString("User"));

        }catch (JSONException e) {
            e.printStackTrace();
        }
        //String decUname = Base64.encodeToString(MCrypt.decrypt(uname),  Base64.DEFAULT);
    }

    public Integer getId() {
        return id;
    }

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


}
