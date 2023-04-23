package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ObjectOrder implements Parcelable {
    private Integer id;

    ObjectObject myObject;
    ObjectDealer myDealer;
    ArrayList<ObjectObjPic> myPictureList;
    String myText;

    protected ObjectOrder(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        myObject = in.readParcelable(ObjectObject.class.getClassLoader());
        myDealer = in.readParcelable(ObjectDealer.class.getClassLoader());
        myPictureList = in.createTypedArrayList(ObjectObjPic.CREATOR);
        myText = in.readString();
    }
    public static final Creator<ObjectOrder> CREATOR = new Creator<ObjectOrder>() {
        @Override
        public ObjectOrder createFromParcel(Parcel in) {
            return new ObjectOrder(in);
        }

        @Override
        public ObjectOrder[] newArray(int size) {
            return new ObjectOrder[size];
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
        dest.writeParcelable(myObject, flags);
        dest.writeParcelable(myDealer, flags);
        dest.writeTypedList(myPictureList);
        dest.writeString(myText);
    }

    public ObjectOrder() {
        this.id = -1;
        this.myObject = null;
        this.myDealer = null;
        this.myText = "";
        this.myPictureList = new ArrayList<>();
    }

    public ObjectOrder(Integer id, ObjectObject myObject, ObjectDealer myDealer, ArrayList<ObjectObjPic> myPictureList) {
        this.id = id;
        this.myObject = myObject;
        this.myDealer = myDealer;
        this.myText = "";
        this.myPictureList = myPictureList;
    }

    public Integer getId() {        return id;    }
    public void setId(Integer id) {        this.id = id;    }
    public ObjectObject getMyObject() {        return myObject;    }
    public void setMyObject(ObjectObject myObject) {        this.myObject = myObject;    }
    public ObjectDealer getMyDealer() {        return myDealer;    }
    public void setMyDealer(ObjectDealer myDealer) {        this.myDealer = myDealer;    }
    public String getMyText() {        return myText;    }
    public void setMyText(String myText) {        this.myText = myText;    }
    public ArrayList<ObjectObjPic> getMyPictureList() {        return myPictureList;    }
    public void setMyPictureList(ArrayList<ObjectObjPic> myPictureList) {        this.myPictureList = myPictureList;    }


}
