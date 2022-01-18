package www.fiberathome.com.parkingapp.ui.reservation.payment;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.BookedPlace;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.ReservationResponse;
import www.fiberathome.com.parkingapp.databinding.BottomSheetDialogScratchCardBinding;
import www.fiberathome.com.parkingapp.databinding.FragmentPaymentBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationActivity;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationParkFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationViewModel;
import www.fiberathome.com.parkingapp.ui.reservation.schedule.ScheduleActivity;
import www.fiberathome.com.parkingapp.ui.reservation.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

public class PaymentFragment extends BaseFragment implements IOnBackPressListener {

    static Date arrivedDate, departureDate;
    static String arrivedTime, departureTime, timeDifference, placeId, areaName, areaNameBangla, parkingSlotCount;
    static long differenceUnit;
    static double lat, lon;
    static boolean isBookNowChecked;
    private static boolean isInArea;

    private BaseActivity context;
    private ReservationViewModel reservationViewModel;
    private FragmentChangeListener listener;

    FragmentPaymentBinding binding;
    private static String netBill;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(Date mArrivedDate, Date mDepartedDate, String mArrivedTime,
                                              String mDepartureTime, String mTimeDifference, long mDifferenceUnit,
                                              String mMarkerUid, double mLat, double mLon, String mAreaName, String mAreaNameBangla,
                                              String mParkingSlotCount, boolean mIsBookNowChecked, boolean mIsInArea, String bill) {
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
        areaNameBangla = mAreaNameBangla;
        parkingSlotCount = mParkingSlotCount;
        isBookNowChecked = mIsBookNowChecked;
        isInArea = mIsInArea;
        netBill = bill;
        return new PaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HomeActivity) {
            context = (HomeActivity) getActivity();
        } else if (getActivity() instanceof ReservationActivity) {
            context = (ReservationActivity) getActivity();
        } else if (getActivity() instanceof ScheduleActivity) {
            context = (ScheduleActivity) getActivity();
        }
        listener = (FragmentChangeListener) context;
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
            reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
            setListeners();
            setData();
            //netBill = setBill();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        if (Preferences.getInstance(context).getBooked().getIsBooked()) {
            if (getActivity() instanceof HomeActivity) {
                listener.fragmentChange(HomeFragment.newInstance());
            } else if (getActivity() instanceof ReservationActivity) {
                startActivityWithFinishAffinity(context, HomeActivity.class);
            } else if (getActivity() instanceof ScheduleActivity) {
                startActivityWithFinishAffinity(context, HomeActivity.class);
            }
        }
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
                startActivityWithFinish(context, ScheduleActivity.class);
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setListeners() {
        binding.ivBackArrow.setOnClickListener(v -> {
            ScheduleFragment scheduleFragment = ScheduleFragment.newInstance(placeId, areaName,
                    parkingSlotCount, lat, lon, isInArea);
            listener.fragmentChange(scheduleFragment);
        });

        binding.tvPromo.setOnClickListener(v -> ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.coming_soon)));

        binding.tvTermCondition.setOnClickListener(v -> ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.coming_soon)));

        binding.btnPay.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                long diff = arrivedDate.getTime() - departureDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                Timber.e("days -> %s", days);
                Timber.e("hours -> %s", hours);
                Timber.e("minutes -> %s", minutes);
                if (minutes > 120) {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.booking_time_rules));
                } else {
                    if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                        if (!Preferences.getInstance(context).getBooked().getIsBooked()
                                && Preferences.getInstance(context).getBooked().getBill() == Math.round(Float.parseFloat(netBill))
                                && Preferences.getInstance(context).getBooked().isPaid()) {
                            storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                    ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getArriveDate()),
                                    ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getDepartedDate()),
                                    Preferences.getInstance(context).getBooked().getPlaceId());
                        } else {
                            try {
                                sslPayment(Double.parseDouble(netBill));
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

        binding.btnScratchCard.setOnClickListener(v1 -> {
            try {
                BottomSheetDialogScratchCardBinding dialogScratchCardBinding = BottomSheetDialogScratchCardBinding.inflate(getLayoutInflater());
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(dialogScratchCardBinding.getRoot());
                try {
                    dialog.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                } catch (NullPointerException e) {
                    e.getCause();
                }
                dialog.show();

                dialogScratchCardBinding.buttonSubmit.setOnClickListener(v -> {
                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                        long diff = arrivedDate.getTime() - departureDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;
                        Timber.e("days -> %s", days);
                        Timber.e("hours -> %s", hours);
                        Timber.e("minutes -> %s", minutes);
                        if (minutes > 120) {
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.booking_time_rules));
                        } else {
                            if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                                BookedPlace mBookedPlace = new BookedPlace();
                                mBookedPlace.setBill((float) setBill());
                                mBookedPlace.setLat(lat);
                                mBookedPlace.setLon(lon);
                                mBookedPlace.setAreaName(areaName);
                                mBookedPlace.setParkingSlotCount(parkingSlotCount);
                                mBookedPlace.setDepartedDate(departureDate.getTime());
                                mBookedPlace.setArriveDate(arrivedDate.getTime());
                                mBookedPlace.setPlaceId(placeId);
                                mBookedPlace.setPaid(true);
                                Preferences.getInstance(context).setBooked(mBookedPlace);
                                storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                                        ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getArriveDate()),
                                        ApplicationUtils.getDate(Preferences.getInstance(context).getBooked().getDepartedDate()),
                                        Preferences.getInstance(context).getBooked().getPlaceId());
                                dialog.dismiss();
                            }
                        }
                    }
                });

                dialogScratchCardBinding.buttonCancel.setOnClickListener(v -> dialog.dismiss());
            } catch (Exception e) {
                // generic exception handling
                e.printStackTrace();
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
        if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            mBookedPlace.setAreaName(areaName);
        } else {
            mBookedPlace.setAreaName(areaNameBangla);
        }
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
                        Timber.e("transactionFail -> %s", s);
                    }

                    @Override
                    public void merchantValidationError(String s) {
                        Timber.e("merchantValidationError -> %s", s);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvArrivedTime.setText(TextUtils.getInstance().convertTextEnToBn(arrivedTime));
        } else {
            binding.tvArrivedTime.setText(arrivedTime);
        }

        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvDepartureTime.setText(TextUtils.getInstance().convertTextEnToBn(departureTime));
        } else {
            binding.tvDepartureTime.setText(departureTime);
        }

        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvDifferenceTime.setText(String.format("%s " + context.getResources().getString(R.string.hr), TextUtils.getInstance().convertTextEnToBn(timeDifference)));
        } else {
            binding.tvDifferenceTime.setText(String.format("%s " + context.getResources().getString(R.string.hr), timeDifference));
        }

        if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvParkingSlotName.setText(areaName);
        } else {
            if (areaNameBangla != null) {
                binding.tvParkingSlotName.setText(areaNameBangla);
            } else {
                binding.tvParkingSlotName.setText(areaName);
            }
        }

        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvDiscount.setText(context.getResources().getString(R.string.bdt) + "  " + TextUtils.getInstance().convertTextEnToBn(context.getResources().getString(R.string.digit_00_00)));
        } else {
            binding.tvDiscount.setText(context.getResources().getString(R.string.bdt) + "  " + context.getResources().getString(R.string.digit_00_00));
        }

        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            binding.tvEachHourBill.setText(context.getResources().getString(R.string.bdt) + "  " + TextUtils.getInstance().convertTextEnToBn(context.getResources().getString(R.string.bdt_hour_charge)));
        } else {
            binding.tvEachHourBill.setText(context.getResources().getString(R.string.bdt) + "  " + context.getResources().getString(R.string.bdt_hour_charge));
        }

    }

    @SuppressLint("SetTextI18n")
    private double setBill() {
        binding.tvSubTotal.setText(context.getResources().getString(R.string.bdt) + "  " + TextUtils.getInstance().convertTextEnToBn(netBill));

        binding.tvTotal.setText(context.getResources().getString(R.string.bdt) + "  " + TextUtils.getInstance().convertTextEnToBn(netBill));

        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            binding.btnPay.setText(String.format("%s %s  %s", context.getResources().getString(R.string.money_sign), TextUtils.getInstance().convertTextEnToBn(netBill), context.getResources().getString(R.string.pay_bdt)));
        } else {
            binding.btnPay.setText(String.format("%s  %s", context.getResources().getString(R.string.pay_bdt), TextUtils.getInstance().convertTextEnToBn(netBill)));
        }
        return Double.parseDouble(netBill);
    }

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String mPlaceId) {
        showLoading(context);
        reservationViewModel.storeReservationInit(mobileNo, arrivalTime, departureTime, mPlaceId, "2", Preferences.getInstance(context).getSelectedVehicleNo());
        reservationViewModel.getStoreReservationMutableData().observe(requireActivity(), (@NonNull ReservationResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                if (response.getUid() != null) {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.reservation_successful));
                    //set booked place info
                    BookedPlace mBookedPlace;
                    mBookedPlace = Preferences.getInstance(context).getBooked();
                    mBookedPlace.setBookedUid(response.getUid());
                    mBookedPlace.setReservation(response.getReservation());
                    mBookedPlace.setIsBooked(true);
                    mBookedPlace.setPsId(response.getPsId());
                    Preferences.getInstance(context).setBooked(mBookedPlace);
                    if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                        binding.actionBarTitle.setText(context.getResources().getString(R.string.booking_payment));
                        if (isBookNowChecked) {
                            setBookingPark(Preferences.getInstance(context).getUser().getMobileNo(), mBookedPlace.getBookedUid());
                        } else {
                            ApplicationUtils.startAlarm(context, ApplicationUtils.convertLongToCalendar(Preferences.getInstance(context).getBooked().getArriveDate())
                                    , ApplicationUtils.convertLongToCalendar(Preferences.getInstance(context).getBooked().getDepartedDate()));
                            if (getActivity() instanceof ReservationActivity) {
                                startActivityWithFinishAffinity(context, HomeActivity.class);
                            } else if (getActivity() instanceof HomeActivity) {
                                listener.fragmentChange(HomeFragment.newInstance());
                            } else if (getActivity() instanceof ScheduleActivity) {
                                startActivityWithFinishAffinity(context, HomeActivity.class);
                            }
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
                    }
                } else {
                    DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.parking_slot_not_available), context);
                }
            }
        });
    }

    private void setBookingPark(String mobileNo, String uid) {
        showLoading(context);

        reservationViewModel.initReservation(mobileNo, uid);
        reservationViewModel.setParkedCar().observe(context, reservationCancelResponse -> {
            hideLoading();
            if (!reservationCancelResponse.getError()) {
                BookedPlace mBookedPlace = Preferences.getInstance(context).getBooked();
                mBookedPlace.setCarParked(true);
                Preferences.getInstance(context).setBooked(mBookedPlace);
                ApplicationUtils.stopBookingTrackService(context);
                getBookingParkStatus(Preferences.getInstance(context).getUser().getMobileNo());
            }
        });
    }

    private void getBookingParkStatus(String mobileNo) {
        showLoading(context);

        reservationViewModel.initBookingParkStatus(mobileNo);
        reservationViewModel.getBookingParkStatus().observe(context, bookingParkStatusResponse -> {
            hideLoading();
            if (bookingParkStatusResponse.getSensors() != null) {
                BookingParkStatusResponse.Sensors sensors = bookingParkStatusResponse.getSensors();
                listener.fragmentChange(ReservationParkFragment.newInstance(sensors));
            }
        });
    }
}
