package com.nitish.privacyindicator;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SP_NAME = BuildConfig.APPLICATION_ID;
    private static final int ACCESS_MODE = Context.MODE_PRIVATE;

    private static SharedPrefManager sharedPrefManager;
    private SharedPreferences sharedPreferences;
    private Context context;

    public static SharedPrefManager getInstance(Context context) {
        if (null == sharedPrefManager) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }


    public SharedPrefManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
    }

    public void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context context, String key, String def_value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getString(key, def_value);
    }

    public void setInteger(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public int getInteger(Context context, String key, int def_value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getInt(key, def_value);
    }

    public void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(Context context, String key, boolean def_value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getBoolean(key, def_value);
    }

    public void setCameraIndicatorEnabled(boolean value){
        setBoolean(context, "CAMERA_ENABLED", value);
    }

    public boolean isCameraIndicatorEnabled(){
        return getBoolean(context, "CAMERA_ENABLED", true);
    }

    public void setCameraIndicatorColor(String value){
        setString(context, "CAMERA_INDICATOR", value);
    }

    public String getCameraIndicatorColor(){
        return getString(context, "CAMERA_INDICATOR", "#FC6042");
    }

    public void setCameraIndicatorSize(int value) {
        setInteger(context, "CAMERA_INDICATOR_SIZE", value);
    }

    public int getCameraIndicatorSize(){
        return getInteger(context, "CAMERA_INDICATOR_SIZE", 55);
    }

    public void setCameraIndicatorOpacity(int value){
        setInteger(context, "CAMERA_INDICATOR_OPACITY", value);
    }

    public int getCameraIndicatorOpacity(){
        return getInteger(context, "CAMERA_INDICATOR_OPACITY", 255);
    }

    public void setMicIndicatorEnabled(boolean value){
        setBoolean(context, "MIC_ENABLED", value);
    }

    public boolean isMicIndicatorEnabled(){
        return getBoolean(context, "MIC_ENABLED", true);
    }

    public void setMicIndicatorColor(String value){
        setString(context, "MIC_INDICATOR", value);
    }

    public String getMicIndicatorColor(){
        return getString(context, "MIC_INDICATOR", "#FFA840");
    }

    public void setMicIndicatorSize(int value){
        setInteger(context, "MIC_INDICATOR_SIZE", value);
    }

    public int getMicIndicatorSize(){
        return getInteger(context, "MIC_INDICATOR_SIZE", 55);
    }

    public void setMicIndicatorOpacity(int value){
        setInteger(context, "MIC_INDICATOR_OPACITY", value);
    }

    public int getMicIndicatorOpacity(){
        return getInteger(context, "MIC_INDICATOR_OPACITY", 255);
    }

    public void setNotificationEnabled(boolean value){
        setBoolean(context, "NOTIF_ENABLED", value);
    }

    public boolean isNotificationEnabled(){
        return getBoolean(context, "NOTIF_ENABLED", false);
    }

    public void setVibrationEnabled(boolean value){
        setBoolean(context, "VIB_ENABLED", value);
    }

    public boolean isVibrationEnabled(){
        return getBoolean(context, "VIB_ENABLED", false);
    }

    //0-TopRight 1-BotRight 2-BotLeft 3-TopLeft
    public void setPosition(int value){
        setInteger(context, "POSITION", value);
    }

    public int getPosition(){
        return getInteger(context, "POSITION", 0);
    }

}
