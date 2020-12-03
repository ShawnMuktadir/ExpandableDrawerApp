package www.fiberathome.com.parkingapp.ui.booking.newBooking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.response.booking.BookingArea;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;
import static com.android.volley.VolleyLog.TAG;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

public class BookingFragment extends Fragment implements IOnBackPressListener {

    @BindView(R.id.recyclerViewBooking)
    RecyclerView recyclerViewBooking;
    @BindView(R.id.textViewNoData)
    TextView textViewNoData;
    @BindView(R.id.imageViewCross)
    ImageView imageViewCross;

    private Unbinder unbinder;
    private BookingAdapter bookingAdapter;
    private ArrayList<BookingArea> bookingAreas = new ArrayList<>();

    private Context context;
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
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();

        setListeners();

        String mobileNo = SharedPreManager.getInstance(context).getUser().getMobileNo();
        if (ApplicationUtils.checkInternet(context)){
            fetchParkingBookingSpot(mobileNo);
        }else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (ApplicationUtils.checkInternet(context)){
                    fetchParkingBookingSpot(mobileNo);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }
            });
        }
        return view;
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
        dismissProgressDialog();
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
        dismissProgressDialog();
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
//            HomeFragment nextFrag = new HomeFragment();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
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

    private void fetchParkingBookingSpot(String mobileNo) {
        HttpsTrustManager.allowAllSSL();
        progressDialog = ApplicationUtils.progressDialog(getActivity(),
                "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_BOOKINGS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Timber.e("booking list response -> %s",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Booking Object: ", jsonObject.toString());
                    if (!jsonObject.getBoolean("error")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("bookings");

//                        stringArrayList = new ArrayList<String>();
//
//                        numbRows = (Integer) jsonArray.length();
//
//                        Log.e("Number of Bookings: ", String.valueOf(numbRows));

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

                            // Log.e("Spot Info: ", spotName.toString());
//                            stringArrayList.add(spotName.toString() + "|" + timeStart.toString() + "|" + timeEnd.toString() + "|" + currentBill.toString() + "|" + previousDue.toString());
                        }
                        setFragmentControls(bookingAreas);
                    } else {
//                        stringArrayList = new ArrayList<String>();
//                        stringArrayList.add(" \n No Booking Found! ");
//                        bookingList(stringArrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley Error", error.getMessage());
                ApplicationUtils.showMessageDialog(error.getMessage(), getActivity());
            }
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
    }

    private void setFragmentControls(ArrayList<BookingArea> bookingAreas) {
        this.bookingAreas = bookingAreas;
        recyclerViewBooking.setHasFixedSize(true);
        recyclerViewBooking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        bookingAdapter = new BookingAdapter(context, this, bookingAreas);
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
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, HomeFragment.newInstance());
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
