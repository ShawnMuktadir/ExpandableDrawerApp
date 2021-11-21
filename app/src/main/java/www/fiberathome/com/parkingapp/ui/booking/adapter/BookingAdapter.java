package www.fiberathome.com.parkingapp.ui.booking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.databinding.BookingsRowBinding;
import www.fiberathome.com.parkingapp.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.model.response.booking.BookingArea;
import www.fiberathome.com.parkingapp.ui.booking.BookingActivity;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public LatLng location;
    private final ArrayList<BookingArea> bookingAreas = new ArrayList<>();
    private ArrayList<BookedList> bookedLists;
    BookingAdapterClickListener bookingAdapterClickListener;

    public BookingAdapter(BookingActivity context, ArrayList<BookedList> bookedLists,
                          BookingAdapterClickListener bookingAdapterClickListener) {
        this.context = context;
        this.bookedLists = bookedLists;
        this.bookingAdapterClickListener = bookingAdapterClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        BookingsRowBinding itemBinding = BookingsRowBinding.inflate(layoutInflater, parent, false);
        return new BookingViewHolder(itemBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;

        BookedList bookedList = bookedLists.get(position);
        bookingViewHolder.binding.textViewParkingSlot.setText(bookedList.getAddress());
        bookingViewHolder.binding.textViewParkingTotalPaymentAmount.setText(context.getResources().getString(R.string.total_fair) + " " + bookedList.getCurrentBill());
        bookingViewHolder.binding.textViewSpotId.setText("#" + bookedList.getPsId());
        bookingViewHolder.binding.textViewParkingTime.setText("Arrival " + bookedList.getTimeStart() + " - \n" + "Departure " + bookedList.getTimeEnd());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date1 = simpleDateFormat.parse(bookedList.getTimeEnd());
            Date date2 = simpleDateFormat.parse(bookedList.getTimeStart());

            long difference = 0;
            if (date1 != null && date2 != null) {
                difference = date1.getTime() - date2.getTime();
            }
            bookingViewHolder.binding.textViewParkingTotalTime.setText(getTimeDifference(difference));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0") && bookedList.getP_status().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setText("Parking");
            bookingViewHolder.binding.tvStatus.setTextColor(context.getColor(R.color.green2));
        } else if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0")) {
            bookingViewHolder.binding.tvCancel.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setVisibility(View.GONE);
        } else if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setText("Completed");
            bookingViewHolder.binding.tvStatus.setTextColor(context.getColor(R.color.green2));
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setText("Canceled");
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setTextColor(Color.RED);
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("0")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setText("Rejected");
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setTextColor(Color.RED);
        }

        bookingViewHolder.binding.tvRebooking.setOnClickListener(v -> {
            if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0")) {
                ToastUtils.getInstance().showToast(context, "" + context.getResources().getString(R.string.already_booked_msg));
            } else {
                bookingAdapterClickListener.onItemRebookListener(position);
            }

        });

        bookingViewHolder.binding.tvGetHelp.setOnClickListener(v -> {
            bookingAdapterClickListener.onItemGetHelpListener();
        });

        bookingViewHolder.binding.tvCancel.setOnClickListener(v -> bookingAdapterClickListener.onBookingItemCancel(bookingViewHolder.getAbsoluteAdapterPosition(), bookedList.getSpotId(), bookedList.getId()));

        bookingViewHolder.binding.btnViewReceipt.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        bookingViewHolder.binding.btnGetHelp.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());
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
        BookingsRowBinding binding;

        public BookingViewHolder(BookingsRowBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface BookingAdapterClickListener {
        void onBookingItemCancel(int position, String uid, String id);

        void onItemGetHelpListener();

        void onItemRebookListener(int position);

    }
}
