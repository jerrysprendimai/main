package com.example.jerrysprendimai;

import android.util.Base64;

import org.json.JSONObject;

public class ObjectUser {
    private Integer id;
    private String email, uname, type, locked, first_name, last_name, passwd, reg_date, last_login, user_lv, sessionId;

    private Boolean checked;

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
}
