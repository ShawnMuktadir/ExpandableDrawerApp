package www.fiberathome.com.parkingapp.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentBookingBinding;
import www.fiberathome.com.parkingapp.databinding.FragmentNotificationBinding;

public class NotificationFragment extends BaseFragment {

    FragmentNotificationBinding binding;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
