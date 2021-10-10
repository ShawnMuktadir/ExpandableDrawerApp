package www.fiberathome.com.parkingapp.ui.schedule;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.BookedPlace;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.Reservation;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.module.notification.NotificationPublisher;
import www.fiberathome.com.parkingapp.ui.booking.PaymentFragment;
import www.fiberathome.com.parkingapp.ui.booking.helper.DialogHelper;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

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
    TextView textViewTotalParkingTime;

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

    private Unbinder unbinder;

    private Context context;

    private Date arrivedDate, departedDate;
    private boolean setArrivedDate = false;
    private boolean more = false;
    private FragmentChangeListener listener;
    public DialogHelper.PayBtnClickListener payBtnClickListener;
    private String markerUid = "";
    public long arrived, departure, difference;
    private double lat;
    private double lon;
    private String route, areaName, parkingSlotCount;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
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

            textViewCurrentDate.setText(DateTimeUtils.getInstance().getPSTTimeZoneCurrentDate());

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
            departurePicker.setDefaultDate(currentTime);

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
                Timber.e("onDateChanged: -> %s", date);
                Timber.e("onDateChanged: arrivedDate: -> %s", arrivedDate);

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
                Timber.e("onDateChanged: -> %s", date);
                Timber.e("onDateChanged: departureDate: -> %s", departedDate);

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
                Timber.d("onClick: didnot entered to else");
                if (departedDate.getTime() - arrivedDate.getTime() < 0) {
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

                    //open DialogHelper with total amount, time difference

                    /*Dialog dialog = new Dialog(requireActivity());
                        dialog.setContentView(R.layout.voucher_dialog);
                        DialogHelper dialogHelper = new DialogHelper(dialog, requireActivity(), getDate(arrivedDate.getTime()), getDate(departerDate.getTime()),
                                getTimeDiffrence(departerDate.getTime() - arrivedDate.getTime()),
                                departerDate.getTime() - arrivedDate.getTime(), payBtnClickListener);
                        dialogHelper.initDialog();
                        dialog.show();*/

                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                        long diff = departedDate.getTime() - arrivedDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;
                        Timber.e("hours -> %s", hours);
                        Timber.e("minutes -> %s", minutes);
                        if (minutes > 120) {
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.booking_time_rules));
                        } else {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    getDate(arrivedDate.getTime()), getDate(departedDate.getTime()), markerUid);
                        }
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
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
        Bundle bundle = new Bundle();
        Timber.d("onClick: -> %s", arrivedDate.getTime());
        Timber.d("onClick: -> %s", departedDate.getTime());
        //bundle.putBoolean("s", true);
        bundle.putLong("arrived", arrivedDate.getTime());
        bundle.putLong("departure", departedDate.getTime());
        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.setArguments(bundle);
        listener.fragmentChange(paymentFragment);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String markerUid) {
        showLoading(context);
        String totalBookingTime = arrivalTime + "-" + departureTime;
        textViewActionBarTitle.setText(totalBookingTime);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, markerUid);
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call,
                                   @NonNull retrofit2.Response<ReservationResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    overlay.setVisibility(View.VISIBLE);
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.reservation_successful));
                    //check 15 minutes before departure
                    //ToDo
                    startAlarm(convertLongToCalendar(departedDate.getTime()));
                    BookedPlace bookedPlace = new BookedPlace();
                    bookedPlace.setBookedUid(markerUid);
                    bookedPlace.setLat(lat);
                    bookedPlace.setLon(lon);
                    bookedPlace.setRoute(route);
                    bookedPlace.setAreaName(areaName);
                    bookedPlace.setParkingSlotCount(parkingSlotCount);
                    bookedPlace.setIsBooked(true);

                    Preferences.getInstance(context).setBooked(bookedPlace);
                    if (isGPSEnabled()) {
                        if (getActivity() != null)
                            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                        ApplicationUtils.replaceFragmentWithAnimation(getParentFragmentManager(), HomeFragment.newInstance(bookedPlace));
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
                    }
                } else {
                    overlay.setVisibility(View.GONE);
                    Toast.makeText(getContext(), context.getResources().getString(R.string.reservation_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
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
}
