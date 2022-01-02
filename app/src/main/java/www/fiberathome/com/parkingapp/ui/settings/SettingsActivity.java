package www.fiberathome.com.parkingapp.ui.settings;

import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class SettingsActivity extends NavigationActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setTitle(context.getResources().getString(R.string.action_settings));

        //Initialize Settings fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SettingsFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            setAppLocale(LANGUAGE_EN);
        } else {
            setAppLocale(Preferences.getInstance(context).getAppLanguage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(HomeActivity.class);
    }
}
