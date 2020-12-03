package www.fiberathome.com.parkingapp.ui.followUs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NativeActivity;
import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class FollowUsActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        setTitle(context.getResources().getString(R.string.follow_us));

        // Initialize FollowUsFragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                FollowUsFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavDrawerItem(R.id.nav_follow_us);
    }
}
