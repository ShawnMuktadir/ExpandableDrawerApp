package www.fiberathome.com.parkingapp.ui.booking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookedFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    private long arrived, departure;
    private TextView arrivedtimeTV, departuretimeTV, timeDifferenceTV, textViewTermsCondition;
    private long difference;
    private Button moreBtn;
    private Button btnCarDeparture;
    private Button liveParkingBtn;
    private FragmentChangeListener listener;

    private Context context;

    public BookedFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_booked, container, false);

        listener = (FragmentChangeListener) getActivity();
        arrivedtimeTV = view.findViewById(R.id.arrivedtimeTV);
        departuretimeTV = view.findViewById(R.id.departureTimeTV);
        timeDifferenceTV = view.findViewById(R.id.timeDifferenceTV);
        textViewTermsCondition = view.findViewById(R.id.textViewTermsCondition);
        moreBtn = view.findViewById(R.id.moreBtn);
        btnCarDeparture = view.findViewById(R.id.btnCarDeparture);
        liveParkingBtn = view.findViewById(R.id.liveParkingBtn);

        assert getArguments() != null;
        arrived = getArguments().getLong("arrived", 0);
        departure = getArguments().getLong("departure", 0);
        difference = departure - arrived;

        Log.d(TAG, "onCreateView: " + arrived + "    " + departure);
        Log.d(TAG, "onCreateView: difference:" + difference);
       /* String arrivedDate=getDate(arrived);
        String departureDate=getDate(departure);

        timeElavation.setText("Arrived "+arrivedDate+"-"+departureDate+" Departure");
        timeDifference.setText(getDate(difference));
*/
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrivedtimeTV.setText("Arrived " + getDate(arrived));
        departuretimeTV.setText("Departure " + getDate(departure));
        timeDifferenceTV.setText(getTimeDifference(difference) + " min");

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("m", true);
                bundle.putLong("a", arrived);
                bundle.putLong("d", departure);
                scheduleFragment.setArguments(bundle);
                listener.fragmentChange(scheduleFragment);
            }
        });

        btnCarDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Car Departure Coming Soon!!!", Toast.LENGTH_SHORT).show();
            }
        });

        liveParkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Live Parking Coming Soon!!!", Toast.LENGTH_SHORT).show();
            }
        });

        textViewTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "T&C Coming Soon!!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("DefaultLocale")
    private String getTimeDifference(long difference) {

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)) // The change is in this line
        );
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }
}
