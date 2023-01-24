package com.example.jerrysprendimai;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MCrypt2 {

    static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};


    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    private String iv = "oiuyxc98765_yrej";//Dummy iv (CHANGE IT!)
    private String SecretKey = "zottffssent_yrej";//Dummy secretKey (CHANGE IT!)

    private static String ivGlobal = "oiuyxc98765_yrej";//Dummy iv (CHANGE IT!)
    private static String secretKeyGlobal = "zottffssent_yrej";//Dummy secretKey (CHANGE IT!)

    public MCrypt2()
    {
        ivspec = new IvParameterSpec(iv.getBytes(), 0, 16);

        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] data) {
        byte[] key = secretKeyGlobal.getBytes();
        byte[] ivs = ivGlobal.getBytes();

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static byte[] decrypt(byte[] data) {
        byte[] key = secretKeyGlobal.getBytes();
        byte[] ivs = ivGlobal.getBytes();

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decryptSingle(String encrypted){

        byte [] byteEncrypted   = Base64.decode(encrypted,0);
        String  stringEncrypted = Base64.encodeToString(MCrypt2.decrypt(byteEncrypted),  Base64.DEFAULT);
        String value            = new String(Base64.decode(stringEncrypted, 0));
        return value;
    }
    public static String decryptDouble(String encrypted){

        byte [] byteEncrypted = Base64.decode(encrypted,0);
        //      byteEncrypted = MCrypt.decrypt(byteEncrypted);
        String  stringEncrypted = Base64.encodeToString(MCrypt2.decrypt(byteEncrypted),  Base64.DEFAULT);
        String value            = new String(Base64.decode(stringEncrypted, 0));
               value            = new String(Base64.decode(value, 0));

        return value;
    }

    public static String encodeToString(String str){
        return Base64.encodeToString(MCrypt2.encrypt(str.getBytes()), Base64.DEFAULT);
    }

    public static JSONObject decryptJSONObject(JSONObject encryptedJSONObject){
        JSONObject decryptedJSONObject = new JSONObject();
        Iterator<String> iterator;
        try {
            decryptedJSONObject = new JSONObject();
            iterator = encryptedJSONObject.keys();
            while (iterator.hasNext()) {
               String key = iterator.next();
               if(key.equals("User")){
                 decryptedJSONObject.put(key, MCrypt2.decryptDouble(encryptedJSONObject.optString(key)));
               }else{
                 decryptedJSONObject.put(key, MCrypt2.decryptSingle(encryptedJSONObject.optString(key)));
                    }
               }
        }catch (Exception e){
            e.printStackTrace();
        }
        return decryptedJSONObject;
    }

    public static JSONArray decryptJSONArray(JSONArray encryptedJSONArray){
        JSONArray  decryptedJSONArray = new JSONArray();
        JSONObject encryptedJSONObject;
        JSONObject decryptedJSONObject;
        Iterator<String> iterator;
        try {
            for (int i = 0; i < encryptedJSONArray.length(); i++) {
                decryptedJSONObject = new JSONObject();
                encryptedJSONObject = (JSONObject) encryptedJSONArray.get(i);
                iterator = encryptedJSONObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if(key.equals("User")){
                      decryptedJSONObject.put(key, MCrypt2.decryptDouble(encryptedJSONObject.optString(key)));
                      //decryptedJSONObject.put(key, MCrypt.decryptSingle(encryptedJSONObject.optString(key)));
                    }else{
                      decryptedJSONObject.put(key, MCrypt2.decryptSingle(encryptedJSONObject.optString(key)));
                    }
                }
                decryptedJSONArray.put(decryptedJSONObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return decryptedJSONArray;
    }

    public static String bytesToHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }


    public static byte[] hexToBytes(String str) {
        if (str==null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }

    private static String padString(String source)
    {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++)
        {
            source += paddingChar;
        }

        return source;
    }
}
