package www.fiberathome.com.parkingapp.ui.booking;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.ui.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.ui.parking.ParkingAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingsFragment extends Fragment {

    @BindView(R.id.recyclerViewBooking)
    RecyclerView recyclerViewBooking;
    @BindView(R.id.textViewNoData)
    TextView textViewNoData;
    @BindView(R.id.imageViewCross)
    ImageView imageViewCross;

    private BookingsAdapter bookingsAdapter;

    private Context context;
    private View view;

    public BookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(this, view);

        initUI();
        setListeners();
        setFragmentControls();
        return view;
    }

    private void initUI() {

    }

    private void setListeners() {
        imageViewCross.setOnClickListener(v -> {
            if (getActivity() != null) {
//               getActivity().onBackPressed();
                ((MainActivity) getActivity()).toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                ((MainActivity) getActivity()).navigationView.getMenu().getItem(0).setChecked(true);
            }
            if (getFragmentManager() != null) {
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
//            }
        });
    }

    private void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
//        textViewNoData.setText(context.getString(R.string.no_record_found));
    }

    private void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    private void setFragmentControls() {
//        this.sensorAreas = sensorAreas;
        recyclerViewBooking.setHasFixedSize(true);
        recyclerViewBooking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        bookingsAdapter = new BookingsAdapter(context, this);
        recyclerViewBooking.setAdapter(bookingsAdapter);
    }
}
