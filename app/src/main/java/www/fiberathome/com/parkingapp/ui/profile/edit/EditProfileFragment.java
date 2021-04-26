package www.fiberathome.com.parkingapp.ui.profile.edit;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;

import static android.content.Context.LOCATION_SERVICE;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings("unused")
public class EditProfileFragment extends Fragment implements IOnBackPressListener {

    @BindView(R.id.textInputLayoutFullName)
    TextInputLayout textInputLayoutFullName;

    @BindView(R.id.editTextFullName)
    EditText editTextFullName;

    @BindView(R.id.textInputLayoutCarNumber)
    TextInputLayout textInputLayoutCarNumber;

    @BindView(R.id.editTextCarNumber)
    EditText editTextCarNumber;

    @BindView(R.id.tvUserMobileNo)
    TextView tvUserMobileNo;

    @BindView(R.id.imageViewEditProfileImage)
    CircleImageView imageViewEditProfileImage;

    @BindView(R.id.imageViewCaptureImage)
    ImageView imageViewCaptureImage;

   /* @BindView(R.id.btn_update_info)
    Button btnUpdateInfo;*/

    private Unbinder unbinder;

    private EditProfileActivity context;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_new, container, false);

        unbinder = ButterKnife.bind(this, view);

        context = (EditProfileActivity) getActivity();

        User user = Preferences.getInstance(context).getUser();

        setData(user);

        setListeners();

        return view;
    }

    private void setListeners() {
        /*btnUpdateInfo.setOnClickListener(v-> {

        });*/
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
        editTextFullName.setText(name);

        tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefix(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        editTextCarNumber.setText(user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
        Timber.e("Image URL -> %s", url);
        Glide.with(context).load(url).placeholder(R.drawable.ic_account_settings).dontAnimate().into(imageViewEditProfileImage);
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
