package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

public class ObjectOrder implements Parcelable {
    private Integer id;

    ObjectObject myObject;
    ObjectDealer myDealer;
    ArrayList<ObjectObjPic> myPictureList;
    String myText, creationDate,firstName, from, to;
    boolean emailSent;
    Integer userId;

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
        creationDate = in.readString();
        firstName = in.readString();
        from = in.readString();
        to = in.readString();
        emailSent = in.readByte() != 0;
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
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
        dest.writeString(creationDate);
        dest.writeString(firstName);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeByte((byte) (emailSent ? 1 : 0));
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
    }

    public ObjectOrder() {
        this.id            = -1;
        this.userId        = -1;
        this.myObject      = null;
        this.myDealer      = null;
        this.myText        = "";
        this.creationDate  = HelperDate.get_current_date_disply();
        this.myPictureList = new ArrayList<>();
        this.firstName     = "";
        this.from          = "";
        this.to            = "";
        this.emailSent     = false;
    }

    public ObjectOrder(Integer id, ObjectObject myObject, ObjectDealer myDealer, ArrayList<ObjectObjPic> myPictureList) {
        this.id = id;
        this.myObject = myObject;
        this.myDealer = myDealer;
        this.myText = "";
        this.myPictureList = myPictureList;
    }

    public String toJson(){
        JSONObject jsonObject  = new JSONObject();
        JSONObject jsonPicture = new JSONObject();
        try {
            jsonObject.put("id",           this.getId().toString());
            //jsonObject.put("myObject",     this.getMyObject().toJson());
            //jsonObject.put("myDealer",     this.getMyDealer().toJson());
            jsonObject.put("myText",       this.getMyText());
            jsonObject.put("creationDate", this.getCreationDate());
            jsonObject.put("firstName",    this.getFirstName());
            jsonObject.put("userId",       this.getUserId());
            jsonObject.put("from",         this.getFrom());
            jsonObject.put("to",           this.getTo());
            String value = "";
            if (this.isEmailSent()){
                value = "X";
            }
            jsonObject.put("emailSent",    value);
            /*for(ObjectObjPic objectObjPic: this.myPictureList){
                jsonPicture.put("picture", objectObjPic.toJson());
            }
            jsonObject.put("myPictures", jsonPicture.toString());*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
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
    public String getCreationDate() {        return creationDate;    }
    public void setCreationDate(String creationDate) {        this.creationDate = creationDate;    }
    public String getFirstName() {        return firstName;    }
    public void setFirstName(String firstName) {        this.firstName = firstName;    }
    public Integer getUserId() {        return userId;    }
    public void setUserId(Integer userId) {        this.userId = userId;    }
    public String getFrom() {        return from;    }
    public void setFrom(String from) {        this.from = from;    }
    public String getTo() {        return to;    }
    public void setTo(String to) {        this.to = to;    }
    public boolean isEmailSent() {        return emailSent;    }
    public void setEmailSent(boolean emailSent) {        this.emailSent = emailSent;    }

}
