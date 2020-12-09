package www.fiberathome.com.parkingapp.ui.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private ProgressDialog progressDialog;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetDirectionEvent event) {
        Timber.e("parkingFragment GetDirectionEvent onMessageEvent called");
        navigationView.getMenu().getItem(0).setChecked(true);
        //toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        tvTimeToolbar.setVisibility(View.VISIBLE);
        linearLayoutToolbarTime.setVisibility(View.VISIBLE);
        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");
        startActivity(HomeActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finishAffinity();
        //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, HomeFragment.newInstance()).commit();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Timber.e("GetDirectionEvent called");
            // Do something after 2s = 2000ms
            try {
                EventBus.getDefault().post(new SetMarkerEvent(event.location));
                progressDialog.dismiss();
            } catch (EventBusException e) {
                e.getCause();
            }
        }, 3000);
    }
}
