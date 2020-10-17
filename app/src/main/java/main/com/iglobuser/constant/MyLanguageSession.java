package main.com.iglobuser.constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by technorizen on 21/3/17.
 */

public class MyLanguageSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "language";

    public static final String KEY_LANG = "langs";

    public static MyLanguageSession get(Context context) {
        return new MyLanguageSession(context);
    }

    public MyLanguageSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void insertLanguage(String language){
        editor.putString(KEY_LANG, language);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }


    public String getLanguage() {
        return pref.getString(KEY_LANG, "en");
    }
    public void setLangRecreate(String langval) {
        Configuration config = _context.getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        _context.getResources().updateConfiguration(config, _context.getResources().getDisplayMetrics());


    }
}
