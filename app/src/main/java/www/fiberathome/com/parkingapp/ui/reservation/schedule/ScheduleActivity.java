package www.fiberathome.com.parkingapp.ui.reservation.schedule;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class ScheduleActivity extends NavigationActivity implements FragmentChangeListener {

    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setTitle(context.getResources().getString(R.string.your_booked));

        double lat = getIntent().getDoubleExtra("lat", 0.0);
        double lng = getIntent().getDoubleExtra("long", 0.0);
        String areaName = getIntent().getStringExtra("areaName");
        String count = getIntent().getStringExtra("parkingSlotCount");
        String placeId = getIntent().getStringExtra("areaPlacedId");
        boolean isInArea = getIntent().getBooleanExtra("isInArea", false);

        //Initialize Schedule Fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ScheduleFragment.newInstance(lat, lng, areaName, count, placeId, isInArea), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    onPostResume is called only when the activity's state is completely restored. In this we will
    set our boolean variable to true. Indicating that transaction is safe now
    */
    @Override
    public void onPostResume() {
        super.onPostResume();
        isTransactionSafe = true;

        /* Here after the activity is restored we check if there is any transaction pending from
        the last restoration */
        if (isTransactionPending) {
            fragmentChange(HomeFragment.newInstance());
        }
    }

    /*
    onPause is called just before the activity moves to background and also before onSaveInstanceState. In this
    we will mark the transaction as unsafe
    */
    @Override
    public void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        try {
            if (isTransactionSafe) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                );
                ft.replace(R.id.nav_host_fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
                isTransactionPending = false;
            } else {
                 /*
                 If any transaction is not done because the activity is in background. We set the
                 isTransactionPending variable to true so that we can pick this up when we come back to
                 foreground */
                isTransactionPending = true;
            }
        } catch (IllegalStateException e) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
            Timber.e(e.getCause());
        }
    }
}
