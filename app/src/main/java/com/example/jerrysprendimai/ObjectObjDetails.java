package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectObjDetails implements Parcelable {
    private Integer id, objectId, posNr;
    private String description, completed;

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
        description = in.readString();
        completed = in.readString();
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
        dest.writeString(description);
        dest.writeString(completed);
    }

    public ObjectObjDetails(){
        this.id           = -1;
        this.objectId     = -1;
        this.posNr        = 0;
        this.description  = "";
        this.completed    = "";
    }
    public ObjectObjDetails(JSONObject obj) {
        try {
           this.id           = Integer.parseInt(obj.getString("id"));
           this.objectId     = Integer.parseInt(obj.getString("Object_id"));
           this.posNr        = Integer.parseInt(obj.getString("Pos_Nr"));
           this.description  = obj.getString("Description");
           this.completed    = obj.getString("Completed");
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

}
