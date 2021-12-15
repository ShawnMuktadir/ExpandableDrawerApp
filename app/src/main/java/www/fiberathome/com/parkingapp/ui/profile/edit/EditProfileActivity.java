package www.fiberathome.com.parkingapp.ui.profile.edit;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class EditProfileActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.edit_profile));

        //Initialize Edit Profile fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                EditProfileFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_profile);
    }
}
