package www.fiberathome.com.parkingapp.model.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.utils.Constants;

import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

public class Preferences {

    private static final String SHARED_PREF_NAME = "PARKINGAPP";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_MOBILE_NO = "mobile_no";
    private static final String KEY_VEHICLE_NO = "vehicle_no";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_ID = "id";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_IS_LOCATION_PERMISSION = "KEY_IS_LOCATION_PERMISSION";

    // SMS Tags
    private static final String KEY_IS_WAITING_FOR_SMS = "isWaitingForSMS22";
    private static final String KEY_IS_UPDATE_REQUIRED = "isUpdateRequired";
    private static final String KEY_CHECKED_ITEM = "checked_item";


    private static Preferences instance;

    private static Context mContext;

    public Preferences(Context context) {
        mContext = context;
    }

    public static synchronized Preferences getInstance(Context context){
        if (instance == null){
            instance = new Preferences(context);
        }

        return instance;
    }

    public void userLogin(User user){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_FULLNAME, user.getFullName());
        editor.putString(KEY_MOBILE_NO, user.getMobileNo());
        editor.putString(KEY_VEHICLE_NO, user.getVehicleNo());
        editor.putString(KEY_PROFILE_PIC, user.getImage());
        editor.apply();
    }

    // String IO
    private void saveValue(String key, String value) {
        if (key == null || key.isEmpty()) return;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getValue(String key, String defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public String getAppLanguage() {
        return getValue(Constants.LANGUAGE, LANGUAGE_EN);
    }

    public void setAppLanguage(String language) {
        saveValue(Constants.LANGUAGE, language);
    }

    public void setIsLocationPermissionGiven(boolean isLocationPermissionGiven){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOCATION_PERMISSION, isLocationPermissionGiven);
        editor.apply();
    }

    public boolean isWaitingForLocationPermission(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOCATION_PERMISSION, false);
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_MOBILE_NO, null) != null){
            return true;
        }else {
            return false;
        }
    }

    public User getUser(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setFullName(sharedPreferences.getString(KEY_FULLNAME, null));
        user.setMobileNo(sharedPreferences.getString(KEY_MOBILE_NO, null));
        user.setVehicleNo(sharedPreferences.getString(KEY_VEHICLE_NO, null));
        user.setImage(sharedPreferences.getString(KEY_PROFILE_PIC, null));

        return user;
    }

    public void logout(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setIsWaitingForSMS(boolean isWaiting){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.apply();
    }

    public boolean isWaitingForSMS(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setIsUpdateRequired(boolean isUpdateRequired){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_UPDATE_REQUIRED, isUpdateRequired);
        editor.apply();
    }

    public boolean isUpdateRequired(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_UPDATE_REQUIRED, false);
    }

    private int checkedItem;

    public int getCheckedItem() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CHECKED_ITEM, 0);
    }

    public void setCheckedItem(int checkedItem) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CHECKED_ITEM, checkedItem);
        editor.apply();
    }
}
