package com.displayfort.dfortusb;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Husain on 15-03-2016.
 */
public class DFPrefrence {


    private static final String SHARED_PREFERENCE_NAME = "DFUSBSharedPreference";
    private static final String SHARED_PREFERENCE_NAME_TUTORIAL = "DFUSBSharedPreferenceTUORIAL";

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesTutorial;

    public void setClearPrefrence() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public DFPrefrence(Context context) {
//        context = RestaurantApplication.getInstance();
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.sharedPreferencesTutorial = context.getSharedPreferences(SHARED_PREFERENCE_NAME_TUTORIAL,
                Context.MODE_PRIVATE);
    }


    public void setIsLogin(boolean IsLogin) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean("IsLogin", IsLogin);
        prefsEditor.commit();
    }

    public boolean IsLogin() {
        return sharedPreferences.getBoolean("IsLogin", false);
    }

    public String getLoginSessionKey() {
        return "";
    }
}