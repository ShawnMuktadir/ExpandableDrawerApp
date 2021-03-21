package www.fiberathome.com.parkingapp.model.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.utils.Constants;

import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

public class LanguagePreferences {

    private static final String SHARED_PREF_NAME = "PARKINGAPP";
    private static final String KEY_ID = "id";
    private static final String KEY_LANGUAGE = "language";

    private static LanguagePreferences instance;

    private static Context mContext;

    public LanguagePreferences(Context context) {
        mContext = context;
    }

    public static synchronized LanguagePreferences getInstance(Context context){
        if (instance == null){
            instance = new LanguagePreferences(context);
        }

        return instance;
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
}