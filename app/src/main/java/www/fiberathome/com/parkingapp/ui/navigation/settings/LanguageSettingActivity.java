package www.fiberathome.com.parkingapp.ui.navigation.settings;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_EN;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.navigation.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;


public class LanguageSettingActivity extends NavigationActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        changeDefaultActionBarDrawerToogleIconWithBackButton();
        setTitle(getString(R.string.language_settings));

        //Initialize language Settings fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                LanguageSettingFragment.newInstance(), R.id.nav_host_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            setAppLocale(LANGUAGE_EN);
        } else {
            setAppLocale(LanguagePreferences.getInstance(context).getAppLanguage());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // refresh your views here
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            setAppLocale(LANGUAGE_EN);
        } else {
            setAppLocale(Preferences.getInstance(context).getAppLanguage());
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(SettingsActivity.class);
    }
}