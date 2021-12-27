package www.fiberathome.com.parkingapp.data.model.data.preference;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import www.fiberathome.com.parkingapp.data.model.data.Constants;

@SuppressLint("StaticFieldLeak")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class LanguagePreferences {

    private static final String SHARED_PREF_NAME = "PARKINGAPP_LANGUAGE";
    private static final String KEY_ID = "id";
    private static final String KEY_LANGUAGE = "language";

    private static LanguagePreferences instance;

    private static Context mContext;

    public LanguagePreferences(Context context) {
        mContext = context;
    }

    public static synchronized LanguagePreferences getInstance(Context context) {
        if (instance == null) {
            instance = new LanguagePreferences(context);
        }

        return instance;
    }

    // String IO
    @SuppressWarnings("SameParameterValue")
    private void saveValue(String key, String value) {
        if (key == null || key.isEmpty()) return;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @SuppressWarnings("SameParameterValue")
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
