package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectObjUser implements Parcelable {
    private Integer id, objectId, userId;
    private String assignmentDate, assigmentUserId, uname, fName, lName, token;

    protected ObjectObjUser(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            objectId = null;
        } else {
            objectId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        assignmentDate = in.readString();
        assigmentUserId = in.readString();
        uname = in.readString();
        fName = in.readString();
        lName = in.readString();
        token = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (objectId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(objectId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(assignmentDate);
        dest.writeString(assigmentUserId);
        dest.writeString(uname);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(token);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<ObjectObjUser> CREATOR = new Creator<ObjectObjUser>() {
        @Override
        public ObjectObjUser createFromParcel(Parcel in) {
            return new ObjectObjUser(in);
        }

        @Override
        public ObjectObjUser[] newArray(int size) {
            return new ObjectObjUser[size];
        }
    };

    public ObjectObjUser(){
        this.id              = -1;
        this.objectId        = -1;
        this.userId          = -1;
        this.assignmentDate  = "";
        this.assigmentUserId = "";
        this.uname           = "";
        this.fName           = "";
        this.lName           = "";
        this.token           = "";
    }

    public ObjectObjUser(JSONObject obj){
        try {
            this.id              = Integer.parseInt(obj.getString("id"));
            this.objectId        = Integer.parseInt(obj.getString("Object_id"));
            this.userId          = Integer.parseInt(obj.getString("User_id"));
            this.assignmentDate  = obj.getString("AssignmentDate");
            this.assigmentUserId = obj.getString("AssigmentUserID");
            this.uname           = obj.getString("User");
            this.fName           = obj.getString("FirstName");
            this.lName           = obj.getString("LastName");
            this.token           = obj.getString("Token");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(String assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getAssigmentUserId() {
        return assigmentUserId;
    }

    public void setAssigmentUserId(String assigmentUserId) {        this.assigmentUserId = assigmentUserId;    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getToken() {        return token;    }

    public void setToken(String token) {        this.token = token;    }
}
