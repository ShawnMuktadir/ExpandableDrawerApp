package www.fiberathome.com.parkingapp.view.share;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.main.MainActivity;
import www.fiberathome.com.parkingapp.view.main.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {

    private Context context;

    public ShareFragment() {
        // Required empty public constructor
    }

    public static ShareFragment newInstance() {
        ShareFragment shareFragment = new ShareFragment();
        return shareFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        context = getActivity();
        if (getActivity() != null){
            ((MainActivity)getActivity()).navigationView.getMenu().getItem(11).setChecked(false);
            ((MainActivity)getActivity()).linearLayoutToolbarTime.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).tvTimeToolbar.setVisibility(View.VISIBLE);
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        }

        if (isGPSEnabled()) {
//            HomeFragment nextFrag = new HomeFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        } else {
            TastyToastUtils.showTastyWarningToast(context, "Please enable GPS!");
        }
        shareApp();
        return view;
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"LOCC Smart Parking App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "LOCC Smart Parking App Link\n\n"+"https://163.47.157.195/parkingapp/");
        startActivity(Intent.createChooser(shareIntent,"Share Via:"));
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

//            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                    .setTitle("GPS Permissions")
//                    .setMessage("GPS is required for this app to work. Please enable GPS.")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();

        }
        return false;
    }
}
