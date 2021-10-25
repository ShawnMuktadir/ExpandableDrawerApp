package www.fiberathome.com.parkingapp.ui.schedule;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.module.notification.NotificationPublisher;
import www.fiberathome.com.parkingapp.ui.booking.PaymentFragment;
import www.fiberathome.com.parkingapp.ui.booking.helper.DialogHelper;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ScheduleFragment extends BaseFragment implements DialogHelper.PayBtnClickListener,
        IOnBackPressListener, AdapterView.OnItemSelectedListener {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.overlay)
    View overlay;

    @BindView(R.id.textViewCurrentDate)
    TextView textViewCurrentDate;

    @BindView(R.id.ivBackArrow)
    ImageView ivBackArrow;

    @BindView(R.id.action_bar_title)
    TextView textViewActionBarTitle;

    @BindView(R.id.action_bar_sub_title)
    TextView textViewActionBarSubTitle;

    @BindView(R.id.tvTotalParkingTime)
    TextView tvTotalParkingTime;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.setBtn)
    Button setBtn;

    @BindView(R.id.cancelBtn)
    Button cancelBtn;

    @BindView(R.id.arriveDisableLayout)
    LinearLayout arriveDisableLayout;

    @BindView(R.id.departureDisableLayout)
    LinearLayout departureDisableLayout;

    @BindView(R.id.arrivedPicker)
    SingleDateAndTimePicker arrivedPicker;

    @BindView(R.id.departurePicker)
    SingleDateAndTimePicker departurePicker;
    @BindView(R.id.classSpinner)
    Spinner classSpinner;

    private Unbinder unbinder;

    private Context context;

    private Date arrivedDate, departedDate, mFutureTime;
    private boolean setArrivedDate = false;
    private boolean more = false;
    private FragmentChangeListener listener;
    public DialogHelper.PayBtnClickListener payBtnClickListener;
    public static String markerUid = "";
    public long arrived, departure, difference;
    private double lat;
    private double lon;
    private String route;
    public static String areaName;
    private String parkingSlotCount;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDataList = new ArrayList<>();

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    public static ScheduleFragment newInstance(String placeId, String mAreaName) {
        markerUid = placeId;
        areaName = mAreaName;
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {
            unbinder = ButterKnife.bind(this, view);

            context = getActivity();

            textViewCurrentDate.setText(DateTimeUtils.getInstance().getCurrentDayTime());



            listener = (FragmentChangeListener) getActivity();
            payBtnClickListener = this;

            // Spinner click listener
            spinner.setOnItemSelectedListener(this);

            // Spinner Drop down elements
            List<String> categories = new ArrayList<>();
            categories.add("Item 1");
            categories.add("Item 2");

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);

            Date currentTime = Calendar.getInstance().getTime();
            //add 30 minutes to date
            mFutureTime = new Date(); // Instantiate a Date object
            Calendar cal = Calendar.getInstance();
            cal.setTime(mFutureTime);
            cal.add(Calendar.MINUTE, 30);
            mFutureTime = cal.getTime();
            Date futureTime = mFutureTime;
            if (getArguments() != null) {
                more = getArguments().getBoolean("m");
                markerUid = getArguments().getString("markerUid");
                lat = getArguments().getDouble("lat");
                lon = getArguments().getDouble("long");
                route = getArguments().getString("route");
                areaName = getArguments().getString("areaName");
                parkingSlotCount = getArguments().getString("parkingSlotCount");
                Timber.e("markerUid -> %s", markerUid);
            }
            arrivedPicker.setIsAmPm(true);
            departurePicker.setIsAmPm(true);
            arrivedPicker.setDefaultDate(currentTime);
            departurePicker.setDefaultDate(mFutureTime);

            if (more) {
                setArrivedDate = true;
                arrivedPicker.setEnabled(false);
                arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));
                Date arrived = new Date(getArguments().getLong("a"));
                Date departure = new Date(getArguments().getLong("d"));

                arrivedDate = arrived;
                departedDate = departure;

                arrivedPicker.setDefaultDate(arrived);
                departurePicker.setDefaultDate(departure);

            } else {
                departurePicker.setEnabled(false);
                departureDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));

                arrivedDate = arrivedPicker.getDate();
                Timber.d("arrived date before scrolling -> %s", arrivedDate);
                departedDate = departurePicker.getDate();
                Timber.d("departure date before scrolling -> %s", departedDate);
                arrived = arrivedDate.getTime();
                departure = departedDate.getTime();
                difference = arrived - departure;
                Timber.e("difference -> %s", difference);
            }

            arrivedPicker.addOnDateChangedListener((displayed, date) -> {
                arrivedDate = date;
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                final long[] pattern = {0, 10};
                final int[] amplitudes = {50, 50};

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createWaveform(pattern, amplitudes, 0);
                    assert vibrator != null;
                    vibrator.vibrate(effect);
                    (new Handler()).postDelayed(vibrator::cancel, 50);
                } else {
                    assert vibrator != null;
                    vibrator.vibrate(10);
                }
            });

            departurePicker.addOnDateChangedListener((displayed, date) -> {
                departedDate = date;
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                final long[] pattern = {0, 10};
                final int[] amplitudes = {50, 50};

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createWaveform(pattern, amplitudes, 0);
                    assert vibrator != null;
                    vibrator.vibrate(effect);
                    (new Handler()).postDelayed(vibrator::cancel, 50);
                } else {
                    assert vibrator != null;
                    vibrator.vibrate(10);
                }
            });

            setListeners();
        }
