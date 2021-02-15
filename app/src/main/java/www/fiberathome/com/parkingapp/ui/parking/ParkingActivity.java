package www.fiberathome.com.parkingapp.ui.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.module.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
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
