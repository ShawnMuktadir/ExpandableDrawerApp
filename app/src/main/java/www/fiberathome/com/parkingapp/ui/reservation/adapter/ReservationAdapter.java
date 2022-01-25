package www.fiberathome.com.parkingapp.ui.reservation.adapter;

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
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookedList;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookingArea;
import www.fiberathome.com.parkingapp.data.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.databinding.RowBookingsBinding;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public LatLng location;
    private final ArrayList<BookingArea> bookingAreas = new ArrayList<>();
    private ArrayList<BookedList> bookedLists;
    ReservationAdapterClickListener bookingAdapterClickListener;

    public ReservationAdapter(ReservationActivity context, ArrayList<BookedList> bookedLists,
                              ReservationAdapterClickListener bookingAdapterClickListener) {
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
        return new ReservationViewHolder(itemBinding);
    }

    @SuppressLint({"SetTextI18n", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ReservationViewHolder reservationViewHolder = (ReservationViewHolder) viewHolder;

        BookedList bookedList = bookedLists.get(position);

        if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            reservationViewHolder.binding.textViewParkingSlot.setText(bookedList.getAddress());
        } else {
            reservationViewHolder.binding.textViewParkingSlot.setText(bookedList.getAddressBangla());
        }

        double tmpReservationId = MathUtils.getInstance().convertToDouble(bookedList.getId());
        reservationViewHolder.binding.textViewReservationId.setText(context.getResources().getString(R.string.parking_reservation_id) + " " + MathUtils.getInstance().localeDoubleConverter(context, String.valueOf(tmpReservationId)));

        double tmpTotalBDT = MathUtils.getInstance().convertToDouble(bookedList.getCurrentBill());
        reservationViewHolder.binding.textViewParkingTotalPaymentAmount.setText(context.getResources().getString(R.string.total_fair) + "  " + MathUtils.getInstance().localeDoubleConverter(context, String.valueOf(tmpTotalBDT)));

        double tmpSpotId = MathUtils.getInstance().convertToDouble(bookedList.getPsId());
        reservationViewHolder.binding.textViewSpotId.setText(context.getResources().getString(R.string.parking_spot_id) + "  " + MathUtils.getInstance().localeDoubleConverter(context, bookedList.getPsId()));

        if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            reservationViewHolder.binding.textViewParkingTime.setText(context.getString(R.string.arrival) + " " + bookedList.getTimeStart()
                    + " - \n" + context.getString(R.string.departuretxt) + " " + bookedList.getTimeEnd());
        } else {
            reservationViewHolder.binding.textViewParkingTime.setText(context.getString(R.string.arrival) + " " + TextUtils.getInstance().convertTextEnToBn(bookedList.getTimeStart())
                    + " - \n" + context.getString(R.string.departuretxt) + " " + TextUtils.getInstance().convertTextEnToBn(bookedList.getTimeEnd()));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date endTime = simpleDateFormat.parse(bookedList.getTimeEnd());
            Date startTime = simpleDateFormat.parse(bookedList.getTimeStart());

            long difference = 0;
            if (endTime != null && startTime != null) {
                difference = endTime.getTime() - startTime.getTime();
            }
            if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                reservationViewHolder.binding.textViewParkingTotalTime.setText(getTimeDifference(difference));
            } else {
                reservationViewHolder.binding.textViewParkingTotalTime.setText(TextUtils.getInstance().convertTextEnToBn(getTimeDifference(difference)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0") &&
                bookedList.getP_status().equalsIgnoreCase("1")) {
            reservationViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvCancel.setVisibility(View.GONE);
            reservationViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.parking));
            reservationViewHolder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.green2));
            reservationViewHolder.binding.tvGetDirection.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvRebooking.setVisibility(View.GONE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("0") && bookedList.getStatus().equalsIgnoreCase("0")) {
            reservationViewHolder.binding.tvCancel.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvStatus.setVisibility(View.GONE);
            reservationViewHolder.binding.tvGetDirection.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvRebooking.setVisibility(View.GONE);
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
            reservationViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvCancel.setVisibility(View.GONE);
            reservationViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.completed));
            reservationViewHolder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.green2));
            reservationViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            reservationViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("1")) {
            reservationViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.canceled));
            reservationViewHolder.binding.tvCancel.setVisibility(View.GONE);
            reservationViewHolder.binding.tvStatus.setTextColor(Color.RED);
            reservationViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            reservationViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        } else if (bookedList.getC_Status().equalsIgnoreCase("1") && bookedList.getStatus().equalsIgnoreCase("0")) {
            reservationViewHolder.binding.tvStatus.setVisibility(View.VISIBLE);
            reservationViewHolder.binding.tvStatus.setText(context.getResources().getString(R.string.rejected));
            reservationViewHolder.binding.tvCancel.setVisibility(View.GONE);
            reservationViewHolder.binding.tvStatus.setTextColor(Color.RED);
            reservationViewHolder.binding.tvGetDirection.setVisibility(View.GONE);
            reservationViewHolder.binding.tvRebooking.setVisibility(View.VISIBLE);
            checkBookingStatusChange(bookedList);
        }

        reservationViewHolder.binding.tvRebooking.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                bookingAdapterClickListener.onItemRebookListener(position, MathUtils.getInstance().convertToDouble(bookedList.getLatitude()),
                        MathUtils.getInstance().convertToDouble(bookedList.getLongitude()), bookedList.getParkingArea(),
                        bookedList.getNoOfParking(), bookedList.getAreaId(), bookedList.getAddressBangla());
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        reservationViewHolder.binding.tvGetHelp.setOnClickListener(v -> bookingAdapterClickListener.onItemGetHelp());

        reservationViewHolder.binding.tvGetDirection.setOnClickListener(v -> bookingAdapterClickListener.onItemGetDirection(position));

        reservationViewHolder.binding.tvCancel.setOnClickListener(v -> bookingAdapterClickListener.onReservationItemCancel(reservationViewHolder.getAbsoluteAdapterPosition(), bookedList.getSpotId(), bookedList.getId()));

        reservationViewHolder.binding.btnViewReceipt.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        reservationViewHolder.binding.btnGetHelp.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());
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

    private String getTimeDifference(long difference) {
        return String.format(Locale.US, "%02d " + context.getResources().getString(R.string.hr) + ": %02d " + context.getResources().getString(R.string.mins),
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)));
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
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        RowBookingsBinding binding;

        public ReservationViewHolder(RowBookingsBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface ReservationAdapterClickListener {
        void onReservationItemCancel(int position, String uid, String id);

        void onItemGetHelp();

        void onItemGetDirection(int position);

        void onItemRebookListener(int position, double lat, double lng, String parkingArea, String count, String placeId, String areaNameBangla);
    }
}