//        UniversalSpinnerAdapter vehicleClassAdapter = new UniversalSpinnerAdapter(context,
//                android.R.layout.simple_spinner_item,
//                populateVehicleClassData());
//
//        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        classSpinner.setAdapter(vehicleClassAdapter);

    }
    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleClassData() {
        classDataList = new ArrayList<>();

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "2 Hour"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "1 Hour 30 Min"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "1 hour"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "30 Min"));

        return classDataList;
    }
    private void setListeners() {
        ivBackArrow.setOnClickListener(v -> onBackPressed());

        setBtn.setOnClickListener(v -> {
            Timber.d("onClick: didnot entered to condition");
            if (!setArrivedDate) {
                Timber.d("onClick:  entered to if");
                arrivedPicker.setEnabled(false);
                arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));

                departurePicker.setEnabled(true);
                departureDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));

                setArrivedDate = true;
            } else {
                long diff = departedDate.getTime() - arrivedDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                Timber.d("onClick: did not entered to else");
                Timber.d("seconds-> %s", seconds);
                Timber.d("minutes-> %s", minutes);
                if (diff < 0) {
                    Toast.makeText(requireActivity(), context.getResources().getString(R.string.departure_time_less_arrive_time), Toast.LENGTH_SHORT).show();
                }
                /*else if (departedDate.getTime() < mFutureTime.getTime() - arrivedDate.getTime()) {
                    Timber.e(String.valueOf(departedDate.getTime()));
                    Timber.e(String.valueOf(mFutureTime.getTime() - arrivedDate.getTime()));
                    Toast.makeText(requireActivity(), context.getResources().getString(R.string.departure_time_less_thirty_arrive_time), Toast.LENGTH_SHORT).show();
                }*/
                else {
                    if(isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                        storeReservation(Preferences.getInstance(context).getUser().getMobileNo(), getDate(arrivedDate.getTime()), getDate(departedDate.getTime()), markerUid);
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(v -> {
            if (setArrivedDate) {
                arrivedPicker.setEnabled(true);
                arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));
                setArrivedDate = false;
                if (getActivity() != null)
                    getActivity().getFragmentManager().popBackStack();
            } else {
                Timber.e("else called");
            }
        });
    }

    @Override
    public void onStart() {
        if (getArguments() != null) {
            more = getArguments().getBoolean("m");
        }
        setArrivedDate = more;
        super.onStart();
        Timber.e("onStart called ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("onDestroyView called ");
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void payBtnClick() {
//        storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),getDate(arrivedDate.getTime()),getDate(departedDate.getTime()),markerUid);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @SuppressLint("DefaultLocale")
    private String getTimeDifference(long difference) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)) // The change is in this line
        );

    }

    public long diffTime() {
        Timber.e("diffTime called");
        long min = 0;
        long difference;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US); // for 12-hour system, hh should be used instead of HH
            // There is no minute different between the two, only 8 hours difference. We are not considering Date, So minute will always remain 0
            Date date1 = simpleDateFormat.parse(String.valueOf(arrived));
            Date date2 = simpleDateFormat.parse(String.valueOf(departure));

            assert date1 != null;
            assert date2 != null;
            difference = (date2.getTime() - date1.getTime()) / 1000;
            long hours = difference % (24 * 3600) / 3600; // Calculating Hours
            long minute = difference % 3600 / 60; // Calculating minutes if there is any minutes difference
            min = minute + (hours * 60); // This will be our final minutes. Multiplying by 60 as 1 hour contains 60 mins
            Timber.e("diffTime min -> %s", min);
        } catch (Throwable e) {
            Timber.e("diffTime min catch-> %s", min);
            e.printStackTrace();
        }
        return min;
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }

        return false;
    }

    private void startAlarm(Calendar c) {
        Timber.e("startAlarm called");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        Intent intent2 = new Intent(context, NotificationPublisher.class);
        intent2.putExtra("ended", "Book Time Up");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 2, intent2, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis() - 900000, pendingIntent);
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(), pendingIntent2);
    }

    public Calendar convertLongToCalendar(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String markerUid) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, markerUid, "1"); // 1 for request availability
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call,
                                   @NonNull retrofit2.Response<ReservationResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            PaymentFragment paymentFragment = PaymentFragment.newInstance(arrivedDate, departedDate, getDate(arrivedDate.getTime()), getDate(departedDate.getTime()),
                                    getTimeDifference(departedDate.getTime() - arrivedDate.getTime()),
                                    departedDate.getTime() - arrivedDate.getTime(), markerUid, lat, lon, route, areaName, parkingSlotCount);
                            listener.fragmentChange(paymentFragment);
                        } else {
                            DialogUtils.getInstance().showOnlyMessageDialog(response.body().getMessage(), context);

                        }

                    } else {
                        Toast.makeText(getContext(), context.getResources().getString(R.string.reservation_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
//                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

}
