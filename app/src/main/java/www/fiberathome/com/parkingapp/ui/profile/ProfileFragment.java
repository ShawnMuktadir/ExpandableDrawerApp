package www.fiberathome.com.parkingapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;

@SuppressLint("NonConstantResourceId")
public class ProfileFragment extends Fragment implements IOnBackPressListener {

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.tvUserMobileNo)
    TextView tvUserMobileNo;

    @BindView(R.id.tvUserVehicleNo)
    TextView tvUserVehicleNo;

    @BindView(R.id.ivUserProfilePic)
    ImageView ivUserProfilePic;

    private Unbinder unbinder;
    private Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        Timber.e("onCreate called");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        User user = Preferences.getInstance(context).getUser();
        setData(user);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

            /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();*/

        }
        return false;
    }

    private void setData(User user) {
        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }

        String name = user.getFullName();
        //name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        name = ApplicationUtils.capitalizeFirstLetter(name);
        tvUserName.setText(name);

        tvUserMobileNo.setText(ApplicationUtils.addCountryPrefix(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        tvUserVehicleNo.setText(user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
        Timber.e("Image URL -> %s", url);
        Glide.with(getActivity()).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(ivUserProfilePic);
    }
}
