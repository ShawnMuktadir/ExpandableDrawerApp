package www.fiberathome.com.parkingapp.ui.booking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.BottomSheetDialogGetHelpBinding;
import www.fiberathome.com.parkingapp.databinding.FragmentBookingBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.ui.booking.adapter.BookingAdapter;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression", "InflateParams"})
public class BookingFragment extends BaseFragment implements IOnBackPressListener {

    private BookingActivity context;

    private ArrayList<BookedList> bookedLists;

    private BookedResponse bookedResponse;
    private BookingAdapter bookingAdapter;

    FragmentBookingBinding binding;
    private FragmentChangeListener listener;

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
        listener = (FragmentChangeListener) context;
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
        if (ApplicationUtils.isGPSEnabled(context)) {
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
                        //Timber.e("bookedLists -> %s", new Gson().toJson(bookedLists));
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
            bookingAdapter = new BookingAdapter(context, bookedLists, new BookingAdapter.BookingAdapterClickListener() {
                @Override
                public void onBookingItemCancel(int position, String uid, String id) {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getResources().getString(R.string.do_u_want_to_cancel_booking),
                            context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Timber.e("Positive Button clicked");
                                    if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                                        cancelBooking(Preferences.getInstance(context).getUser().getMobileNo(), uid, id);
                                    }
                                    Preferences.getInstance(context).clearBooking();
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                }
                            }).show();
                }

                @Override
                public void onItemGetHelp() {
                    BottomSheetDialogGetHelpBinding getHelpBinding = BottomSheetDialogGetHelpBinding.inflate(getLayoutInflater());
                    BottomSheetDialog dialog = new BottomSheetDialog(context);
                    dialog.setContentView(getHelpBinding.getRoot());
                    dialog.show();

                    getHelpBinding.buttonCall.setOnClickListener(v -> {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            mPermissionResult.launch(Manifest.permission.CALL_PHONE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + context.getResources().getString(R.string.number)));
                            startActivity(intent);
                        }
                    });
                    getHelpBinding.buttonSms.setOnClickListener(v -> {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", context.getResources().getString(R.string.number), null));
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(smsIntent);
                    });
                }

                @Override
                public void onItemGetDirection(int position) {
                    context.onBackPressed();
                }

                @Override
                public void onItemRebookListener(int position, double lat, double lng, String parkingArea, String count, String placeId) {
                    boolean isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
                    if (isBooked) {
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.already_booked_msg), context);
                    } else {
                        count = "25";
                        if (placeId != null && !placeId.equalsIgnoreCase("") && placeId.equalsIgnoreCase("0") &&
                                count != null && !count.equalsIgnoreCase("") && count.equalsIgnoreCase("0")) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("m", false); //m for more
                            bundle.putString("areaPlacedId", placeId);
                            bundle.putString("areaName", parkingArea);
                            bundle.putString("parkingSlotCount", count);
                            bundle.putDouble("lat", lat);
                            bundle.putDouble("long", lng);
                            ScheduleFragment scheduleFragment = new ScheduleFragment();
                            scheduleFragment.setArguments(bundle);
                            listener.fragmentChange(scheduleFragment);
                        } else {
                            ToastUtils.getInstance().showToastMessage(context, "Currently Re-book is not available for this parking spot");
                        }
                    }
                }
            });
            binding.recyclerViewBooking.setAdapter(bookingAdapter);
        } else {
            bookingAdapter.updateList(bookedLists);
        }
    }

    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + context.getResources().getString(R.string.number)));
                    startActivity(intent);
                }
            });

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
                        ApplicationUtils.stopBookingTrackService(context);
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

    private void setNoData() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        //textViewNoData.setText(context.getResources().getString(R.string.no_record_found));
    }

    private void hideNoData() {
        binding.textViewNoData.setVisibility(View.GONE);
    }
}
