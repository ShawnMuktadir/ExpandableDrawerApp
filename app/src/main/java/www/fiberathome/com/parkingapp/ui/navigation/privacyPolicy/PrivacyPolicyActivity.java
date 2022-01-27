package www.fiberathome.com.parkingapp.ui.navigation.privacyPolicy;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.navigation.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class PrivacyPolicyActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;

        setTitle(context.getResources().getString(R.string.privacy_policy));

        // Initialize PrivacyPolicy Fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                PrivacyPolicyFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_privacy_policy);
    }
}
