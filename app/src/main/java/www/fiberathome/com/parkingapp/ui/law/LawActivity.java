package www.fiberathome.com.parkingapp.ui.law;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class LawActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.law));

        // Initialize Parking fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                LawFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_law);
    }
}