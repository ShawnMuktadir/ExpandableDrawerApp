package www.fiberathome.com.parkingapp.view.profile;

import android.annotation.SuppressLint;
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
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;

public class ProfileFragment extends Fragment {

    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvUserMobileNo)
    TextView tvUserMobileNo;
    @BindView(R.id.tvUserVehicleNo)
    TextView tvUserVehicleNo;
    @BindView(R.id.ivUserProfilePic)
    ImageView ivUserProfilePic;

    private Unbinder unbinder;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
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

        User user = SharedPreManager.getInstance(getContext()).getUser();
        setData(user);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("onDestroyView called ");
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void setData(User user) {
        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }

        String name = user.getFullName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        tvUserName.setText(name);

        tvUserMobileNo.setText(ApplicationUtils.addCountryPrefix(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        tvUserVehicleNo.setText(user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Timber.e("Image URL -> %s", url);
        Glide.with(getActivity()).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(ivUserProfilePic);
    }
}