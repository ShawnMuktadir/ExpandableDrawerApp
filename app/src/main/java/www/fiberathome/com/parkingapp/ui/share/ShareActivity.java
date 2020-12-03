package www.fiberathome.com.parkingapp.ui.share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class ShareActivity extends NavigationActivity implements FragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));

        // Initialize Parking fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ShareFragment.newInstance(), R.id.nav_host_fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_share);
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
