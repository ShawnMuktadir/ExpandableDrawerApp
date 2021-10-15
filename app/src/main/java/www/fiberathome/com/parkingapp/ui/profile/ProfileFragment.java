package www.fiberathome.com.parkingapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.profile.edit.EditProfileActivity;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;

import static android.content.Context.LOCATION_SERVICE;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ProfileFragment extends Fragment implements IOnBackPressListener {

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.tvUserMobileNo)
    TextView tvUserMobileNo;

    @BindView(R.id.tvUserVehicleNo)
    TextView tvUserVehicleNo;

    @BindView(R.id.tvUserVehicleNoArmy)
    TextView tvUserVehicleNoArmy;

    @BindView(R.id.ivUserProfilePic)
    ImageView ivUserProfilePic;

    @BindView(R.id.btn_update_info)
    Button btnUpdateInfo;

    @BindView(R.id.ivVehicleProfilePlatePreview)
    ImageView ivVehicleProfilePlatePreview;

    private Unbinder unbinder;

    private ProfileActivity context;

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
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        unbinder = ButterKnife.bind(this, view);

        context = (ProfileActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = Preferences.getInstance(context).getUser();
        setData(user);
    }

    private void setListeners() {
        btnUpdateInfo.setOnClickListener(v -> startActivity(new Intent(context, EditProfileActivity.class)));
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

    private void setData(User user) {
        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }

        String name = user.getFullName();
        name = TextUtils.getInstance().capitalizeFirstLetter(name);
        tvUserName.setText(name);

        tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefixWithPlus(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        if (TextUtils.getInstance().isNumeric(Preferences.getInstance(context).getUser().getVehicleNo())) {
            tvUserVehicleNoArmy.setVisibility(View.VISIBLE);
            tvUserVehicleNo.setText(String.format("^%s", user.getVehicleNo().substring(0, 2)));
            tvUserVehicleNoArmy.setText(user.getVehicleNo().substring(2, 6));
        } else {
            tvUserVehicleNo.setText(user.getVehicleNo());
            tvUserVehicleNoArmy.setVisibility(View.GONE);
        }

        if (!user.getImage().endsWith(".jpg")) {
            String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
            Timber.e("Image URL -> %s", url);
            Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(ivUserProfilePic);

            String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage() + ".jpg";
            Timber.e("Vehicle Image URL -> %s", vehicleUrl);
            Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(ivVehicleProfilePlatePreview);
        } else {
            String url = AppConfig.IMAGES_URL + user.getImage();
            Timber.e("Image URL -> %s", url);
            Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(ivUserProfilePic);

            String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage();
            Timber.e("Vehicle Image URL -> %s", vehicleUrl);
            Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(ivVehicleProfilePlatePreview);
        }
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("else called");
        }
        return false;
    }
}
