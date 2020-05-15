package www.fiberathome.com.parkingapp.ui.booking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class BookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private BookingsFragment bookingsFragment;
    private int selectedItem;
    public LatLng location;

    public BookingsAdapter(Context context, BookingsFragment bookingsFragment) {
        this.context = context;
        this.bookingsFragment = bookingsFragment;
        selectedItem = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.bookings_row, parent, false);
        return new BookingViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;
//        SensorArea sensorArea = sensorAreas.get(position);
        bookingViewHolder.textViewParkingSlot.setText("Parking Slot 17");
        bookingViewHolder.textViewParkingSlotAddress.setText("Gulshan - 1, Dhaka, Bangladesh");
        bookingViewHolder.textViewParkingTime.setText(context.getResources().getString(R.string.parking_time));
        bookingViewHolder.textViewParkingTotalTime.setText("2.30h");
        bookingViewHolder.textViewParkingTotalPaymentAmount.setText(context.getResources().getString(R.string.total_fair));


        bookingViewHolder.card_view.setOnClickListener(v -> {
            Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show();
        });

        bookingViewHolder.textViewParkingRateNTip.setOnClickListener(v -> {
            Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show();
        });

        bookingViewHolder.btnViewReceipt.setOnClickListener(v -> {
            Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show();
        });

        bookingViewHolder.btnGetHelp.setOnClickListener(v -> {
            Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return 5;
//        return sensorAreas.size();
    }


    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewParkingSlot)
        TextView textViewParkingSlot;
        @BindView(R.id.textViewParkingSlotAddress)
        TextView textViewParkingSlotAddress;
        @BindView(R.id.textViewParkingTime)
        TextView textViewParkingTime;
        @BindView(R.id.textViewParkingTotalTime)
        TextView textViewParkingTotalTime;
        @BindView(R.id.textViewParkingTotalPaymentAmount)
        TextView textViewParkingTotalPaymentAmount;
        @BindView(R.id.textViewParkingRateNTip)
        TextView textViewParkingRateNTip;
        @BindView(R.id.btnViewReceipt)
        Button btnViewReceipt;
        @BindView(R.id.btnGetHelp)
        Button btnGetHelp;
        @BindView(R.id.card_view)
        CardView card_view;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
