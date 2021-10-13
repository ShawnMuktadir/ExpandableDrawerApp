package www.fiberathome.com.parkingapp.ui.booking.newBooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.model.response.booking.BookingArea;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public LatLng location;
    private final ArrayList<BookingArea> bookingAreas = new ArrayList<>();
    private ArrayList<BookedList> bookedLists;
    BookingAdapterClickListener bookingAdapterClickListener;

    public BookingAdapter(BookingActivity context, ArrayList<BookedList> bookedLists, BookingAdapterClickListener bookingAdapterClickListener) {
        this.context = context;
        this.bookedLists = bookedLists;
        this.bookingAdapterClickListener = bookingAdapterClickListener;
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

        BookedList bookedList = bookedLists.get(position);
        Timber.e("bookedList adapter -> %s", new Gson().toJson(bookedList));

        bookingViewHolder.textViewParkingSlot.setText(bookedList.getAddress());

        bookingViewHolder.textViewParkingTime.setText("Arrival " + bookedList.getTimeStart() + " - \n" + "Departure " + bookedList.getTimeEnd());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        try {
            Date date1 = simpleDateFormat.parse(bookedList.getTimeEnd());
            Date date2 = simpleDateFormat.parse(bookedList.getTimeStart());

            long difference = 0;
            if (date1 != null && date2 != null) {
                difference = date1.getTime() - date2.getTime();
            }
            bookingViewHolder.textViewParkingTotalTime.setText(getTimeDifference(difference));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (bookedList.getC_status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0") && bookedList.getP_status().equalsIgnoreCase("1")) {
            bookingViewHolder.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.btnCancel.setVisibility(View.GONE);
            bookingViewHolder.tvStatus.setText("Parking");
            bookingViewHolder.tvStatus.setTextColor(context.getColor(R.color.green2));
        } else if (bookedList.getC_status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0")) {
            bookingViewHolder.btnCancel.setVisibility(View.VISIBLE);
            bookingViewHolder.tvStatus.setVisibility(View.GONE);
        } else if (bookedList.getC_status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.btnCancel.setVisibility(View.GONE);
            bookingViewHolder.tvStatus.setText("Completed");
            bookingViewHolder.tvStatus.setTextColor(context.getColor(R.color.green2));
        } else if (bookedList.getC_status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.tvStatus.setText("Canceled");
            bookingViewHolder.btnCancel.setVisibility(View.GONE);
            bookingViewHolder.tvStatus.setTextColor(Color.RED);
        } else {
            bookingViewHolder.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.tvStatus.setText("Rejected");
            bookingViewHolder.btnCancel.setVisibility(View.GONE);
            bookingViewHolder.tvStatus.setTextColor(Color.RED);
        }

        bookingViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingAdapterClickListener.onItemClick(bookingViewHolder.getAbsoluteAdapterPosition(), bookedList.getSpotId());
            }
        });

        bookingViewHolder.textViewParkingRateNTip.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        bookingViewHolder.btnViewReceipt.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        bookingViewHolder.btnGetHelp.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());
    }

    public void setDataList(ArrayList<BookedList> dataList) {
        this.bookedLists = dataList;
        notifyDataSetChanged();
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
        return bookedLists.size();
    }

    public void updateList(ArrayList<BookedList> mBookedLists) {
        bookedLists.clear();
        bookedLists.addAll(mBookedLists);
        notifyDataSetChanged();

    }

    @SuppressLint("NonConstantResourceId")
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

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.btnViewReceipt)
        Button btnViewReceipt;

        @BindView(R.id.btnGetHelp)
        Button btnGetHelp;

        @BindView(R.id.btnCancel)
        Button btnCancel;

        @BindView(R.id.card_view)
        CardView card_view;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface BookingAdapterClickListener {
        void onItemClick(int position, String uid);
    }
}
