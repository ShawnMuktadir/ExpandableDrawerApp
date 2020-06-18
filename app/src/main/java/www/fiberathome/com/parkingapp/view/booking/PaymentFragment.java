package www.fiberathome.com.parkingapp.view.booking;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.view.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;


public class PaymentFragment extends Fragment {

    private ProgressBar mProgressBar;
    private long arrived, departure;
    private String TAG = getClass().getSimpleName();
    private FragmentChangeListener listener;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        mProgressBar = view.findViewById(R.id.progressBar);
        listener = (FragmentChangeListener) getActivity();

        //assert getArguments() != null;
        if (getArguments() != null) {

            arrived = getArguments().getLong("arrived", 0);
            departure = getArguments().getLong("departure", 0);
            Log.d(TAG, "onViewCreated: " + arrived);
            Log.d(TAG, "onViewCreated: " + departure);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: payment");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireActivity(), "Payment Successful", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putBoolean("s", true);
                bundle.putLong("arrived", arrived);
                bundle.putLong("departure", departure);
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                listener.fragmentChange(homeFragment);
            }
        }, 5000);

    }
}
