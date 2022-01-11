package www.fiberathome.com.parkingapp.ui.navigation.profile;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.navigation.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class ProfileActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.profile));

        //Initialize Profile fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ProfileFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_profile);
    }
}
