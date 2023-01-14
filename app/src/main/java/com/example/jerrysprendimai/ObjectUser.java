package com.example.jerrysprendimai;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONObject;

public class ObjectUser implements Parcelable {
    private Integer id;
    private String email, uname, type, locked, first_name, last_name, passwd, reg_date, last_login, user_lv, sessionId;

    private Boolean checked;

    protected ObjectUser(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        email = in.readString();
        uname = in.readString();
        type = in.readString();
        locked = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        passwd = in.readString();
        reg_date = in.readString();
        last_login = in.readString();
        user_lv = in.readString();
        sessionId = in.readString();
        byte tmpChecked = in.readByte();
        checked = tmpChecked == 0 ? null : tmpChecked == 1;
    }
    public static final Creator<ObjectUser> CREATOR = new Creator<ObjectUser>() {
        @Override
        public ObjectUser createFromParcel(Parcel in) {
            return new ObjectUser(in);
        }

        @Override
        public ObjectUser[] newArray(int size) {
            return new ObjectUser[size];
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
        dest.writeString(email);
        dest.writeString(uname);
        dest.writeString(type);
        dest.writeString(locked);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(passwd);
        dest.writeString(reg_date);
        dest.writeString(last_login);
        dest.writeString(user_lv);
        dest.writeString(sessionId);
        dest.writeByte((byte) (checked == null ? 0 : checked ? 1 : 2));
    }

    public ObjectUser(JSONObject obj){
        try {
            byte [] uname = Base64.decode(obj.getString("uname"),0);
            byte [] passwd = Base64.decode(obj.getString("passwd"), 0);

            String decUname = Base64.encodeToString(MCrypt.decrypt(uname),  Base64.DEFAULT);
            String decPaswd = Base64.encodeToString(MCrypt.decrypt(passwd), Base64.DEFAULT);

            decUname = new String(Base64.decode(decUname, 0));
            decPaswd = new String(Base64.decode(decPaswd, 0));

            decUname = new String(Base64.decode(decUname, 0));
            decPaswd = new String(Base64.decode(decPaswd, 0));

            this.id         = Integer.parseInt(obj.getString("id"));
            this.uname      = decUname.toLowerCase();
            //this.uname      = obj.getString("uname").toLowerCase();
            this.email      = obj.getString("email");
            this.type       = obj.getString("type");
            this.locked     = obj.getString("locked");
            this.first_name = obj.getString("first_name");
            this.last_name  = obj.getString("last_name");
            this.passwd     = decPaswd;
            //this.passwd     = obj.getString("passwd");
            //this.sessionId  = obj.getString("sessionId");
            this.reg_date   = DateHelper.get_date_display(obj.getString("reg_date"));
            this.last_login = DateHelper.get_timestamp_display(obj.getString("last_login"));//obj.getString("last_login");
            this.user_lv    = obj.getString("user_lv");
            this.checked    = false;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getUser_lv() {
        return user_lv;
    }

    public void setUser_lv(String user_lv) {
        this.user_lv = user_lv;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
