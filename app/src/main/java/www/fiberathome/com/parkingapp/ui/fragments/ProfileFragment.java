package www.fiberathome.com.parkingapp.ui.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.utils.AppConfig;
import www.fiberathome.com.parkingapp.utils.SharedPreManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private TextView fullnameTV;
    private TextView mobileNoTV;
    private TextView vehicleNoTV;
    private ImageView userProfilePic;

    private Button editProfile;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // intialize
        fullnameTV = view.findViewById(R.id.user_fullName);
        mobileNoTV = view.findViewById(R.id.user_mobile_no);
        vehicleNoTV = view.findViewById(R.id.user_vehicle_no);
        userProfilePic = view.findViewById(R.id.profile_image);


        //editProfile = view.findViewById(R.id.fab);

        User user = SharedPreManager.getInstance(getContext()).getUser();

        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }
        String name  = user.getFullName();
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        fullnameTV.setText(name);
        mobileNoTV.setText("+88" + user.getMobileNo());
        vehicleNoTV.setText(user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Log.e("URL", url);
        Glide.with(getActivity()).load(url).placeholder(R.drawable.blank_profile_pic).into(userProfilePic);

        return view;
    }


}
