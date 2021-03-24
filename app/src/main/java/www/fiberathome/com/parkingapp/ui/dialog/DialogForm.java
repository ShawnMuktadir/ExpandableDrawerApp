package www.fiberathome.com.parkingapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.Reservation;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;

import static com.android.volley.VolleyLog.TAG;
import static www.fiberathome.com.parkingapp.utils.NotificationClass.CHANNEL_HIGH_PRIORITY_ID;

public class DialogForm extends AppCompatDialogFragment {

    //    private EditText usernameEt;
//    private EditText passwordEt;
//    private EditText mobileET;
    private DialogFormListener listener;
    private TimePicker parkingReqStartTime;
    private TimePicker parkingReqEndTime;
    private DatePicker parkingReqStartDate;
    private DatePicker parkingReqEndDate;
    private Button reserveFinal;
    private Button reservePayment;
    private Button reserveCancel;

    private TextView reserveBillAmountTV;
    private TextView reserveIdTV;
    private ImageView paymentGateIV;

    private Calendar calendar;
    private String format = "";

    public String selectedSpt;

    private NotificationManagerCompat notificationManager;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        super.onCreate(savedInstanceState);

        notificationManager = NotificationManagerCompat.from(getContext());

        final Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_HIGH_PRIORITY_ID)
                .setSmallIcon(R.drawable.ic_event_available)
                .setContentTitle("New Reservation!")
                .setContentText("A new reservation is complete.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        if (getArguments() != null) {
            selectedSpt = getArguments().getString("selectedSpt", "");

        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_form, null);
//        View view = inflater.inflate(R.layout.booking_dialog_form, null);
        builder.setView(view)
                .setTitle("Parking Spot Reservation");

//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setPositiveButton("Reserve", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        String username = usernameEt.getText().toString();
////                        String password = passwordEt.getText().toString();
////                        String mobile = mobileET.getText().toString();
//
//
//
//
//                        //listener.applyTexts(username, password, mobile);
//                    }
//                });

//        usernameEt = view.findViewById(R.id.username_et);
//        passwordEt = view.findViewById(R.id.password_et);
//        mobileET = view.findViewById(R.id.mobile_et);

        reserveFinal = view.findViewById(R.id.reserve_final);
        reservePayment = view.findViewById(R.id.reserve_payment);
        reservePayment.setVisibility(View.INVISIBLE);
        reserveCancel = view.findViewById(R.id.reserve_cancel);

        reserveBillAmountTV = view.findViewById(R.id.parking_req_bill_amount);
        reserveBillAmountTV.setVisibility(View.INVISIBLE);

        reserveIdTV = view.findViewById(R.id.parking_req_res_id);
        reserveBillAmountTV.setVisibility(View.INVISIBLE);
        paymentGateIV = view.findViewById(R.id.payment_gate_image);
        paymentGateIV.setVisibility(View.GONE);

        parkingReqStartDate = (DatePicker) view.findViewById(R.id.parking_req_start_date);

        parkingReqStartTime = (TimePicker) view.findViewById(R.id.parking_req_start_time);

        parkingReqEndDate = (DatePicker) view.findViewById(R.id.parking_req_end_date);

        parkingReqEndTime = (TimePicker) view.findViewById(R.id.parking_req_end_time);

        reserveFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int hour;
                int min;

                String startTimestamp = parkingReqStartDate.getYear() + "-" + (parkingReqStartDate.getMonth() + 1) + "-" + parkingReqStartDate.getDayOfMonth() + " " + parkingReqStartTime.getCurrentHour() + ":" + parkingReqStartTime.getCurrentMinute();
                String endTimestamp = parkingReqEndDate.getYear() + "-" + (parkingReqEndDate.getMonth() + 1) + "-" + parkingReqEndDate.getDayOfMonth() + " " + parkingReqEndTime.getCurrentHour() + ":" + parkingReqEndTime.getCurrentMinute();

                //String startTime =  showTime(parkingReqStartTime.getCurrentHour(),  parkingReqStartTime.getCurrentMinute());

                Timber.e(startTimestamp.toString());

                //String endTime =  showTime(parkingReqEndTime.getCurrentHour(), parkingReqEndTime.getCurrentMinute());

                Timber.e(endTimestamp.toString());

                User user = Preferences.getInstance(requireContext()).getUser();

                String mobileNo = user.getMobileNo();

                Timber.e(mobileNo.toString());
                Timber.e(selectedSpt.toString());

                storeReservation(mobileNo.toString(), startTimestamp.toString(), endTimestamp.toString(), selectedSpt.toString());

