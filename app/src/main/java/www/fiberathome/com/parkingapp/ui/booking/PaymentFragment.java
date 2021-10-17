package www.fiberathome.com.parkingapp.ui.booking;

import static android.content.Context.LOCATION_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.BookedPlace;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.module.notification.NotificationPublisher;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

public class PaymentFragment extends BaseFragment implements IOnBackPressListener {

    static Date arrivedDate, departureDate;
    static String arrivedTime, departureTime, timeDifference, placeId, route, areaName, parkingSlotCount;
    static long differenceUnit;
    static double lat, lon;
    private TextView tvArrivedTime, tvDepartureTime, tvTimeDifference, tvSubTotal, tvTotal, tvEditSlot,
            tvTermsCondition, tvPromo, tvParkingSlotName, actionBarTitle;
    private ImageView ivBackArrow;
    private Button payBtn;
    private HomeActivity context;
    private FragmentChangeListener listener;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(Date mArrivedDate, Date mDepartedDate, String mArrivedTime,
                                              String mDepartureTime, String mTimeDifference, long mDifferenceUnit,
                                              String mMarkerUid, double mLat, double mLon, String mRoute, String mAreaName, String mParkingSlotCount) {
        arrivedDate = mArrivedDate;
        departureDate = mDepartedDate;
        arrivedTime = mArrivedTime;
        departureTime = mDepartureTime;
        timeDifference = mTimeDifference;
        differenceUnit = mDifferenceUnit;
        placeId = mMarkerUid;
        lat = mLat;
        lon = mLon;
        route = mRoute;
        areaName = mAreaName;
        parkingSlotCount = mParkingSlotCount;
        return new PaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            context = (HomeActivity) getActivity();
            listener = context;
            initUI(view);
            setListeners();
            setData();
            setBill();
        }
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
        context.onBackPressed();
        return false;
    }

    private void initUI(View view) {
        tvArrivedTime = view.findViewById(R.id.tvArrivedTime);
        tvDepartureTime = view.findViewById(R.id.tvDepartureTime);
        tvTimeDifference = view.findViewById(R.id.tvDifferenceTime);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvTotal = view.findViewById(R.id.tvTotal);
        payBtn = view.findViewById(R.id.btnPay);
        tvEditSlot = view.findViewById(R.id.tvEditSlot);
        tvTermsCondition = view.findViewById(R.id.tvTermCondition);
        tvPromo = view.findViewById(R.id.tvPromo);
        tvParkingSlotName = view.findViewById(R.id.tvParkingSlotName);
        actionBarTitle = view.findViewById(R.id.action_bar_title);
        ivBackArrow = view.findViewById(R.id.ivBackArrow);
    }

    private void setListeners() {
        ivBackArrow.setOnClickListener(v -> {
            ScheduleFragment scheduleFragment = ScheduleFragment.newInstance(placeId, areaName);
            listener.fragmentChange(scheduleFragment);
        });

        tvEditSlot.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        tvPromo.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        tvTermsCondition.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        payBtn.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                long diff = arrivedDate.getTime() - departureDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                Timber.e("hours -> %s", hours);
                Timber.e("minutes -> %s", minutes);
                if (minutes > 120) {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.booking_time_rules));
                } else {
                  if(isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                      storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                              getDate(arrivedDate.getTime()), getDate(departureDate.getTime()), placeId);
                  }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });
    }
    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("else called");
        }

        return false;
    }
    private void setData() {
        tvArrivedTime.setText(arrivedTime);
        tvDepartureTime.setText(departureTime);
        tvTimeDifference.setText(new StringBuilder().append(timeDifference).append(" min").toString());
        tvParkingSlotName.setText(areaName);
    }

    private void setBill() {
        final double perMintBill = 0.6666666667;
        DecimalFormat df = new DecimalFormat("##.##");
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceUnit);
        tvSubTotal.setText(new StringBuilder().append("BDT ").append(df.format(perMintBill * minutes)).toString());
        tvTotal.setText(new StringBuilder().append("BDT ").append(df.format(perMintBill * minutes)).toString());
        payBtn.setText(new StringBuilder().append("Pay BDT ").append(df.format(perMintBill * minutes)).toString());
    }

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String markerUid) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, markerUid, "2");
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call,
                                   @NonNull retrofit2.Response<ReservationResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getUid() != null) {
                            Timber.e("response -> %s", new Gson().toJson(response.body()));
                            TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.reservation_successful));
                            //check 15 minutes before departure
                            //ToDo

                            BookedPlace bookedPlace = new BookedPlace();
                            bookedPlace.setBookedUid(response.body().getUid());
                            bookedPlace.setLat(lat);
                            bookedPlace.setLon(lon);
                            bookedPlace.setRoute(route);
                            bookedPlace.setAreaName(areaName);
                            bookedPlace.setParkingSlotCount(parkingSlotCount);
                            bookedPlace.setDepartedDate(departureDate.getTime());
                            bookedPlace.setArriveDate(arrivedDate.getTime());
                            bookedPlace.setPlaceId(markerUid);
                            bookedPlace.setIsBooked(true);

                            Preferences.getInstance(context).setBooked(bookedPlace);
                            startAlarm(convertLongToCalendar(Preferences.getInstance(context).getBooked().getArriveDate()));
                            if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                                actionBarTitle.setText(context.getResources().getString(R.string.booking_payment));
                                listener.fragmentChange(HomeFragment.newInstance(bookedPlace));
                                //ApplicationUtils.replaceFragmentWithAnimation(getParentFragmentManager(), HomeFragment.newInstance(bookedPlace));
                            } else {
                                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
                            }
                        } else {
                            DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.parking_slot_not_available), context);
                            return;
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
    private void startAlarm(Calendar c) {
        Timber.e("startAlarm called");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("Started", "Booked Time About to start for :"+ Preferences.getInstance(context).getBooked().getAreaName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis() - 900000, pendingIntent);
    }

    public Calendar convertLongToCalendar(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }
}
