package www.fiberathome.com.parkingapp.ui.profile;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.databinding.FragmentProfileBinding;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.profile.edit.EditProfileActivity;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ProfileFragment extends Fragment implements IOnBackPressListener {

    private ProfileActivity context;

    FragmentProfileBinding binding;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (ProfileActivity) getActivity();
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = Preferences.getInstance(context).getUser();
        setData(user);
    }

    private void setListeners() {
        binding.btnUpdateInfo.setOnClickListener(v -> startActivity(new Intent(context, EditProfileActivity.class)));
    }

    @Override
    public void onDestroyView() {
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
        binding.tvUserName.setText(name);

        binding.tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefixWithPlus(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        if (TextUtils.getInstance().isNumeric(Preferences.getInstance(context).getUser().getVehicleNo())) {
            binding.tvUserVehicleNoArmy.setVisibility(View.VISIBLE);
            binding.tvUserVehicleNo.setText(String.format("^%s", user.getVehicleNo().substring(0, 2)));
            binding.tvUserVehicleNoArmy.setText(user.getVehicleNo().substring(2, 6));
        } else {
            binding.tvUserVehicleNo.setText(user.getVehicleNo());
            binding.tvUserVehicleNoArmy.setVisibility(View.GONE);
        }

        if (!user.getImage().endsWith(".jpg")) {
            String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
            Timber.e("Image URL -> %s", url);
            Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(binding.ivUserProfilePic);

            String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage() + ".jpg";
            Timber.e("Vehicle Image URL -> %s", vehicleUrl);
            Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleProfilePlatePreview);
        } else {
            String url = AppConfig.IMAGES_URL + user.getImage();
            Timber.e("Image URL -> %s", url);
            Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(binding.ivUserProfilePic);

            String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage();
            Timber.e("Vehicle Image URL -> %s", vehicleUrl);
            Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleProfilePlatePreview);
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
