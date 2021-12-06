package www.fiberathome.com.parkingapp.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentScheduleBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.model.Spinner;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.model.response.booking.TimeSlotResponse;
import www.fiberathome.com.parkingapp.ui.booking.BookingActivity;
import www.fiberathome.com.parkingapp.ui.booking.PaymentFragment;
import www.fiberathome.com.parkingapp.ui.booking.helper.DialogHelper;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ScheduleFragment extends BaseFragment implements DialogHelper.PayBtnClickListener,
        IOnBackPressListener {

    public static String areaPlaceId = "";
    public static String areaName;
    public static String areaCount;
    public DialogHelper.PayBtnClickListener payBtnClickListener;
    public long arrived, departure, difference;

    private BaseActivity context;

    private Date arrivedDate;
    private Date departedDate;

    private boolean setArrivedDate = false;
    private boolean more = false;
    private static boolean isInArea = false;
    private boolean isBookNowChecked = false;

    private static double lat;
    private static double lon;

    private static String parkingSlotCount;
    private String time = "";
    private String timeValue = "";

    private final List<www.fiberathome.com.parkingapp.model.Spinner> departureTimeDataList = new ArrayList<>();
    private List<List<String>> sensorAreaStatusList = new ArrayList<>();

    FragmentScheduleBinding binding;
    private FragmentChangeListener listener;
    private Date currentTime;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    public static ScheduleFragment newInstance(double lat, double lng, String areaName, String count, String placeId, boolean isInArea) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("long", lng);
        bundle.putString("areaName", areaName);
        bundle.putString("count", count);
        bundle.putString("areaPlacedId", placeId);
        bundle.putBoolean("isInArea", isInArea);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ScheduleFragment newInstance(String placeId, String mAreaName, String mParkingSlotCount,
                                               double mLat, double mLon, boolean mIsInArea) {
        areaPlaceId = placeId;
        areaName = mAreaName;
        parkingSlotCount = mParkingSlotCount;
        lat = mLat;
        lon = mLon;
        isInArea = mIsInArea;
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            more = getArguments().getBoolean("m");
            isInArea = getArguments().getBoolean("isInArea");
            areaPlaceId = getArguments().getString("areaPlacedId");
            lat = getArguments().getDouble("lat");
            lon = getArguments().getDouble("long");
            areaName = getArguments().getString("areaName");
            parkingSlotCount = getArguments().getString("parkingSlotCount");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            if (getActivity() instanceof HomeActivity) {
                context = (HomeActivity) getActivity();
            } else if (getActivity() instanceof BookingActivity) {
                context = (BookingActivity) getActivity();
            } else if (getActivity() instanceof ScheduleActivity) {
                context = (ScheduleActivity) getActivity();
            }
            currentTime = Calendar.getInstance().getTime();
            binding.textViewCurrentDate.setText(DateTimeUtils.getInstance().getCurrentDayTime());
            listener = (FragmentChangeListener) getActivity();
            payBtnClickListener = this;
            setDatePickerTime();
            setListeners();
        }
        getTimeSlots();
    }

    private void setDatePickerTime() {
        //add 30 minutes to date
        Date mFutureTime = new Date(); // Instantiate a Date object
        Calendar cal = Calendar.getInstance();
        cal.setTime(mFutureTime);
        cal.add(Calendar.MINUTE, 30);
        mFutureTime = cal.getTime();
        Date futureTime = mFutureTime;

        binding.arrivedPicker.setIsAmPm(true);
        binding.departurePicker.setIsAmPm(true);
        binding.arrivedPicker.setDefaultDate(currentTime);
        binding.arrivedPicker.setMinDate(currentTime);
        binding.departurePicker.setDefaultDate(mFutureTime);
        if (more) {
            setArrivedDate = true;
            binding.arrivedPicker.setEnabled(false);
            binding.arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));
            if (getArguments() != null) {
                Date arrived = new Date(getArguments().getLong("a"));
                Date departure = new Date(getArguments().getLong("d"));
                arrivedDate = arrived;
                departedDate = departure;
                binding.arrivedPicker.setDefaultDate(arrived);
                binding.departurePicker.setDefaultDate(departure);
            }
        } else {
            binding.departurePicker.setEnabled(false);
            arrivedDate = binding.arrivedPicker.getDate();
            departedDate = binding.departurePicker.getDate();
            arrived = arrivedDate.getTime();
        }
    }

    @Override
    public void onStart() {
        if (getArguments() != null) {
            more = getArguments().getBoolean("m");
        }
        setArrivedDate = more;
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.arrivedPicker.setIsAmPm(true);
        binding.arrivedPicker.setDefaultDate(currentTime);
        binding.arrivedPicker.setMinDate(currentTime);
        if (isInArea) {
            binding.cbBookNow.setVisibility(View.VISIBLE);
        } else {
            binding.cbBookNow.setVisibility(View.GONE);
        }
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }

    @Override
    public boolean onBackPressed() {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
            if (getActivity() instanceof HomeActivity) {
                listener.fragmentChange(HomeFragment.newInstance());
            } else if (getActivity() instanceof BookingActivity) {
                startActivityWithFinish(context, BookingActivity.class);
            } else if (getActivity() instanceof ScheduleActivity) {
                startActivityWithFinish(context, HomeActivity.class);
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void payBtnClick() {
    }

    private void setListeners() {
        binding.ivBackArrow.setOnClickListener(v -> onBackPressed());

        binding.cbBookNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isBookNowChecked = true;
                binding.arrivedPicker.setEnabled(false);
                binding.arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));
                arrivedDate = new Date();
            } else {
                isBookNowChecked = false;
                setArrivedDate = true;
                binding.arrivedPicker.setEnabled(true);
                binding.arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));
            }
        });

        binding.arrivedPicker.addOnDateChangedListener((displayed, date) -> {
            arrivedDate = date;
            isBookNowChecked = false;
            binding.cbBookNow.setChecked(false);
            binding.cbBookNow.setVisibility(View.INVISIBLE);

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

        binding.departurePicker.addOnDateChangedListener((displayed, date) -> {
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

        binding.setBtn.setOnClickListener(v -> {
            if (!setArrivedDate) {
                binding.cbBookNow.setEnabled(false);
                binding.arrivedPicker.setEnabled(false);
                binding.arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.disableColor));

                binding.departurePicker.setEnabled(true);
                binding.departureDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));

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
                    } else {
                        if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(), getDate(arrivedDate.getTime()), getDate((departure + arrivedDate.getTime())), areaPlaceId);
                        } else {
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                        }
                    }
                }
            }
        });

        binding.cancelBtn.setOnClickListener(v -> {
            if (binding.cbBookNow.isChecked()) {
                binding.cbBookNow.toggle();
            }
            binding.arrivedPicker.setDefaultDate(new Date());
            if (isInArea) {
                binding.cbBookNow.setVisibility(View.VISIBLE);
                binding.cbBookNow.setEnabled(true);
            }
            if (setArrivedDate) {
                binding.arrivedPicker.setEnabled(true);
                binding.arriveDisableLayout.setBackgroundColor(getResources().getColor(R.color.enableColor));
                setArrivedDate = false;
                if (getActivity() != null)
                    getActivity().getFragmentManager().popBackStack();
            } else {
                Timber.e("else called");
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

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String mPlaceId) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, mPlaceId, "1"); // 1 for request availability
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
                                    (departure + arrivedDate.getTime()) - arrivedDate.getTime(), mPlaceId, lat, lon, areaName, parkingSlotCount, isBookNowChecked, isInArea);
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
            }
        });
    }

    private void getTimeSlots() {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<TimeSlotResponse> call = request.getTimeSlot();
        call.enqueue(new Callback<TimeSlotResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeSlotResponse> call,
                                   @NonNull retrofit2.Response<TimeSlotResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
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
                                            departureTimeDataList.add(new Spinner(Double.parseDouble(timeValue), time));
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
            public void onFailure(@NonNull Call<TimeSlotResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }

    private void setSpinner(List<www.fiberathome.com.parkingapp.model.Spinner> departureTimeDataList) {
        UniversalSpinnerAdapter departureTimeAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                departureTimeDataList);

        try {
            departure = (long) (departureTimeDataList.get(0).getTimeValue() * 3600000);
        } catch (Exception e) {
            Timber.e(e.getCause());
        }
        binding.spinnerDepartureTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departure = (long) (departureTimeDataList.get(position).getTimeValue() * 3600000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerDepartureTime.setAdapter(departureTimeAdapter);
    }
}