package main.com.iglobuser.constant;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by shyam on 11/04/2016.
 */
public class MySession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "MyPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "id";

    public static final String KEY_FIRSTNAME = "first_name";
    public static final String KEY_LASTNAME = "last_name";
    public static final String KEY_SENDVIAMAIL = "Sendofferviamail";
    public static final String KEY_USERREGISTERED = "user_registered";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_BIRTHDATE = "DOB";
    public static final String KEY_MOBILE = "Mobileno";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_EMAILVERFY = "email_verify";
    public static final String KEY_USERSTATUS = "user_status";
    public static final String KEY_ALLDATA = "alldata";
    public static final String IS_ONLINE = "isonline";
    public static final String APP_UPDATE = "appupdate";

    public MySession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setuserId(String uid) {
        editor.putString(KEY_ID, uid);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }
    public void setAppUpdate(String appupdate) {
        editor.putString(APP_UPDATE, appupdate);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }
    public String getAppUpdate() {
        return pref.getString(APP_UPDATE, "cancel");
    }

    public void setuserfirstName(String firstname) {
        editor.putString(KEY_FIRSTNAME, firstname);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }

    public void setlogindata(String alldata) {
        editor.putString(KEY_ALLDATA, alldata);
        editor.commit();

    }

    public String getKeyAlldata() {
        return pref.getString(KEY_ALLDATA, null);
    }

    private String language = "", userid, name_str, birthdate_str, location_str, occupation_str, gender_str, imagepath_str, maritalstatus_str, children_str, smoking_habits_str, drinking_habits_str, education_str, height_str, country_str;


    public void createLoginSession(String id, String firstname, String lastname, String sendviamail, String user_registered, String user_email, String DOB, String Mobileno, String email_verify, String user_status) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_FIRSTNAME, firstname);
        editor.putString(KEY_LASTNAME, lastname);
        editor.putString(KEY_SENDVIAMAIL, sendviamail);
        editor.putString(KEY_USERREGISTERED, user_registered);
        editor.putString(KEY_EMAIL, user_email);
        editor.putString(KEY_BIRTHDATE, DOB);
        editor.putString(KEY_MOBILE, Mobileno);
        editor.putString(KEY_EMAILVERFY, email_verify);
        editor.putString(KEY_USERSTATUS, user_status);


        editor.commit();
    }

    /* public void checkLogin() {
         if (!this.IsLoggedIn()) {
             Intent intent = new Intent(_context, LoginActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             _context.startActivity(intent);
         }
     }
 */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_ID, pref.getString(KEY_ID, ""));
        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, ""));
        user.put(KEY_LASTNAME, pref.getString(KEY_LASTNAME, ""));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, ""));
        user.put(KEY_SENDVIAMAIL, pref.getString(KEY_SENDVIAMAIL, ""));

        user.put(KEY_USERREGISTERED, pref.getString(KEY_USERREGISTERED, ""));
        user.put(KEY_BIRTHDATE, pref.getString(KEY_BIRTHDATE, ""));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, ""));
        user.put(KEY_EMAILVERFY, pref.getString(KEY_EMAILVERFY, ""));
        user.put(KEY_USERSTATUS, pref.getString(KEY_USERSTATUS, ""));


        return user;
    }

    public void signinusers(boolean val){
        editor.putBoolean(IS_LOGIN, val);
        editor.commit();

    }
    public void onlineuser(boolean val){
        editor.putBoolean(IS_ONLINE, val);
        editor.commit();

    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
    public boolean IsOnline()

    {
        return pref.getBoolean(IS_ONLINE, true);
    }

    public boolean IsLoggedIn()

    {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getFirstName() {
        return pref.getString(KEY_FIRSTNAME, null);
    }

    public String getLastName() {
        return pref.getString(KEY_LASTNAME, null);
    }


    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }


    public String getId() {
        return pref.getString(KEY_ID, null);
    }


    public void setVIP(boolean isChecked) {
        editor.putBoolean("VIP",isChecked);
        editor.commit();
    }
    public boolean isVIP(){
        return pref.getBoolean("VIP",false);
    }
}