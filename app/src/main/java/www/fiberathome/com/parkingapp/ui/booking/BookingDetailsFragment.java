package www.fiberathome.com.parkingapp.ui.booking;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import www.fiberathome.com.parkingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingDetailsFragment extends Fragment {

    private Button bookingEdit;
    private Button bookingDelete;

    TextView bookingSpot;
    TextView bookingStart;
    TextView bookingEnd;
    TextView bookingBill;
    TextView bookingBillDue;

    public BookingDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_booking_details, container, false);

        bookingEdit = v.findViewById(R.id.booking_edit_Button);
        bookingDelete = v.findViewById(R.id.booking_delete_Button);

        bookingSpot = v.findViewById(R.id.TV_booking_spot);
        bookingStart = v.findViewById(R.id.TV_booking_start);
        bookingEnd = v.findViewById(R.id.TV_booking_end);
        bookingBill = v.findViewById(R.id.TV_booking_bill);
        bookingBillDue = v.findViewById(R.id.TV_booking_bill_due);

        Bundle b = getArguments();
        if(b!=null){
            String s = b.getString("s");


            StringTokenizer tokens = new StringTokenizer(s, "|");

            String spotName = tokens.nextToken();
            String timeStart = tokens.nextToken();
            String timeEnd = tokens.nextToken();
            String currentBill = tokens.nextToken()+" Tk";
            String previousDue = tokens.nextToken()+" Tk";


            bookingSpot.setText(spotName);
            bookingStart.setText(timeStart);
            bookingEnd.setText(timeEnd);
            bookingBill.setText(currentBill);
            bookingBillDue.setText(previousDue);

            Log.e("Current Timestamp: ", getCurrentTimeStamp());
            Log.e("End Timestamp: ", timeEnd.toString());


            if(Timestamp.valueOf(timeEnd).before(Timestamp.valueOf(getCurrentTimeStamp()))){

                hideVisibility(bookingEdit);
                hideVisibility(bookingDelete);
                //Log.e("Booking Complete info: ","Complete");

            }//else
                //Log.e("Booking Complete info: ","Not Complete");

        }


        return v;
    }

    private void hideVisibility(Button bookingEdit) {

        bookingEdit.setVisibility(getView().GONE);
    }

    /**
     *
     * @return yyyy-MM-dd HH:mm:ss formate date as string
     */
    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
