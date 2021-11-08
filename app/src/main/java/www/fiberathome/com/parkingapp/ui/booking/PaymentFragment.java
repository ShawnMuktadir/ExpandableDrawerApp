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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentPaymentBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.model.BookedPlace;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.service.notification.NotificationPublisher;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
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

    private HomeActivity context;
    private FragmentChangeListener listener;

    FragmentPaymentBinding binding;
    private double netBill;
    BookedPlace bookedPlace;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(Date mArrivedDate, Date mDepartedDate, String mArrivedTime,
                                              String mDepartureTime, String mTimeDifference, long mDifferenceUnit,
                                              String mMarkerUid, double mLat, double mLon, String mRoute, String mAreaName,
                                              String mParkingSlotCount) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            context = (HomeActivity) getActivity();
            listener = context;
            setListeners();
            setData();
            netBill = setBill();
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

    private void setListeners() {
        binding.ivBackArrow.setOnClickListener(v -> {
            ScheduleFragment scheduleFragment = ScheduleFragment.newInstance(placeId, areaName);
            listener.fragmentChange(scheduleFragment);
        });

        binding.tvEditSlot.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        binding.tvPromo.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        binding.tvTermCondition.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        binding.btnPay.setOnClickListener(v -> {
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
                    if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                        if (!Preferences.getInstance(context).getBooked().getIsBooked()
                                && Preferences.getInstance(context).getBooked().getBill() == Math.round(netBill)
                                && Preferences.getInstance(context).getBooked().isPaid() && bookedPlace != null) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    getDate(arrivedDate.getTime()), getDate(departureDate.getTime()), placeId);
                        } else {
                            Toast.makeText(context, Math.round(netBill) + "->netbill <-" + Preferences.getInstance(context).getBooked().getBill(), Toast.LENGTH_SHORT).show();
//
                            sslPayment(netBill);
                        }
                    }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });
    }

    private void sslPayment(double mNetBill) {
        Random random = new Random();

        int tnxId = random.nextInt(9999999);

        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization
                ("fiber61877740d2a85", "fiber61877740d2a85@ssl", mNetBill, SSLCCurrencyType.BDT, tnxId + Preferences.getInstance(context).getUser().getMobileNo(),
                        "CarParkingBill", SSLCSdkType.TESTBOX);
        IntegrateSSLCommerz
                .getInstance(context)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .buildApiCall(new SSLCTransactionResponseListener() {
                    @Override
                    public void transactionSuccess(SSLCTransactionInfoModel sslcTransactionInfoModel) {

                        bookedPlace = new BookedPlace();
                        bookedPlace.setPaid(true);
                        bookedPlace.setBill((float) mNetBill);
                        bookedPlace.setLat(lat);
                        bookedPlace.setLon(lon);
                        bookedPlace.setRoute(route);
                        bookedPlace.setAreaName(areaName);
                        bookedPlace.setParkingSlotCount(parkingSlotCount);
                        bookedPlace.setDepartedDate(departureDate.getTime());
                        bookedPlace.setArriveDate(arrivedDate.getTime());
                        bookedPlace.setPlaceId(placeId);
                        Preferences.getInstance(context).setBooked(bookedPlace);
                        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    getDate(arrivedDate.getTime()), getDate(departureDate.getTime()), placeId);
                        } else {
                            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                        }
                    }

                    @Override
                    public void transactionFail(String s) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void merchantValidationError(String s) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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
        binding.tvArrivedTime.setText(arrivedTime);
        binding.tvDepartureTime.setText(departureTime);
        binding.tvDifferenceTime.setText(String.format("%s hr", timeDifference));
        binding.tvParkingSlotName.setText(areaName);
    }

    private double setBill() {
        final double perMintBill = 0.6666666667;
        DecimalFormat df = new DecimalFormat("##.##",
                new DecimalFormatSymbols(Locale.US));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceUnit);
        binding.tvSubTotal.setText(new StringBuilder().append("BDT ").append(df.format(perMintBill * minutes)).toString());
        binding.tvTotal.setText(new StringBuilder().append("BDT ").append(df.format(perMintBill * minutes)).toString());
        binding.btnPay.setText(new StringBuilder().append("Pay BDT ").append(df.format(perMintBill * minutes)).toString());
        /*binding.btnPay.setText(new StringBuilder().append("Pay BDT ").append(MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#",
                new DecimalFormatSymbols(Locale.US)).format(df.format(perMintBill * minutes)))));*/
        return perMintBill * minutes;
    }

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String mPlaceId) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, mPlaceId, "2");
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
                            //set booked place info
                            bookedPlace.setBookedUid(response.body().getUid());
                            bookedPlace.setLat(lat);
                            bookedPlace.setLon(lon);
                            bookedPlace.setRoute(route);
                            bookedPlace.setAreaName(areaName);
                            bookedPlace.setParkingSlotCount(parkingSlotCount);
                            bookedPlace.setDepartedDate(departureDate.getTime());
                            bookedPlace.setArriveDate(arrivedDate.getTime());
                            bookedPlace.setPlaceId(mPlaceId);
                            bookedPlace.setReservation(response.body().getReservation());
                            bookedPlace.setIsBooked(true);

                            Preferences.getInstance(context).setBooked(bookedPlace);
                            startAlarm(convertLongToCalendar(Preferences.getInstance(context).getBooked().getArriveDate()));
                            if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                                binding.actionBarTitle.setText(context.getResources().getString(R.string.booking_payment));
                                listener.fragmentChange(new HomeFragment());
                            } else {
                                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
                            }
                        } else {
                            DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.parking_slot_not_available), context);
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
        intent.putExtra("Started", "Booked Time About to start for : \n" + Preferences.getInstance(context).getBooked().getAreaName());
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
