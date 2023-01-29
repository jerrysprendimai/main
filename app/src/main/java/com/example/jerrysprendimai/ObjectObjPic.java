package com.example.jerrysprendimai;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectObjPic implements Parcelable{
    private Integer id, objectId, posNr;
    String picName, creationDate, picUrl, picUri;
    Bitmap imageResource;

    protected ObjectObjPic(Parcel in) {
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
        picName = in.readString();
        creationDate = in.readString();
        picUrl = in.readString();
        picUri = in.readString();
        imageResource = in.readParcelable(Bitmap.class.getClassLoader());
    }
    public static final Creator<ObjectObjPic> CREATOR = new Creator<ObjectObjPic>() {
        @Override
        public ObjectObjPic createFromParcel(Parcel in) {
            return new ObjectObjPic(in);
        }

        @Override
        public ObjectObjPic[] newArray(int size) {
            return new ObjectObjPic[size];
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
        dest.writeString(picName);
        dest.writeString(creationDate);
        dest.writeString(picUrl);
        dest.writeString(picUri);
        dest.writeParcelable(imageResource, flags);
    }

    public ObjectObjPic(){
        this.id           = -1;
        this.objectId     = -1;
        this.posNr        = 0;
        this.picName      = "";
        this.creationDate = DateHelper.get_current_date_disply();
        this.picUrl       = "";
        this.picUri       = "";
    }

    public ObjectObjPic(JSONObject obj) {
        try {
            this.id           = Integer.parseInt(obj.getString("id"));
            this.objectId     = Integer.parseInt(obj.getString("Object_id"));
            this.posNr        = Integer.parseInt(obj.getString("Pos_Nr"));
            this.picName      = obj.getString("PicName");
            this.creationDate = obj.getString("CreationDate");
            this.picUrl       = obj.getString("PicURL");
            this.picUri       = "";
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

    public Integer getPosNr() {
        return posNr;
    }

    public void setPosNr(Integer posNr) {
        this.posNr = posNr;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public Bitmap getImageResource() {
        return imageResource;
    }

    public void setImageResource(Bitmap imageResource) {
        this.imageResource = imageResource;
    }

}
