package www.fiberathome.com.parkingapp.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

public class PaymentFragment extends BaseFragment implements IOnBackPressListener {

    static Date arrivedDate, departureDate;
    static String arrivedTime, departureTime, timeDifference, placeId, areaName, parkingSlotCount;
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
                                              String mMarkerUid, double mLat, double mLon, String mAreaName,
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
        areaName = mAreaName;
        parkingSlotCount = mParkingSlotCount;
        return new PaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (HomeActivity) getActivity();
        listener = context;
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
            setListeners();
            setData();
            netBill = setBill();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        if (Preferences.getInstance(context).getBooked().getIsBooked()) {
            listener.fragmentChange(HomeFragment.newInstance());
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                        if (!Preferences.getInstance(context).getBooked().getIsBooked()
                                && Preferences.getInstance(context).getBooked().getBill() == Math.round(netBill)
                                && Preferences.getInstance(context).getBooked().isPaid()) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getArriveDate()),
                                    ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getArriveDate()),
                                    Preferences.getInstance(context).getBooked().getPlaceId());
                        } else {
                            try {
                                sslPayment(netBill);
                            } catch (Exception e) {
                                Timber.e(e.getCause());
                            }
                        }
                    }
                }
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });
    }

    private void sslPayment(double mNetBill) {
        Random random = new Random();

        int tnxId = random.nextInt(9999999);

        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization
                ("fiber61877740d2a85", "fiber61877740d2a85@ssl", mNetBill, SSLCCurrencyType.BDT, tnxId + Preferences.getInstance(context).getUser().getMobileNo(),
                        "CarParkingBill", SSLCSdkType.TESTBOX);
        final SSLCCustomerInfoInitializer customerInfoInitializer = new SSLCCustomerInfoInitializer(Preferences.getInstance(context).getUser().getFullName(), "customer email",
                "address", "dhaka", "1214", "Bangladesh", Preferences.getInstance(context).getUser().getMobileNo());

        BookedPlace mBookedPlace = new BookedPlace();
        mBookedPlace.setBill((float) mNetBill);
        mBookedPlace.setLat(lat);
        mBookedPlace.setLon(lon);
        mBookedPlace.setAreaName(areaName);
        mBookedPlace.setParkingSlotCount(parkingSlotCount);
        mBookedPlace.setDepartedDate(departureDate.getTime());
        mBookedPlace.setArriveDate(arrivedDate.getTime());
        mBookedPlace.setPlaceId(placeId);
        Preferences.getInstance(context).setBooked(mBookedPlace);

        IntegrateSSLCommerz
                .getInstance(context)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .addCustomerInfoInitializer(customerInfoInitializer)
                .buildApiCall(new SSLCTransactionResponseListener() {
                    @Override
                    public void transactionSuccess(SSLCTransactionInfoModel sslcTransactionInfoModel) {
                        Timber.e("transactionSuccess -> %s", new Gson().toJson(sslcTransactionInfoModel));
                        BookedPlace mBookedPlace;
                        mBookedPlace = Preferences.getInstance(context).getBooked();
                        mBookedPlace.setPaid(true);
                        Preferences.getInstance(context).setBooked(mBookedPlace);
                        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    ApplicationUtils.getDate(mBookedPlace.getArriveDate()), ApplicationUtils.getDate(mBookedPlace.getDepartedDate()), mBookedPlace.getPlaceId());
                        } else {
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
                        }
                    }

                    @Override
                    public void transactionFail(String s) {
//                        ToastUtils.getInstance().showToast(context, s);
                        Timber.e("transactionFail -> %s", s);
                    }

                    @Override
                    public void merchantValidationError(String s) {
                        Timber.e("merchantValidationError -> %s", s);
                    }
                });
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
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.reservation_successful));
                            //set booked place info
                            BookedPlace mBookedPlace;
                            mBookedPlace = Preferences.getInstance(context).getBooked();
                            mBookedPlace.setBookedUid(response.body().getUid());
                            mBookedPlace.setReservation(response.body().getReservation());
                            mBookedPlace.setIsBooked(true);
                            mBookedPlace.setPsId(response.body().getPsId());
                            Preferences.getInstance(context).setBooked(mBookedPlace);
                            ApplicationUtils.startAlarm(context, ApplicationUtils.convertLongToCalendar(Preferences.getInstance(context).getBooked().getArriveDate())
                                    , ApplicationUtils.convertLongToCalendar(Preferences.getInstance(context).getBooked().getDepartedDate()));
                            if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                                binding.actionBarTitle.setText(context.getResources().getString(R.string.booking_payment));
                                listener.fragmentChange(HomeFragment.newInstance());
                            } else {
                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
                            }
                        } else {
                            DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.parking_slot_not_available), context);
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.reservation_failed));
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
}
