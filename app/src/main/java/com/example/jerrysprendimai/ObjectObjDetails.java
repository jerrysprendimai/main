package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectObjDetails implements Parcelable {
    private Integer id, objectId, posNr, lockedByUserId;
    private String name, description, completed;

    protected ObjectObjDetails(Parcel in) {
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
            posNr = null;
        } else {
            posNr = in.readInt();
        }
        if (in.readByte() == 0) {
            lockedByUserId = null;
        } else {
            lockedByUserId = in.readInt();
        }
        name = in.readString();
        description = in.readString();
        completed = in.readString();
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
        if (posNr == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(posNr);
        }
        if (lockedByUserId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(lockedByUserId);
        }
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(completed);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<ObjectObjDetails> CREATOR = new Creator<ObjectObjDetails>() {
        @Override
        public ObjectObjDetails createFromParcel(Parcel in) {
            return new ObjectObjDetails(in);
        }

        @Override
        public ObjectObjDetails[] newArray(int size) {
            return new ObjectObjDetails[size];
        }
    };

    public ObjectObjDetails(){
        this.id             = -1;
        this.objectId       = -1;
        this.posNr          = 0;
        this.name           = "";
        this.description    = "";
        this.lockedByUserId = -1;
        this.completed      = "";
    }
    public ObjectObjDetails(JSONObject obj) {
        try {
           this.id              = Integer.parseInt(obj.getString("id"));
           this.objectId        = Integer.parseInt(obj.getString("Object_id"));
           this.posNr           = Integer.parseInt(obj.getString("Pos_Nr"));
           this.name            = obj.getString("Name");
           this.description     = obj.getString("Description");
           this.lockedByUserId = Integer.parseInt(obj.getString("LockedByUserID"));;
           this.completed       = obj.getString("Completed");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",             this.getId().toString());
            jsonObject.put("objectId",       this.getObjectId());
            jsonObject.put("posNr",          this.getPosNr());
            jsonObject.put("name",           this.getName());
            jsonObject.put("description",    this.getDescription());
            jsonObject.put("lockedByUserId", this.getLockedByUserId());
            if (this.completed.equals(true)){
                jsonObject.put("completed","X");
            }else{
                jsonObject.put("completed","");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getPosNr() {
        return posNr;
    }

    public void setPosNr(Integer posNr) {
        this.posNr = posNr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLockedByUserId() {
        return lockedByUserId;
    }

    public void setLockedByUserId(Integer lockedByUserId) {
        this.lockedByUserId = lockedByUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
