package www.fiberathome.com.parkingapp.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.profile.ProfileFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

import static www.fiberathome.com.parkingapp.utils.LocaleManager.LANGUAGE_BANGLA;
import static www.fiberathome.com.parkingapp.utils.LocaleManager.LANGUAGE_ENGLISH;

public class SettingsActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.action_settings));

        // Initialize Settings fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SettingsFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_settings);
    }
}
