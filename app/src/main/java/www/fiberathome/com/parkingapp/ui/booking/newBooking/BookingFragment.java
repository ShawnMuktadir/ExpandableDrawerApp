package www.fiberathome.com.parkingapp.ui.booking.newBooking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingArea;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

@SuppressLint("NonConstantResourceId")
public class BookingFragment extends BaseFragment implements IOnBackPressListener {

    @BindView(R.id.recyclerViewBooking)
    RecyclerView recyclerViewBooking;

    @BindView(R.id.textViewNoData)
    TextView textViewNoData;

    @BindView(R.id.imageViewCross)
    ImageView imageViewCross;

    private Unbinder unbinder;

    private BookingActivity context;

    private BookingAdapter bookingAdapter;

    private ArrayList<BookingArea> bookingAreas = new ArrayList<>();

    private ProgressDialog progressDialog;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);
        context = (BookingActivity) getActivity();

        setListeners();

        String mobileNo = Preferences.getInstance(context).getUser().getMobileNo();
        if (ApplicationUtils.checkInternet(context)) {
            //fetchParkingBookingSpot(mobileNo);
            fetchBookedParkingPlace(mobileNo);
        } else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (ApplicationUtils.checkInternet(context)) {
                    //fetchParkingBookingSpot(mobileNo);
                    fetchBookedParkingPlace(mobileNo);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (context != null) {
                    context.finish();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }
            });
        }
    }

    @Override
    public void onStart() {
        Timber.e("onStart called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Timber.e("onResume called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
        //dismissProgressDialog();
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
        //dismissProgressDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
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

    /*private void fetchParkingBookingSpot(String mobileNo) {

        //progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");
        if (!context.isFinishing())
            showLoading(context);

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_BOOKINGS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //progressDialog.dismiss();
                hideLoading();
                Timber.e("booking list response -> %s", new Gson().toJson(response));
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Timber.e("booking list jsonObject -> %s", new Gson().toJson(jsonObject));
                    //Log.e("Booking Object: ", jsonObject.toString());
                    if (!jsonObject.getBoolean("error")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("bookings");

    stringArrayList = new ArrayList<String>();

                        numbRows = (Integer) jsonArray.length();

                        Log.e("Number of Bookings: ", String.valueOf(numbRows));

                        BookingArea bookingArea = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            // Log.e("Booking Info: ", Jasonobject.toString());
                            String spotName = object.getString("parking_area").toString();
                            String timeStart = object.getString("time_start").toString();
                            String timeEnd = object.getString("time_end").toString();
                            String currentBill = object.getString("current_bill").toString();
                            String previousDue = object.getString("penalty").toString();

                            bookingArea = new BookingArea(spotName, timeStart, timeEnd);
                            bookingAreas.add(bookingArea);

                            //Log.e("Spot Info: ", spotName.toString());
                            //stringArrayList.add(spotName.toString() + "|" + timeStart.toString() + "|" + timeEnd.toString() + "|" + currentBill.toString() + "|" + previousDue.toString());
                        }
                        setFragmentControls(bookingAreas);
                    } else {
                        stringArrayList = new ArrayList<String>();
                        stringArrayList.add(" \n No Booking Found! ");
                        bookingList(stringArrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            //Log.e("Volley Error", error.getMessage());
            //ApplicationUtils.showMessageDialog(error.getMessage(), getActivity());
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", mobileNo);
                Timber.e("booking user_id -> %s", params);
                return params;
            }
        };
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }*/

    private ArrayList<BookedList> bookedLists;
    private BookedResponse bookedResponse;

    String address = "";
    String timeStart = "";
    String timeEnd = "";
    String currentBill = "";
    String penalty = "";

    private void fetchBookedParkingPlace(String mobileNo) {

        Timber.e("fetchBookedParkingPlace mobileNo -> %s,", mobileNo);

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
                            setFragmentControls(bookedLists);
                        } else {
                            Toast.makeText(context, "bookedLists empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Timber.e("response -> %s", new Gson().toJson(response.body()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookedResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    /*private void setFragmentControls(ArrayList<BookingArea> bookingAreas) {
        this.bookingAreas = bookingAreas;
        recyclerViewBooking.setHasFixedSize(true);
        recyclerViewBooking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        bookingAdapter = new BookingAdapter(context, this, bookingAreas);
        recyclerViewBooking.setAdapter(bookingAdapter);
    }*/

    private void setFragmentControls(ArrayList<BookedList> bookedLists) {
        //this.bookedLists = bookedLists;
        recyclerViewBooking.setHasFixedSize(true);
        recyclerViewBooking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        bookingAdapter = new BookingAdapter(context);
        bookingAdapter.setDataList(bookedLists);
        recyclerViewBooking.setAdapter(bookingAdapter);
    }

    private void setListeners() {
        imageViewCross.setOnClickListener(v -> {
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

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
//        textViewNoData.setText(context.getString(R.string.no_record_found));
    }

    private void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }
}
