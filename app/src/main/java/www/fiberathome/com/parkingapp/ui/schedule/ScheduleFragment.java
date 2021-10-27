package www.fiberathome.com.parkingapp.ui.schedule;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.annotation.SuppressLint;
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
import www.fiberathome.com.parkingapp.model.response.booking.TimeSlotsResponse;
import www.fiberathome.com.parkingapp.ui.booking.PaymentFragment;
import www.fiberathome.com.parkingapp.ui.booking.helper.DialogHelper;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ScheduleFragment extends BaseFragment implements DialogHelper.PayBtnClickListener,
        IOnBackPressListener {

    public static String markerUid = "";
    public static String areaName;
    private final String TAG = getClass().getSimpleName();
    public DialogHelper.PayBtnClickListener payBtnClickListener;
    public long arrived, departure, difference;
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
    Spinner departureTimeSpinner;
    private Unbinder unbinder;
    private Context context;
    private Date arrivedDate, departedDate, mFutureTime;
    private boolean setArrivedDate = false;
    private boolean more = false;
    private FragmentChangeListener listener;
    private double lat;
    private double lon;
    private String route;
    private String parkingSlotCount;
    private final List<www.fiberathome.com.parkingapp.model.Spinner> departureTimeDataList = new ArrayList<>();
    private List<List<String>> sensorAreaStatusList = new ArrayList<>();
    private UniversalSpinnerAdapter departureTimeAdapter;

    private String time = "";
    private String timeValue = "";

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
            getTimeSlots();
        }

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
                if (departure != 0) {
                    long diff = (departure + arrivedDate.getTime()) - arrivedDate.getTime();
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
                    else {
                        if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(), getDate(arrivedDate.getTime()), getDate((departure + arrivedDate.getTime())), markerUid);
                        }
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
        //storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),getDate(arrivedDate.getTime()),getDate(departedDate.getTime()),markerUid);
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
            Date date2 = simpleDateFormat.parse(String.valueOf((departure + arrivedDate.getTime())));

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
                            PaymentFragment paymentFragment = PaymentFragment.newInstance(arrivedDate, new Date((departure + arrivedDate.getTime())), getDate(arrivedDate.getTime()), getDate((departure + arrivedDate.getTime())),
                                    getTimeDifference((departure + arrivedDate.getTime()) - arrivedDate.getTime()),
                                    (departure + arrivedDate.getTime()) - arrivedDate.getTime(), markerUid, lat, lon, route, areaName, parkingSlotCount);
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
                //ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private void getTimeSlots() {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<TimeSlotsResponse> call = request.getTimeSlots();
        call.enqueue(new Callback<TimeSlotsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeSlotsResponse> call,
                                   @NonNull retrofit2.Response<TimeSlotsResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().isError()) {
                            if (response.body().getSensors() != null) {
                                sensorAreaStatusList = response.body().getSensors();
                                if (sensorAreaStatusList != null) {
                                    for (List<String> baseStringList : sensorAreaStatusList) {
                                        for (int i = 0; i < baseStringList.size(); i++) {
                                            if (i == 1) {
                                                time = baseStringList.get(i);
                                            }

                                            if (i == 2) {
                                                timeValue = baseStringList.get(i);
                                            }
                                        }
                                        try {
                                            departureTimeDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(MathUtils.getInstance().convertToInt(timeValue), time));
                                        } catch (NumberFormatException e) {
                                            e.getCause();
                                        }
                                    }
                                    setSpinner(departureTimeDataList);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeSlotsResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }

    private void setSpinner(List<www.fiberathome.com.parkingapp.model.Spinner> departureTimeDataList) {
        departureTimeAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                departureTimeDataList);

        departure = departureTimeDataList.get(0).getId() * 3600000L;
        departureTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departure = departureTimeDataList.get(position).getId() * 3600000L;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        departureTimeSpinner.setAdapter(departureTimeAdapter);
    }

}
