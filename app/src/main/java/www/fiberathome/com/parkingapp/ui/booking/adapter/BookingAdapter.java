package www.fiberathome.com.parkingapp.ui.booking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.BookedPlace;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookedList;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookingArea;
import www.fiberathome.com.parkingapp.data.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.databinding.RowBookingsBinding;
import www.fiberathome.com.parkingapp.ui.booking.BookingActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
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
        RowBookingsBinding itemBinding = RowBookingsBinding.inflate(layoutInflater, parent, false);
        return new BookingViewHolder(itemBinding);
    }

    @SuppressLint({"SetTextI18n", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;

        BookedList bookedList = bookedLists.get(position);
        bookingViewHolder.binding.textViewParkingSlot.setText(bookedList.getAddress());
        bookingViewHolder.binding.textViewReservationId.setText(context.getResources().getString(R.string.parking_reservation_id) + bookedList.getId());
        bookingViewHolder.binding.textViewParkingTotalPaymentAmount.setText(context.getResources().getString(R.string.total_fair) + "  " + bookedList.getCurrentBill());
        bookingViewHolder.binding.textViewSpotId.setText(context.getResources().getString(R.string.parking_spot_id) + bookedList.getPsId());
        bookingViewHolder.binding.textViewParkingTime.setText(context.getString(R.string.arrival) + " " + bookedList.getTimeStart()
                + " - \n" + context.getString(R.string.departuretxt) + " " + bookedList.getTimeEnd());

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

        if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0") &&
                bookedList.getP_status().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.parking));
            bookingViewHolder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.green2));
            bookingViewHolder.binding.tvGetDirection.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvRebooking.setVisibility(View.GONE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0")) {
            bookingViewHolder.binding.tvCancel.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setVisibility(View.GONE);
            bookingViewHolder.binding.tvGetDirection.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvRebooking.setVisibility(View.GONE);
            if (!Preferences.getInstance(context).getBooked().getIsBooked()) {
                List<SensorArea> sensorAreaArrayList = Preferences.getInstance(context).getSensorAreaList();
                SensorArea sensorArea;
                for (SensorArea status : sensorAreaArrayList) {
                    if (status.getPlaceId().equalsIgnoreCase(bookedList.getAreaId())) {
                        BookedPlace mBookedPlace = new BookedPlace();
                        mBookedPlace.setBill(MathUtils.getInstance().convertToFloat(bookedList.getCurrentBill()));
                        mBookedPlace.setLat(MathUtils.getInstance().convertToDouble(bookedList.getLatitude()));
                        mBookedPlace.setLon(MathUtils.getInstance().convertToDouble(bookedList.getLongitude()));
                        mBookedPlace.setAreaName(bookedList.getAddress());
                        mBookedPlace.setParkingSlotCount(status.getCount());
                        mBookedPlace.setDepartedDate(DateTimeUtils.getInstance().getStringDateToMillis(bookedList.getTimeEnd()));
                        mBookedPlace.setArriveDate(DateTimeUtils.getInstance().getStringDateToMillis(bookedList.getTimeStart()));
                        mBookedPlace.setPlaceId(bookedList.getAreaId());
                        mBookedPlace.setPaid(true);
                        mBookedPlace.setBookedUid(bookedList.getSpotId());
                        mBookedPlace.setReservation(bookedList.getId());
                        mBookedPlace.setIsBooked(true);
                        mBookedPlace.setPsId(bookedList.getPsId());
                        Preferences.getInstance(context).setBooked(mBookedPlace);
                        Preferences.getInstance(context).isBookingCancelled = true;
                        ApplicationUtils.startBookingTrackService(context);
                        break;
                    }
                }
            }
        } else if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.completed));
            bookingViewHolder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.green2));
            bookingViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            bookingViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("1")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.canceled));
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setTextColor(Color.RED);
            bookingViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            bookingViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("0")) {
            bookingViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            bookingViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.rejected));
            bookingViewHolder.binding.tvCancel.setVisibility(View.GONE);
            bookingViewHolder.binding.tvStatus.setTextColor(Color.RED);
            bookingViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            bookingViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        }

        bookingViewHolder.binding.tvRebooking.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                bookingAdapterClickListener.onItemRebookListener(position, MathUtils.getInstance().convertToDouble(bookedList.getLatitude()),
                        MathUtils.getInstance().convertToDouble(bookedList.getLongitude()), bookedList.getParkingArea(),
                        bookedList.getNoOfParking(), bookedList.getAreaId());
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        bookingViewHolder.binding.tvGetHelp.setOnClickListener(v -> bookingAdapterClickListener.onItemGetHelp());

        bookingViewHolder.binding.tvGetDirection.setOnClickListener(v -> bookingAdapterClickListener.onItemGetDirection(position));

        bookingViewHolder.binding.tvCancel.setOnClickListener(v -> bookingAdapterClickListener.onBookingItemCancel(bookingViewHolder.getAbsoluteAdapterPosition(), bookedList.getSpotId(), bookedList.getId()));

        bookingViewHolder.binding.btnViewReceipt.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        bookingViewHolder.binding.btnGetHelp.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());
    }

    private void checkBookingStatusChange(BookedList bookedList) {
        if (Preferences.getInstance(context).getBooked().getIsBooked()) {
            if (bookedList.getId().equalsIgnoreCase(Preferences.getInstance(context).getBooked().getReservation())) {
                Preferences.getInstance(context).clearBooking();
                ApplicationUtils.stopBookingTrackService(context);
                Preferences.getInstance(context).isBookingCancelled = true;
            }
        }
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
        RowBookingsBinding binding;

        public BookingViewHolder(RowBookingsBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface BookingAdapterClickListener {
        void onBookingItemCancel(int position, String uid, String id);

        void onItemGetHelp();

        void onItemGetDirection(int position);

        void onItemRebookListener(int position, double lat, double lng, String parkingArea, String count, String placeId);
    }
}
