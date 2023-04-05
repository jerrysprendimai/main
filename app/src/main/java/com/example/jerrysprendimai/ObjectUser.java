package com.example.jerrysprendimai;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import org.json.JSONObject;

public class ObjectUser implements Parcelable {
    private Integer id;
    private String email, uname, type, locked, first_name, last_name, passwd, reg_date, last_login, user_lv, sessionId, last_action, token;
    private MyAdapterUserShow.MyViewHolder myViewHolderUserShow;

    private Boolean checked, passwdDecoded;

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
        last_action = in.readString();
        token = in.readString();
        byte tmpChecked = in.readByte();
        checked = tmpChecked == 0 ? null : tmpChecked == 1;
        byte tmpPasswdDecoded = in.readByte();
        passwdDecoded = tmpPasswdDecoded == 0 ? null : tmpPasswdDecoded == 1;
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
        dest.writeString(last_action);
        dest.writeString(token);
        dest.writeByte((byte) (checked == null ? 0 : checked ? 1 : 2));
        dest.writeByte((byte) (passwdDecoded == null ? 0 : passwdDecoded ? 1 : 2));
    }
    @Override
    public int describeContents() {
        return 0;
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

    public ObjectUser(){
        this.id          = -1;
        this.uname       = "";
        this.email       = "";
        this.type        = "3";
        this.locked      = "";
        this.first_name  = "";
        this.last_name   = "";
        this.passwd      = "";  //--------------> decode here
        this.reg_date    = "";  //DateHelper.get_current_date_disply();
        this.last_login  = "";
        this.user_lv     = "";
        this.checked     = false;
        this.last_action = "";
        this.token       = "";
        this.passwdDecoded = false;
    }

    public ObjectUser(JSONObject obj){
        try {
            this.id          = Integer.parseInt(MCrypt.decryptSingle(obj.getString("id")));
            this.uname       = MCrypt.decryptDouble(obj.getString("uname"));
            this.email       = MCrypt.decryptSingle(obj.getString("email"));
            this.type        = MCrypt.decryptSingle(obj.getString("type"));
            this.locked      = MCrypt.decryptSingle(obj.getString("locked"));
            this.first_name  = MCrypt.decryptSingle(obj.getString("first_name"));
            this.last_name   = MCrypt.decryptSingle(obj.getString("last_name"));
            this.passwd      = MCrypt.decryptDouble(obj.getString("passwd"));
            try {this.reg_date    = HelperDate.get_date_display(MCrypt.decryptSingle(obj.getString("reg_date")));
            }catch (Exception e){}
            try {this.last_login  = HelperDate.get_timestamp_display(MCrypt.decryptSingle(obj.getString("last_login")));
            }catch (Exception e){}
            try { this.user_lv     = MCrypt.decryptSingle(obj.getString("user_lv"));
            }catch (Exception e){}
            try { this.sessionId   = MCrypt.decryptSingle(obj.getString("session"));
            }catch (Exception e){}
            try { this.last_action = MCrypt.decryptSingle(obj.getString("last_activity"));
            }catch (Exception e){}
            try { this.token       = MCrypt.decryptSingle(obj.getString("token"));
            }catch (Exception e){}
            try { this.checked     = false;
            }catch (Exception e){}
            this.passwdDecoded = false;
        }catch (Exception e){
            try{
                try {this.id          = Integer.parseInt(obj.getString("id"));
                }catch (Exception ee){}
                try { this.uname       = obj.getString("User");
                }catch (Exception ee){}
                try {this.email       = obj.getString("Email");
                }catch (Exception ee){}
                try { this.type        = obj.getString("Type");
                }catch (Exception ee){}
                try {this.first_name  = obj.getString("FirstName");
                }catch (Exception ee){}
                try {this.last_name   = obj.getString("LastName");
                }catch (Exception ee){}
                try { this.passwd      = obj.getString("Passwd");
                }catch (Exception ee){}
                try {this.locked      = obj.getString("Locked");
                }catch (Exception ee){}
                try {this.reg_date    = HelperDate.get_date_display(obj.getString("RegDate"));
                }catch (Exception ee){}
                try {this.last_login  = HelperDate.get_timestamp_display(obj.getString("LastLogin"));
                }catch (Exception ee){}
                try {this.user_lv     = obj.getString("user_lv");
                }catch (Exception ee){}
                try {this.sessionId   = obj.getString("Session");
                }catch (Exception ee){}
                try {this.last_action = obj.getString("LastActivity");
                }catch (Exception ee){}
                try { this.token       = obj.getString("Token");
                }catch (Exception ee){}
                this.checked    = false;
                this.passwdDecoded = false;
            }catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public int getUserLevelIndicatorColor(Context context) {
        Integer color = null;
        if (this.getType().equals("1")){
            color = ContextCompat.getColor(context, R.color.jerry_grey_light);
        }else if(this.getType().equals("2")){
            color = ContextCompat.getColor(context, R.color.jerry_blue);
        }else if(this.getType().equals("3")){
            //color = ContextCompat.getColor(context, R.color.jerry_grey);
            color = ContextCompat.getColor(context, R.color.teal_700);
        }
        return color;
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try{

            String endUname = "";
            String encPaswd = "";
            try{
                endUname = Base64.encodeToString(this.getUname().toLowerCase().getBytes(), Base64.DEFAULT);
                encPaswd = Base64.encodeToString(this.getPasswd().getBytes(), Base64.DEFAULT);

                endUname = Base64.encodeToString(MCrypt.encrypt(endUname.getBytes()), Base64.DEFAULT);
                encPaswd = Base64.encodeToString(MCrypt.encrypt(encPaswd.getBytes()), Base64.DEFAULT);
                //endUname = Base64.encodeToString(MCrypt.encrypt(this.getUname().toLowerCase().getBytes()), Base64.DEFAULT);
                //encPaswd = Base64.encodeToString(MCrypt.encrypt(this.getPasswd().getBytes()), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            jsonObject.put("id", this.getId().toString());
            jsonObject.put("uname", endUname);
            //jsonObject.put("uname", this.getUname().toLowerCase());
            jsonObject.put("email", this.getEmail());
            jsonObject.put("type",this.getType());
            jsonObject.put("locked",this.getLocked());
            jsonObject.put("fname",this.getFirst_name());
            jsonObject.put("lname",this.getLast_name());
            jsonObject.put("password",encPaswd);
            //jsonObject.put("password",this.getPasswd());
            jsonObject.put("regDate", HelperDate.get_date_mysql(this.getReg_date()));
            jsonObject.put("cheked", this.getChecked().toString());
        }catch(Exception e){
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
    public String getPasswd() {        return passwd;    }
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
    public String getLast_action() {        return last_action;    }
    public void setLast_action(String last_action) {        this.last_action = last_action;    }
    public String getToken() {        return token;    }
    public void setToken(String token) {        this.token = token;    }
    public MyAdapterUserShow.MyViewHolder getMyViewHolderUserShow() {        return myViewHolderUserShow;    }
    public void setMyViewHolderUserShow(MyAdapterUserShow.MyViewHolder myViewHolderUserShow) {        this.myViewHolderUserShow = myViewHolderUserShow;    }
    public Boolean isPasswdDecoded() {        return passwdDecoded;    }
    public void setPasswdDecoded(Boolean passwdDecoded) {        this.passwdDecoded = passwdDecoded;    }

}
