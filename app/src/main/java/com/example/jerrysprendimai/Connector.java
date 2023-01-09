package com.example.jerrysprendimai;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Connector {
    private static final String domain              = "https://www.jerry-sprendimai.eu";

    private String connDBUrl;
    private String connDBName;
    private String connDBUser;
    private String connDBPassword;
    private String connDBServer;

    public HttpURLConnection httpURLConnection;
    public OutputStream outputStream;
    public BufferedWriter bufferedWriter;

    JSONArray resultJson;

    String postData;
    int statusCode;
    JSONArray resultJsonArray;
    String result;

    public Connector(Context context, String link) {
        try {
            resultJson = new JSONArray();
            result = "";

            //----------reading values from Internal DB
            SQLiteDB dbHelper = new SQLiteDB(context);
            Cursor result = dbHelper.getData();
            if (result.getCount() == 0) {
                //Toast.makeText(context, "No SETTINGS data found", Toast.LENGTH_SHORT).show();
                return;
            }

            result.moveToNext();
            connDBUrl = result.getString(1);
            connDBServer = result.getString(2);
            connDBName = result.getString(3);
            connDBUser = result.getString(4);
            connDBPassword = result.getString(5);

            if( connDBUrl.isEmpty()){
                connDBUrl = domain;
            }

            URL url = new URL(connDBUrl + '/' + link);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setReadTimeout(15000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);

            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            postData = URLEncoder.encode("db_name", "UTF-8") + "=" + URLEncoder.encode(getConnDBName(), "UTF-8") + "&"
                    + URLEncoder.encode("db_user", "UTF-8") + "=" + URLEncoder.encode(getConnDBUser(), "UTF-8") + "&"
                    + URLEncoder.encode("db_password", "UTF-8") + "=" + URLEncoder.encode(getConnDBPassword(), "UTF-8") + "&"
                    + URLEncoder.encode("db_server", "UTF-8") + "=" + URLEncoder.encode(getConnDBServer(), "UTF-8");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        try {
            bufferedWriter.write(getPostData());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            setStatusCode(httpURLConnection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        String res = "";
        JSONArray jsonArray = new JSONArray();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                res += line;
            }
            try {
                jsonArray = new JSONArray(res);
            } catch (Exception e) {
                //JSONObject jsonObject = new JSONObject(res);
                jsonArray = new JSONArray().put(new JSONObject(res));
            }

            bufferedReader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setResult(res);
        setResultJsonArray(jsonArray);
    }

    public void disconnect() {
        try {
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray getResultJsonArray() {
        return resultJsonArray;
    }

    public void setResultJsonArray(JSONArray resultJson) {
        this.resultJsonArray = resultJson;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void addPostParameter(String parameterName, String parameterValue) {
        String postData = getPostData();
        try {
            postData += "&" + URLEncoder.encode(parameterName, "UTF-8") + "=" + URLEncoder.encode(parameterValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setPostData(postData);
    }

    public String getConnDBName() {
        return Base64.encodeToString(MCrypt.encrypt(connDBName.getBytes()), Base64.DEFAULT);
        //return connDBName;
    }

    public String getConnDBUser() {
        return Base64.encodeToString(MCrypt.encrypt(connDBUser.getBytes()), Base64.DEFAULT);
        //return connDBUser;
    }

    public String getConnDBPassword() {
        return Base64.encodeToString(MCrypt.encrypt(connDBPassword.getBytes()), Base64.DEFAULT);
        //return connDBPassword;
    }

    public String getConnDBServer() {
        return Base64.encodeToString(MCrypt.encrypt(connDBServer.getBytes()), Base64.DEFAULT);
        //return connDBServer;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