//                Toast.makeText(getContext(),"Your Reservation is Completed! ",Toast.LENGTH_SHORT).show();
//                Toast.makeText(getContext(),"Start Time: "+startTimestamp.toString()+"End Time: "+endTimestamp.toString(),Toast.LENGTH_SHORT).show();
//                dismiss();
            }
        });

        reserveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        reservePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Your Reservation is Completed! ", Toast.LENGTH_SHORT).show();
                Timber.e("Reservation: Your Reservation is Completed!");
                notificationManager.notify(1, notification);
                dismiss();
            }
        });

        return builder.create();
    }


    private void storeReservation(final String mobileNo, final String timeStart, final String timeEnd, final String spotId) {
        //Toast.makeText(getContext(),"Reservation Called!",Toast.LENGTH_SHORT);

        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConfig.URL_STORE_RESERVATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Timber.e(jsonObject.toString());

                    if (!jsonObject.getBoolean("error")) {

                        Timber.e(jsonObject.getString("reservation"));
                        Timber.e(jsonObject.getString("bill"));

                        reserveFinal.setVisibility(getView().GONE);
                        parkingReqStartTime.setVisibility(getView().GONE);
                        parkingReqEndTime.setVisibility(getView().GONE);
                        parkingReqStartDate.setVisibility(getView().GONE);
                        parkingReqEndDate.setVisibility(getView().GONE);

                        reserveBillAmountTV.setText("Net Payable Apount: " + jsonObject.getString("bill"));
                        reserveIdTV.setText("Your Reservation Id: NMC00" + jsonObject.getString("reservation"));
                        reservePayment.setVisibility(getView().VISIBLE);
                        reserveBillAmountTV.setVisibility(getView().VISIBLE);
                        paymentGateIV.setVisibility(getView().VISIBLE);
                        reserveIdTV.setVisibility(getView().VISIBLE);

                        // creating a new user object
                        Reservation reservation = new Reservation();

                        // getting the reservation from the response
                        //JSONObject reservationJson = jsonObject.getJSONObject("reservation");

                        JSONObject reservationJson = new JSONObject(response);

                        reservation.setId(reservationJson.getInt("reservation"));

                        //reservation.setMobileNo(reservationJson.getString("mobile_no"));
                        //reservation.setTimeStart(reservationJson.getString("time_start"));
                        //reservation.setTimeEnd(reservationJson.getString("time_end"));
                        //reservation.setSpotId(reservationJson.getString("spot_id"));

                    } else {
                        Toast.makeText(getContext(), "Reservation Failed! Please Try Again. ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley Error", error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobileNo);
                params.put("time_start", timeStart);
                params.put("time_end", timeEnd);
                params.put("spot_id", spotId);
                return params;
            }
        };

        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }


//    public String showTime(int hour, int min) {
//        if (hour == 0) {
//            hour += 12;
//            format = "AM";
//        } else if (hour == 12) {
//            format = "PM";
//        } else if (hour > 12) {
//            hour -= 12;
//            format = "PM";
//        } else {
//            format = "AM";
//        }
//
//        //time.setText(new StringBuilder().append(hour).append(" : ").append(min)
//         //       .append(" ").append(format));
//
//        //Log.e("Start Time:", "Time: "+hour+" : "+min+" : "+format);
//
//        String calculatedTime = hour+":"+min+" "+format;
//
//        return calculatedTime;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogFormListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogFormListener");

        }
    }

    public interface DialogFormListener {
        void applyTexts(String username, String password, String mobile);
    }
}
