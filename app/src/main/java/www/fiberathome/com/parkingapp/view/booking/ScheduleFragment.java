package www.fiberathome.com.parkingapp.view.booking;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.view.booking.helper.DialogHelper;
import www.fiberathome.com.parkingapp.view.booking.listener.FragmentChangeListener;


public class ScheduleFragment extends Fragment implements DialogHelper.PayBtnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Button setBtn, cancelBtn;
    private SingleDateAndTimePicker arrivedPicker, departurePicker;
    private Date arrivedDate, departerDate;
    private LinearLayout arriveDisableLayout, departureDisableLayout;
    private boolean setArrivedDate = false;
    private boolean more = false;
    private FragmentChangeListener listener;
    private DialogHelper.PayBtnClickListener payBtnClickListener;


    public ScheduleFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        arrivedPicker = view.findViewById(R.id.arrivedPicker);
        departurePicker = view.findViewById(R.id.departurePicker);
        setBtn = view.findViewById(R.id.saveBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        arriveDisableLayout = view.findViewById(R.id.arrivedLayout);
        departureDisableLayout = view.findViewById(R.id.departureDisable);

        listener = (FragmentChangeListener) getActivity();
        payBtnClickListener = this;

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: ");
        Date currentTime = Calendar.getInstance().getTime();

        //assert getArguments() != null;
        /*if(getArguments()!=null)
        {*/
        if (getArguments() != null) {
            more = getArguments().getBoolean("m");
        }
        //}
        arrivedPicker.setIsAmPm(true);
        departurePicker.setIsAmPm(true);
        arrivedPicker.setDefaultDate(currentTime);
        departurePicker.setDefaultDate(currentTime);


        if (more) {
            setArrivedDate = true;
            arrivedPicker.setEnabled(false);
            arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));
            Date arrived = new Date(getArguments().getLong("a"));
            Date departure = new Date(getArguments().getLong("d"));


            arrivedDate = arrived;
            departerDate = departure;

            arrivedPicker.setDefaultDate(arrived);
            departurePicker.setDefaultDate(departure);

        } else {
            departurePicker.setEnabled(false);
            departureDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));

            arrivedDate = arrivedPicker.getDate();
            Log.d(TAG, "arrived date before scrolling " + arrivedDate);
            departerDate = departurePicker.getDate();
            Log.d(TAG, "departure date before scrolling " + departerDate);
        }

        arrivedPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                arrivedDate = date;
                Log.d(TAG, "onDateChanged: " + date);
                Log.d(TAG, "onDateChanged: arrivedDate" + arrivedDate);
            }
        });

        departurePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                departerDate = date;
                Log.d(TAG, "onDateChanged: " + date);
                Log.d(TAG, "onDateChanged: departureDate" + departerDate);
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: didnot entered to condition");
                if (!setArrivedDate) {
                    Log.d(TAG, "onClick:  entered to if");
                    arrivedPicker.setEnabled(false);
                    arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));

                    departurePicker.setEnabled(true);
                    departureDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));


                    setArrivedDate = true;
                } else {
                    Log.d(TAG, "onClick: didnot entered to else");
                    if (departerDate.getTime() - arrivedDate.getTime() < 0) {
                        Toast.makeText(requireActivity(), "Departure time can't less than arrived time", Toast.LENGTH_SHORT).show();
                    } else {
                        /*Bundle bundle = new Bundle();
                        Log.d(TAG, "onClick: " + arrivedDate.getTime());
                        Log.d(TAG, "onClick: " + departerDate.getTime());
                        bundle.putBoolean("s", true);
                        bundle.putLong("arrived", arrivedDate.getTime());
                        bundle.putLong("departure", departerDate.getTime());
                        //BookedFragment bookedFragment=new BookedFragment();
                        //bookedFragment.setArguments(bundle);
                        HomeFragment homeFragment = new HomeFragment();
                        homeFragment.setArguments(bundle);
                        listener.FragmentChange(homeFragment);*/
                        Dialog dialog = new Dialog(requireActivity());
                        dialog.setContentView(R.layout.voucher_dialog);
                        DialogHelper dialogHelper = new DialogHelper(dialog, requireActivity(), getDate(arrivedDate.getTime()), getDate(departerDate.getTime()),
                                getTimeDiffrence(departerDate.getTime() - arrivedDate.getTime())
                                , departerDate.getTime() - arrivedDate.getTime(), payBtnClickListener);
                        dialogHelper.initDialog();
                        dialog.show();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setArrivedDate) {
                    arrivedPicker.setEnabled(true);
                    arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));
                    setArrivedDate = false;
                } else {

                }
            }
        });

    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private String getTimeDiffrence(long difference) {

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)) // The change is in this line
        );
    }

    @Override
    public void onStart() {
        if (getArguments() != null) {
            more = getArguments().getBoolean("m");
        }
        if (more) {
            setArrivedDate = true;
        } else {
            setArrivedDate = false;
        }
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void payBtnClick() {
        Bundle bundle = new Bundle();
        Log.d(TAG, "onClick: " + arrivedDate.getTime());
        Log.d(TAG, "onClick: " + departerDate.getTime());
        // bundle.putBoolean("s", true);
        bundle.putLong("arrived", arrivedDate.getTime());
        bundle.putLong("departure", departerDate.getTime());
        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.setArguments(bundle);
        listener.fragmentChange(paymentFragment);
    }
}
