package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class ObjectDealer implements Parcelable {
    private Integer id;

    private String name, email, regDate, icon;

    public ObjectDealer() {
        this.id         = -1;
        this.name       = "";
        this.email      = "";
        this.regDate    = "";
        this.icon       = "";
    }

    public ObjectDealer(JSONObject obj) {
        try {
            this.id     = Integer.parseInt(MCrypt.decryptSingle(obj.getString("id")));
            this.name   = MCrypt.decryptSingle(obj.getString("Name"));
            this.email  = MCrypt.decryptSingle(obj.getString("Email"));
            try {this.regDate = HelperDate.get_date_display(MCrypt.decryptSingle(obj.getString("RegDate")));
            }catch (Exception e){}
            this.icon  =  MCrypt.decryptSingle(obj.getString("Icon"));
        }catch (Exception e){

        }
    }

    protected ObjectDealer(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        email = in.readString();
        regDate = in.readString();
        icon = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(regDate);
        dest.writeString(icon);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<ObjectDealer> CREATOR = new Creator<ObjectDealer>() {
        @Override
        public ObjectDealer createFromParcel(Parcel in) {
            return new ObjectDealer(in);
        }

        @Override
        public ObjectDealer[] newArray(int size) {
            return new ObjectDealer[size];
        }
    };

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("id",       this.getId().toString());
            jsonObject.put("name",     this.getName());
            jsonObject.put("email",    this.getEmail());
            jsonObject.put("regDate",  this.getRegDate());
            jsonObject.put("icon",     this.getIcon());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getIcon() {        return icon;    }
    public void setIcon(String icon) {        this.icon = icon;    }
    public Integer getId() {        return id;    }
    public void setId(Integer id) {        this.id = id;    }
    public String getName() {        return name;    }
    public void setName(String name) {        this.name = name;    }
    public String getEmail() {        return email;    }
    public void setEmail(String email) {        this.email = email;    }
    public String getRegDate() {        return regDate;    }
    public void setRegDate(String regDate) {        this.regDate = regDate;    }

}
