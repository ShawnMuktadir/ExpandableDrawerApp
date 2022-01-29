package www.fiberathome.com.parkingapp.ui.reservation.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.DepartureTimeData;
import www.fiberathome.com.parkingapp.data.model.Spinner;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.reservation.ReservationResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.TimeSlotResponse;
import www.fiberathome.com.parkingapp.data.model.response.vehicle_list.UserVehicleListResponse;
import www.fiberathome.com.parkingapp.data.model.response.vehicle_list.Vehicle;
import www.fiberathome.com.parkingapp.databinding.FragmentScheduleBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationActivity;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationViewModel;
import www.fiberathome.com.parkingapp.ui.reservation.payment.PaymentFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ScheduleFragment extends BaseFragment implements IOnBackPressListener {

    private static boolean isInArea = false;
    public static String areaPlaceId = "";
    public static String areaName, areaNameBangla;
    public static String areaCount;
    public long arrived, departure, difference;

    private Date arrivedDate, departedDate;

    private boolean setArrivedDate = false;
    private boolean more = false;
    private boolean isBookNowChecked = false;

    private static double lat, lon;

    private static String parkingSlotCount;
    private String time = "";
    private String timeValue = "";
    private String selectedVehicleNo;

    private final List<www.fiberathome.com.parkingapp.data.model.Spinner> departureTimeDataList = new ArrayList<>();
    private List<List<String>> timeSlotArrayList = new ArrayList<>();

    private List<Vehicle> vehicleList = new ArrayList<>();
    private final List<www.fiberathome.com.parkingapp.data.model.Spinner> userVehicleDataList = new ArrayList<>();

    private BaseActivity context;
    private ReservationViewModel reservationViewModel;
    FragmentScheduleBinding binding;
    private FragmentChangeListener listener;

    private final ArrayList<DepartureTimeData> departureTimeDataArrayList = new ArrayList<>();
    private ScheduleDepartureTimeAdapter adapter;
    private TimePickerDialog mTimePicker;
    private Calendar currentCalendar;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    public static ScheduleFragment newInstance(double lat, double lng, String areaName, String areaNameBangla,
                                               String count, String placeId, boolean isInArea) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("long", lng);
        bundle.putString("areaName", areaName);
        bundle.putString("areaNameBangla", areaNameBangla);
        bundle.putString("parkingSlotCount", count);
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
            areaNameBangla = getArguments().getString("areaNameBangla");
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
            } else if (getActivity() instanceof ReservationActivity) {
                context = (ReservationActivity) getActivity();
            } else if (getActivity() instanceof ScheduleActivity) {
                context = (ScheduleActivity) getActivity();
            }
            reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
            currentCalendar = Calendar.getInstance();
            arrivedDate = Calendar.getInstance().getTime();
            binding.textViewCurrentDate.setText(DateTimeUtils.getInstance().getCurrentDayTime());
            listener = (FragmentChangeListener) getActivity();
            setCurrentDateTimeData();
            setListeners();
        }
        getUserVehicleList(Preferences.getInstance(context).getUser().getMobileNo());
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
        if (isInArea) {
            binding.linearLayoutBookNow.setVisibility(View.VISIBLE);
        } else {
            binding.linearLayoutBookNow.setVisibility(View.GONE);
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
            } else if (getActivity() instanceof ReservationActivity) {
                startActivityWithFinish(context, ReservationActivity.class);
            } else if (getActivity() instanceof ScheduleActivity) {
                startActivityWithFinish(context, HomeActivity.class);
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
        }
        return false;
    }

    private void setCurrentDateTimeData() {
        final Calendar calendar = Calendar.getInstance();
        String dateFormat = "MMMM dd, yyyy";
        String timeFormat = "hh:mm aa";
        SimpleDateFormat mDateFormat;
        SimpleDateFormat mTimeFormat;
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            mDateFormat = new SimpleDateFormat(dateFormat, new Locale("bn"));
            mTimeFormat = new SimpleDateFormat(timeFormat, new Locale("bn"));
        } else {
            mDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            mTimeFormat = new SimpleDateFormat(timeFormat, Locale.US);
        }
        binding.tvArriveDateTime.setText(mDateFormat.format(calendar.getTime()));
        binding.tvArriveTime.setText(mTimeFormat.format(calendar.getTime()));

        int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = currentCalendar.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
            currentCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            currentCalendar.set(Calendar.MINUTE, selectedMinute);
            if (calendar.getTime().getTime() > currentCalendar.getTime().getTime()) {
                mTimePicker.updateTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE);
                currentCalendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY);
                currentCalendar.set(Calendar.MINUTE, Calendar.MINUTE);
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.please_do_not_select_past_time));
            } else {
                binding.tvArriveTime.setText(mTimeFormat.format(currentCalendar.getTime()));
                arrivedDate = currentCalendar.getTime();
            }
        }, hour, minute, false); //Yes 24 hour time

        mTimePicker.setTitle(context.getResources().getString(R.string.select_time));
    }

    private void setListeners() {
        binding.ivBackArrow.setOnClickListener(v -> onBackPressed());

        binding.cbBookNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isBookNowChecked = true;
                binding.arriveDisableLayout.setBackgroundColor(context.getResources().getColor(R.color.disableColor));
                binding.arriveDisableLayout.setEnabled(false);
                binding.tvArriveDateTime.setEnabled(false);
                binding.tvArriveTime.setEnabled(false);
                arrivedDate = new Date();
            } else {
                isBookNowChecked = false;
                setArrivedDate = true;
                binding.arriveDisableLayout.setEnabled(true);
                binding.tvArriveDateTime.setEnabled(true);
                binding.tvArriveTime.setEnabled(true);
                binding.arriveDisableLayout.setBackgroundColor(context.getResources().getColor(R.color.enableColor));
            }
        });

        binding.btnConfirm.setOnClickListener(v -> {
            if (!setArrivedDate) {
                binding.cbBookNow.setEnabled(false);
                binding.arriveDisableLayout.setBackgroundColor(context.getResources().getColor(R.color.disableColor));
                binding.arriveDisableLayout.setEnabled(false);
                binding.tvArriveDateTime.setEnabled(false);
                binding.tvArriveTime.setEnabled(false);
                setArrivedDate = true;
            } else {
                binding.arriveDisableLayout.setEnabled(true);
                binding.tvArriveDateTime.setEnabled(true);
                binding.tvArriveTime.setEnabled(true);
                binding.arriveDisableLayout.setBackgroundColor(context.getResources().getColor(R.color.enableColor));
                if (departure != 0) {
                    if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                        storeReservation(Preferences.getInstance(context).getUser().getMobileNo(), getDate(arrivedDate.getTime()), getDate((departure + arrivedDate.getTime())), areaPlaceId);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                }
            }
        });

        binding.btnReset.setOnClickListener(v -> {
            if (binding.cbBookNow.isChecked()) {
                binding.cbBookNow.toggle();
            }
            if (isInArea) {
                binding.cbBookNow.setVisibility(View.VISIBLE);
                binding.cbBookNow.setEnabled(true);
            }
            if (setArrivedDate) {
                binding.arriveDisableLayout.setEnabled(true);
                binding.tvArriveDateTime.setEnabled(true);
                binding.tvArriveTime.setEnabled(true);
                binding.arriveDisableLayout.setBackgroundColor(context.getResources().getColor(R.color.enableColor));
                setArrivedDate = false;
                if (getActivity() != null)
                    getActivity().getFragmentManager().popBackStack();
            } else {
                Timber.e("else called");
            }
            if (adapter != null) {
                adapter.onReset();
                adapter.notifyDataSetChanged();
                if (departureTimeDataArrayList.size() > 0) {
                    departure = (long) (departureTimeDataArrayList.get(0).getTimeValue() * 3600000);
                }
            }
        });

        binding.tvArriveDateTime.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, day);
                String mFormat = "MMMM dd, yyyy";
                arrivedDate = currentCalendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat(mFormat, Locale.US);
                binding.tvArriveDateTime.setText(dateFormat.format(currentCalendar.getTime()));
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        binding.tvArriveTime.setOnClickListener(view -> {
            arrivedDate = currentCalendar.getTime();
            mTimePicker.show();
        });
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.US);
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

        reservationViewModel.storeReservationInit(mobileNo, arrivalTime, departureTime, mPlaceId, "1", selectedVehicleNo);
        reservationViewModel.getStoreReservationMutableData().observe(requireActivity(), (@NonNull ReservationResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                PaymentFragment paymentFragment = PaymentFragment.newInstance(arrivedDate, new Date((departure + arrivedDate.getTime())), getDate(arrivedDate.getTime()), getDate((departure + arrivedDate.getTime())),
                        getTimeDifference((departure + arrivedDate.getTime()) - arrivedDate.getTime()),
                        (departure + arrivedDate.getTime()) - arrivedDate.getTime(), mPlaceId, lat, lon, areaName, areaNameBangla, parkingSlotCount, isBookNowChecked, isInArea, response.getBill());
                listener.fragmentChange(paymentFragment);
            } else {
                DialogUtils.getInstance().showOnlyMessageDialog(response.getMessage(), context);
            }
        });
    }

    private void getUserVehicleList(String mobileNo) {
        showLoading(context);

        reservationViewModel.initUserVehicleList(mobileNo);
        reservationViewModel.getUserVehicleListMutableLiveDat().observe(requireActivity(), (@NonNull UserVehicleListResponse response) -> {
            hideLoading();
            getTimeSlots();
            if (!response.getError()) {
                vehicleList = response.getVehicle();
                if (vehicleList != null && !vehicleList.isEmpty()) {
                    for (Vehicle userVehicleList : vehicleList) {
                        try {
                            String vehicleNo = userVehicleList.getVehicleNo();
                            int priority = Integer.parseInt(userVehicleList.getPriority());
                            userVehicleDataList.add(new Spinner(priority, vehicleNo));
                        } catch (Exception e) {
                            Timber.e(e.getCause());
                        }
                    }
                    Timber.e("userVehicleDataList _. %s", new Gson().toJson(userVehicleDataList));
                    setVehicleListSpinner(userVehicleDataList);
                } else {
                    Timber.e("else called");
                }
            }
        });
    }

    private void getTimeSlots() {
        showLoading(context);

        reservationViewModel.initTimeSlotList();
        reservationViewModel.getTimeSlotListMutableLiveDat().observe(requireActivity(), (@NonNull TimeSlotResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                if (response.getTimeSlots() != null) {
                    timeSlotArrayList = response.getTimeSlots();
                    if (timeSlotArrayList != null) {
                        for (List<String> baseStringList : timeSlotArrayList) {
                            for (int i = 0; i < baseStringList.size(); i++) {
                                if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                                    if (i == 1) {
                                        time = baseStringList.get(i);
                                    }
                                } else {
                                    if (i == 5) {
                                        time = baseStringList.get(i);
                                    }
                                }


                                if (i == 2) {
                                    timeValue = baseStringList.get(i);
                                }
                            }
                            try {
                                DepartureTimeData departureTimeData = new DepartureTimeData(time, Double.parseDouble(timeValue));
                                departureTimeDataArrayList.add(departureTimeData);
                            } catch (NumberFormatException e) {
                                e.getCause();
                            }
                        }
                        setFragmentControls(departureTimeDataArrayList);
                    }
                }
            }
        });
    }

    private void setFragmentControls(ArrayList<DepartureTimeData> departureTimeDataArrayList) {
        // added data from arraylist to adapter class.
        if (departureTimeDataArrayList.size() > 0) {
            departure = (long) (departureTimeDataArrayList.get(0).getTimeValue() * 3600000);
        }
        adapter = new ScheduleDepartureTimeAdapter(departureTimeDataArrayList, context,
                (value) -> departure = (long) (value * 3600000));

        // setting grid layout manager to implement grid view.
        // in this method '3' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);

        // at last set adapter to recycler view.
        binding.recyclerViewDepartureTime.setLayoutManager(layoutManager);
        binding.recyclerViewDepartureTime.setAdapter(adapter);
    }

    private void setTotalDepartureTimeSpinner
            (List<www.fiberathome.com.parkingapp.data.model.Spinner> departureTimeDataList) {
        UniversalSpinnerAdapter departureTimeAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                departureTimeDataList);
        try {
            departure = (long) (departureTimeDataList.get(0).getTimeValue() * 3600000);
        } catch (Exception e) {
            Timber.e(e.getCause());
        }
    }

    private void setVehicleListSpinner
            (List<www.fiberathome.com.parkingapp.data.model.Spinner> vehicleList) {
        UniversalSpinnerAdapter userVehicleListAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                vehicleList);

        try {
            selectedVehicleNo = vehicleList.get(0).getValue();
        } catch (Exception e) {
            Timber.e(e.getCause());
        }
        binding.spinnerUserVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicleNo = vehicleList.get(position).getValue();
                Preferences.getInstance(context).setSelectedVehicleNo(selectedVehicleNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerUserVehicle.setAdapter(userVehicleListAdapter);
    }
}