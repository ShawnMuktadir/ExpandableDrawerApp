package www.fiberathome.com.parkingapp.view.booking.newBooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.booking.BookingArea;

public class BookingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private BookingFragment bookingFragment;
    private int selectedItem;
    public LatLng location;
    private ArrayList<BookingArea> bookingAreas = new ArrayList<>();

    public BookingAdapter(Context context, BookingFragment bookingFragment, ArrayList<BookingArea> bookingAreas) {
        this.context = context;
        this.bookingFragment = bookingFragment;
        this.bookingAreas = bookingAreas;
        selectedItem = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.bookings_row, parent, false);
        context = parent.getContext();
        return new BookingViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;

        BookingArea bookingArea = bookingAreas.get(position);

        bookingViewHolder.textViewParkingSlot.setText(bookingArea.getSpotName());

        bookingViewHolder.textViewParkingTime.setText("Arrival " + bookingArea.getTimeStart() + " - \n" + "Departure " + bookingArea.getTimeEnd());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        try {
            Date date1 = simpleDateFormat.parse(bookingArea.getTimeEnd());
            Date date2 = simpleDateFormat.parse(bookingArea.getTimeStart());

            long difference = date1.getTime() - date2.getTime();

//            String result = substractDates(date1, date2, new SimpleDateFormat("HH:mm:ss"));
            bookingViewHolder.textViewParkingTotalTime.setText(getTimeDifference(difference));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        bookingViewHolder.textViewParkingTotalPaymentAmount.setText(context.getResources().getString(R.string.total_fair));

//        bookingViewHolder.card_view.setOnClickListener(v -> {
//            Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show();
//        });

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

    @SuppressLint("DefaultLocale")
    private String getTimeDifference(long difference) {
        return String.format("%02dh:%02dmin",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)) // The change is in this line
        );
    }

    @Override
    public int getItemCount() {
        return bookingAreas.size();
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
