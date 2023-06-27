package com.example.jerrysprendimai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.FileUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ObjectObjPic implements Parcelable{
    private Integer id, objectId, posNr, userId;
    String picName, creationDate, picUrl, firstName;
    String picUri;
    String mimeType;
    String filePath;
    Bitmap imageResource;
    MyAdapterObjectEditPicture.MyViewHolder holder;
    MyAdapterOrderPicture.MyViewHolder orderPicHolder;

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
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        picName = in.readString();
        creationDate = in.readString();
        picUrl = in.readString();
        firstName = in.readString();
        picUri = in.readString();
        mimeType = in.readString();
        filePath = in.readString();
        imageResource = in.readParcelable(Bitmap.class.getClassLoader());
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
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(picName);
        dest.writeString(creationDate);
        dest.writeString(picUrl);
        dest.writeString(firstName);
        dest.writeString(picUri);
        dest.writeString(mimeType);
        dest.writeString(filePath);
        dest.writeParcelable(imageResource, flags);
    }
    @Override
    public int describeContents() {
        return 0;
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

    public ObjectObjPic(){
        this.id           = -1;
        this.objectId     = -1;
        this.posNr        = -1;
        this.picName      = "";
        this.creationDate = HelperDate.get_current_date_disply();
        this.userId       = -1;
        this.picUrl       = "";
        this.picUri       = "";
        this.firstName    = "";
        this.mimeType     = "";
        this.filePath     = "";
    }

    public ObjectObjPic(JSONObject obj) {
        try {
            this.id           = Integer.parseInt(obj.getString("id"));
            this.objectId     = Integer.parseInt(obj.getString("Object_id"));
            this.posNr        = Integer.parseInt(obj.getString("Pos_Nr"));
            this.picName      = obj.getString("PicName");
            this.creationDate = obj.getString("CreationDate");
            this.picUrl       = obj.getString("PicURL");
            this.userId       = Integer.parseInt(obj.getString("User_id"));
            this.picUri       = "";
            this.firstName    = obj.getString("FirstName");
            this.mimeType     = obj.getString("MimeType");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",           this.getId().toString());
            jsonObject.put("objectId",     this.getObjectId());
            jsonObject.put("posNr",        this.getPosNr());
            jsonObject.put("picName",      this.getPicName());
            jsonObject.put("creationDate", this.getCreationDate());
            jsonObject.put("userId",       this.getUserId());
            jsonObject.put("picUrl",       this.getPicUrl());
            jsonObject.put("picUri",       this.getPicUri());
            jsonObject.put("mimeType",     this.getMimeType());
            //jsonObject.put("firstName",    this.getPicUri());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    public String ImgSource(Context context){
        String str_img = "";
        if(this.getMimeType().contains("image/")){
            try {
                if(this.getId().equals(-1)){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Uri imageUri = Uri.parse(this.getPicUri());
                    Bitmap bitmap = null;

                    bitmap = ((BitmapDrawable)holder.myImage.getDrawable()).getBitmap();
                    //bitmap = holder.myImage.getDrawingCache();

                    //bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }else{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = null;
                    bitmap = ((BitmapDrawable)holder.myImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            } catch (Exception e) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Uri imageUri = Uri.parse(this.getPicUri());
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
                e.printStackTrace();
            }
        }else{
            try {
                File file = new File(this.getFilePath());
                if(file.isFile()){
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte byteArray[] = new byte[(int) file.length()];
                    int bytesRead = fileInputStream.read(byteArray, 0, (int) file.length());
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str_img;
    }

    public String orderPicImgSource(Context context){
        String str_img = "";
        if(this.getMimeType().contains("image/")){
            try {
                if(this.getId().equals(-1)){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Uri imageUri = Uri.parse(this.getPicUri());
                    Bitmap bitmap = null;

                    bitmap = ((BitmapDrawable)orderPicHolder.myImage.getDrawable()).getBitmap();
                    //bitmap = holder.myImage.getDrawingCache();

                    //bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }else{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = null;
                    bitmap = ((BitmapDrawable)orderPicHolder.myImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            } catch (Exception e) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Uri imageUri = Uri.parse(this.getPicUri());
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray= baos.toByteArray();
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
                e.printStackTrace();
            }
        }else{
            try {
                File file = new File(this.getFilePath());
                //byte byteArray[] = new byte[(int) file.length()];
                //str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                if(file.isFile()){
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte byteArray[] = new byte[(int) file.length()];
                    int bytesRead = fileInputStream.read(byteArray, 0, (int) file.length());
                    str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str_img;
    }

    public Integer getId() {         return id;    }
    public void setId(Integer id) {        this.id = id;    }
    public Integer getObjectId() {        return objectId;    }
    public void setObjectId(Integer objectId) {        this.objectId = objectId;    }
    public Integer getPosNr() {        return posNr;    }
    public void setPosNr(Integer posNr) {        this.posNr = posNr;    }
    public String getPicName() {        return picName;    }
    public void setPicName(String picName) {        this.picName = picName;    }
    public String getCreationDate() {        return creationDate;    }
    public void setCreationDate(String creationDate) {        this.creationDate = creationDate;    }
    public String getPicUrl() {        return picUrl;    }
    public void setPicUrl(String picUrl) {        this.picUrl = picUrl;    }
    public String getPicUri() {        return picUri;    }
    public void setPicUri(String picUri) {        this.picUri = picUri;    }
    public Bitmap getImageResource() {        return imageResource;    }
    public void setImageResource(Bitmap imageResource) {        this.imageResource = imageResource;    }
    public MyAdapterObjectEditPicture.MyViewHolder getHolder() {        return holder;    }
    public void setHolder(MyAdapterObjectEditPicture.MyViewHolder holder) {        this.holder = holder;    }
    public Integer getUserId() {        return userId;    }
    public void setUserId(Integer userId) {        this.userId = userId;    }
    public String getFirstName() {        return firstName;    }
    public void setFirstName(String firstName) {        this.firstName = firstName;    }
    public MyAdapterOrderPicture.MyViewHolder getOrderPicHolder() {        return orderPicHolder;    }
    public void setOrderPicHolder(MyAdapterOrderPicture.MyViewHolder orderPicHolder) {        this.orderPicHolder = orderPicHolder;    }
    public String getMimeType() {        return mimeType;    }
    public void setMimeType(String mimeType) {        this.mimeType = mimeType;    }
    public String getFilePath() {        return filePath;    }
    public void setFilePath(String filePath) {        this.filePath = filePath;    }

}
