package www.fiberathome.com.parkingapp.ui.search;

import android.content.Context;
import android.os.Bundle;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class SearchActivity extends NavigationActivity {

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setTitle(context.getResources().getString(R.string.parking_spot));

        // Initialize Search fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SearchFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }
}
