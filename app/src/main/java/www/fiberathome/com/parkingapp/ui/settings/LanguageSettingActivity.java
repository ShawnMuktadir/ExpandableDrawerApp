package www.fiberathome.com.parkingapp.ui.settings;

import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;


public class LanguageSettingActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
        setTitle(getString(R.string.language_settings));

        //Initialize language Settings fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                LanguageSettingFragment.newInstance(), R.id.nav_host_fragment);
    }
}