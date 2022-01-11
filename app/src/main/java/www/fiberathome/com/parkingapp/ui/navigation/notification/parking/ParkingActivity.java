package www.fiberathome.com.parkingapp.ui.navigation.notification.parking;

import android.content.Context;
import android.os.Bundle;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.navigation.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class ParkingActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.parking_spot));

        // Initialize Parking fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ParkingFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_parking);
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
