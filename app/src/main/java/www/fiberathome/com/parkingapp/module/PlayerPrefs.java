package www.fiberathome.com.parkingapp.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PlayerPrefs {
    static String TAG = PlayerPrefs.class.getName();
    static PlayerPrefs mInstance;
    static Context mContext;
    static String FileName = "PlayerPrefs";

    //Prevent create object from user
    private PlayerPrefs() {

    }

    public static void Initialize(Context context) {
        mContext = context;
        mInstance = new PlayerPrefs();
    }

    public static PlayerPrefs getInstance() {
        if (mInstance == null) {
            Log.e(TAG, "You have to invoke Initialize(context) first!");
            return null;
        }
        return mInstance;
    }

    public int GetInt(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        return sharedPref.getInt(key, 0);
    }

    public void SetInt(String key, int value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public float GetFloat(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        return sharedPref.getFloat(key, 0);
    }

    public void SetFloat(String key, int value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public String GetString(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        return sharedPref.getString(key, "");
    }

    public void SetString(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
