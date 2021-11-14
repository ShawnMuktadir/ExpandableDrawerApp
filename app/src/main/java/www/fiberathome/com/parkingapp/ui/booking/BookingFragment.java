package www.fiberathome.com.parkingapp.ui.booking;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentBookingBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.service.booking_service.BookingService;
import www.fiberathome.com.parkingapp.ui.booking.adapter.BookingAdapter;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookingFragment extends BaseFragment implements IOnBackPressListener {

    private BookingActivity context;

    private ArrayList<BookedList> bookedLists;

    private BookedResponse bookedResponse;
    private BookingAdapter bookingAdapter;

    FragmentBookingBinding binding;

    public BookingFragment() {
        // Required empty public constructor
    }

    public static BookingFragment newInstance() {
        return new BookingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (BookingActivity) getActivity();
        setListeners();

        String mobileNo = Preferences.getInstance(context).getUser().getMobileNo();
        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchBookedParkingPlace(mobileNo, false);
        } else {
            DialogUtils.getInstance().alertDialog(context,
                    context,
                    context.getResources().getString(R.string.connect_to_internet), context.getResources().getString(R.string.retry), context.getResources().getString(R.string.close_app),
                    new DialogUtils.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                fetchBookedParkingPlace(mobileNo, false);
                            } else {
                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                            }
                        }

                        @Override
                        public void onNegativeClick() {
                            if (context != null) {
                                context.finish();
                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                            }
                        }
                    }).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
        }
        return false;
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

    private void fetchBookedParkingPlace(String mobileNo, boolean refresh) {
        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BookedResponse> bookedResponseCall = service.getBookedPlace(mobileNo);

        // Gathering results.
        bookedResponseCall.enqueue(new Callback<BookedResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookedResponse> call, @NonNull Response<BookedResponse> response) {

                Timber.e("response -> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (response.body() != null && !response.body().getError()) {
                    if (response.isSuccessful()) {
                        bookedResponse = response.body();
                        bookedLists = bookedResponse.getBookedLists();
                        Timber.e("bookedLists -> %s", new Gson().toJson(bookedLists));
                        if (bookedLists != null && !bookedLists.isEmpty()) {
                            setFragmentControls(bookedLists, refresh);
                            hideNoData();
                        } else {
                            setNoData();
                        }
                    } else {
                        Timber.e("response -> %s", new Gson().toJson(response.body()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookedResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private void setFragmentControls(ArrayList<BookedList> bookedLists, boolean refresh) {
        if (!refresh && isAdded()) {
            binding.recyclerViewBooking.setHasFixedSize(true);
            binding.recyclerViewBooking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            bookingAdapter = new BookingAdapter(context, bookedLists, (position, uid, id) -> DialogUtils.getInstance().alertDialog(context,
                    (Activity) context,
                    context.getResources().getString(R.string.dp_u_want_to_cancel_booking),
                    context.getResources().getString(R.string.ok), context.getResources().getString(R.string.cancel),
                    new DialogUtils.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Timber.e("Positive Button clicked");
                            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                                cancelBooking(Preferences.getInstance(context).getUser().getMobileNo(), uid, id);
                            }
                            Preferences.getInstance(context).clearBooking();
                        }

                        @Override
                        public void onNegativeClick() {
                            Timber.e("Negative Button Clicked");
                        }
                    }).show());
            binding.recyclerViewBooking.setAdapter(bookingAdapter);
        } else {
            bookingAdapter.updateList(bookedLists);
        }
    }

    private void cancelBooking(String mobileNo, String uid, String id) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationCancelResponse> call = request.cancelReservation(Preferences.getInstance(context).getUser().getMobileNo(), uid, id);
        call.enqueue(new Callback<ReservationCancelResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationCancelResponse> call, @NonNull Response<ReservationCancelResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                        Preferences.getInstance(context).clearBooking();
                        Preferences.getInstance(context).isBookingCancelled = true;
                        bookedLists.clear();
                        fetchBookedParkingPlace(mobileNo, true);
                        stopBookingTrackService();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationCancelResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }

    private void setListeners() {
        binding.imageViewCross.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
                ((HomeActivity) getActivity()).toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                ((HomeActivity) getActivity()).navigationView.getMenu().getItem(2).setChecked(false);
            }
            if (getFragmentManager() != null) {
                FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void stopBookingTrackService() {
        if (isLocationTrackingServiceRunning()) {
            Intent intent = new Intent(context, BookingService.class);
            intent.setAction(Constants.STOP_BOOKING_TRACKING);
            context.startService(intent);
        }
    }

    private boolean isLocationTrackingServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                String a = Service.class.getName();
                serviceInfo.service.getClassName();
                if (serviceInfo.foreground) {
                    return true;
                }

            }
            return false;
        }
        return false;
    }

    private void setNoData() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        //textViewNoData.setText(context.getResources().getString(R.string.no_record_found));
    }

    private void hideNoData() {
        binding.textViewNoData.setVisibility(View.GONE);
    }
}
