package www.fiberathome.com.parkingapp.view.booking.oldBooking;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

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

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.view.main.MainActivity;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;

import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldBookingFragment extends Fragment {

    private static int numbRows = 0;

    private ListView bookingList;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter arrayAdapter;

    //public static Integer numbRows = 55;


    public OldBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        User user = SharedPreManager.getInstance(getContext()).getUser();

        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }

        fetchBookings(user.getMobileNo());

        bookingList = (ListView) view.findViewById(R.id.booking_list);

        bookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = stringArrayList.get(i);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragmentWithBundle(s);
            }
        });

        return view;
    }

    public void bookingList(ArrayList<String> arrayList) {

        this.stringArrayList = arrayList;
        arrayList = new ArrayList<String>();

        if (getActivity() != null)
            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, arrayList);

        bookingList.setAdapter(arrayAdapter);

        if (numbRows > 0) {
            for (int i = 1; i <= numbRows; i++) {
//                arrayList.add("Booking " + i + ": ");
                arrayList.add("Booking " + i + " ");
            }
        } else arrayList.add("No Booking Found!");
    }

    public void fetchBookings(final String userId) {

        //Log.e("responce",""+userId);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_BOOKINGS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.e("response 1",""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Booking Object: ", jsonObject.toString());
                    if (!jsonObject.getBoolean("error")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("bookings");

                        stringArrayList = new ArrayList<String>();

                        numbRows = (Integer) jsonArray.length();

                        Log.e("Number of Bookings: ", String.valueOf(numbRows));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject Jasonobject = jsonArray.getJSONObject(i);
                            // Log.e("Booking Info: ", Jasonobject.toString());
                            String spotName = Jasonobject.getString("parking_area").toString();
                            String timeStart = Jasonobject.getString("time_start").toString();
                            String timeEnd = Jasonobject.getString("time_end").toString();
                            String currentBill = Jasonobject.getString("current_bill").toString();
                            String previousDue = Jasonobject.getString("penalty").toString();

                            // Log.e("Spot Info: ", spotName.toString());
                            stringArrayList.add(spotName.toString() + "|" + timeStart.toString() + "|" + timeEnd.toString() + "|" + currentBill.toString() + "|" + previousDue.toString());
                        }
                        bookingList(stringArrayList);
                    } else {
                        stringArrayList = new ArrayList<String>();
                        stringArrayList.add(" \n No Booking Found! ");
                        bookingList(stringArrayList);
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
                params.put("user_id", userId);
                return params;
            }
        };
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }
}
