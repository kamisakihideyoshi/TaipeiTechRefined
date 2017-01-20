package owo.npc.taipeitechrefined;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import owo.npc.taipeitechrefined.model.Model;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Locale;

public class MainApplication extends Application {
    private static MainApplication singleton;
    public static String SETTING_NAME = "TaipeiTech";
    public static String lang;
    public static MainApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        lang = Locale.getDefault().getLanguage();
        super.onCreate();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        singleton = this;
        Model.getInstance();
        Intent check_intent = new Intent(this, ActivityCheckReceiver.class);
        check_intent.putExtra("action", "com.taipeitech.action.ACTIVITY_CHECK");
        sendBroadcast(check_intent);
    }

    public static String readSetting(String settingName) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        return settings.getString(settingName, "");
    }

    public static void writeSetting(String settingName, String value) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        settings.edit().putString(settingName, value).apply();
    }

    public static void clearSettings(String settingName) {
        SharedPreferences settings = singleton.getSharedPreferences(
                SETTING_NAME, MODE_PRIVATE);
        settings.edit().remove(settingName).apply();
    }
}
