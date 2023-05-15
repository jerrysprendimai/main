package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;

public class ObjectOrder implements Parcelable {
    private Integer id;

    ObjectObject myObject;
    ObjectDealer myDealer;
    ArrayList<ObjectObjPic> myPictureList;
    String myText, creationDate,firstName, from, to, type, sentDate, messageId, inReplyTo;
    boolean emailSent, notViewed, hasAttachments;
    Integer userId, objectID, dealerId;


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
        type = in.readString();
        sentDate = in.readString();
        messageId = in.readString();
        inReplyTo = in.readString();
        emailSent = in.readByte() != 0;
        notViewed = in.readByte() != 0;
        hasAttachments = in.readByte() != 0;
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            objectID = null;
        } else {
            objectID = in.readInt();
        }
        if (in.readByte() == 0) {
            dealerId = null;
        } else {
            dealerId = in.readInt();
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
        dest.writeString(type);
        dest.writeString(sentDate);
        dest.writeString(messageId);
        dest.writeString(inReplyTo);
        dest.writeByte((byte) (emailSent ? 1 : 0));
        dest.writeByte((byte) (notViewed ? 1 : 0));
        dest.writeByte((byte) (hasAttachments ? 1 : 0));
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (objectID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(objectID);
        }
        if (dealerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dealerId);
        }
    }


    public ObjectOrder() {
        this.id            = -1;
        this.userId        = -1;
        this.objectID      = -1;
        this.dealerId      = -1;
        this.myObject      = null;
        this.myDealer      = null;
        this.myText        = "";
        this.creationDate  = HelperDate.get_current_date_disply();
        this.myPictureList = new ArrayList<>();
        this.firstName     = "";
        this.from          = "";
        this.to            = "";
        this.type          = "";
        this.emailSent     = false;
        this.messageId     = "";
        this.inReplyTo     = "";
        this.notViewed     = false;
    }

    public ObjectOrder(JSONObject obj) {
        try {
            this.id           = Integer.parseInt(MCrypt.decryptSingle(obj.getString("id")));
            this.objectID     = Integer.parseInt(MCrypt.decryptSingle(obj.getString("Object_id")));
            this.dealerId     = Integer.parseInt(MCrypt.decryptSingle(obj.getString("Dealer_id")));
            this.userId       = Integer.parseInt(MCrypt.decryptSingle(obj.getString("User_id")));
            this.firstName    = MCrypt.decryptSingle(obj.getString("FirstName"));
            this.creationDate = HelperDate.get_date_display(MCrypt.decryptSingle(obj.getString("CreationDate")));
            this.myText       = MCrypt.decryptSingle(obj.getString("Text"));
            if(String.valueOf(MCrypt.decryptSingle(obj.getString("Attachments"))).equals("X")){
              this.hasAttachments = true;
            }else{
              this.hasAttachments = false;
            }
            this.type         = MCrypt.decryptSingle(obj.getString("Type"));
            this.from         = MCrypt.decryptSingle(obj.getString("From"));
            this.to           = MCrypt.decryptSingle(obj.getString("To"));
            if(String.valueOf(MCrypt.decryptSingle(obj.getString("Email_sent"))).equals("X")){
                this.emailSent = true;
            }else{
                this.emailSent = false;
            }
            String[] timeStamp = MCrypt.decryptSingle(obj.getString("Sent_date")).split(" ");
            this.sentDate      = HelperDate.get_date_display_short(timeStamp[0]);
            this.messageId     = MCrypt.decryptSingle(obj.getString("Message_id"));
            this.inReplyTo     = MCrypt.decryptSingle(obj.getString("In_reply_to"));
            if (String.valueOf(MCrypt.decryptSingle(obj.getString("Not_viewed"))).equals("X")){
                this.notViewed = true;
            }else{
                this.notViewed = false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
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
            jsonObject.put("type",         this.getType());
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
    public String getType() {        return type;    }
    public void setType(String type) {        this.type = type;    }
    public Integer getObjectID() {        return objectID;    }
    public void setObjectID(Integer objectID) {        this.objectID = objectID;    }
    public Integer getDealerId() {        return dealerId;    }
    public void setDealerId(Integer dealerId) {        this.dealerId = dealerId;    }
    public String getSentDate() {        return sentDate;    }
    public void setSentDate(String sentDate) {        this.sentDate = sentDate;    }
    public String getMessageId() {        return messageId;    }
    public void setMessageId(String messageId) {        this.messageId = messageId;    }
    public String getInReplyTo() {        return inReplyTo;    }
    public void setInReplyTo(String inReplyTo) {        this.inReplyTo = inReplyTo;    }
    public boolean isNotViewed() {        return notViewed;    }
    public void setNotViewed(boolean notViewed) {        this.notViewed = notViewed;    }
    public boolean isHasAttachments() {        return hasAttachments;    }
    public void setHasAttachments(boolean hasAttachments) {        this.hasAttachments = hasAttachments;    }

}
