package www.fiberathome.com.parkingapp.ui.booking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionBottomSheetEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.model.BookingSensors;
import www.fiberathome.com.parkingapp.ui.MainActivity;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.SharedData;

public class BookingSensorAdapter extends RecyclerView.Adapter<BookingSensorAdapter.BookingViewHolder> {

    private int selectedItem = -1;
//    private MainActivity mainActivity;

    private Context context;
    private HomeFragment homeFragment;
    private ArrayList<BookingSensors> bookingSensorsArrayList;
    private String duration;

    public BookingSensorAdapter(Context context, HomeFragment homeFragment, ArrayList<BookingSensors> sensors) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.bookingSensorsArrayList = sensors;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.bottom_sheet_recycler_item, parent, false);
        return new BookingViewHolder(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder viewHolder, int position) {

        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;

        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);

        bookingViewHolder.textViewParkingAreaName.setText(ApplicationUtils.capitalize(bookingSensors.getParkingArea().trim()));
        bookingViewHolder.textViewParkingAreaCount.setText(bookingSensors.getCount());

//        double distance = ApplicationUtils.distance(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude(),
//                bookingSensors.getLat(), bookingSensors.getLng());
//        bookingSensors.setDistance(bookingSensors.getDistance());
//        bookingViewHolder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(adapterDistance) + " km");
//        Timber.e("adapter distance -> %s", bookingViewHolder.textViewParkingDistance.getText());
        bookingViewHolder.textViewParkingDistance.setText(bookingSensors.getDistance());
        bookingViewHolder.textViewParkingTravelTime.setText(bookingSensors.getDuration());

        //setting value for duration
        getDestinationDurationInfo(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), bookingViewHolder);

//        bookingViewHolder.relativeLayout.setOnClickListener(v -> {
//
//            homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(), distance,
//                    bookingViewHolder.textViewParkingTravelTime.getText().toString(), new LatLng(bookingSensors.getLat(), bookingSensors.getLng()));
//
//            selectedItem = position;
//            notifyDataSetChanged();
//        });

        // Here I am just highlighting the background
        bookingViewHolder.itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);

        bookingViewHolder.itemView.setOnClickListener(v -> {
//            Collections.swap(bookingSensorsArrayList, position, 0);
//            notifyItemMoved(position, 0);
//            notifyDataSetChanged();
            homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
                    bookingViewHolder.textViewParkingDistance.getText().toString(),
                    bookingViewHolder.textViewParkingTravelTime.getText().toString(),
                    new LatLng(bookingSensors.getLat(), bookingSensors.getLng()));

            homeFragment.bottomSheetBehavior.setHideable(false);
//            homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            homeFragment.bottomSheetBehavior.setPeekHeight(300);
        });
    }


    @Override
    public int getItemCount() {
        return (null != bookingSensorsArrayList ? bookingSensorsArrayList.size() : 0);
    }

    //    private LatLng origin;
    private String adapterDistance;
    private String adapterDuration;
    private LatLng origin;

    private void getDestinationDurationInfo(Context context, LatLng latLngDestination, BookingSensorAdapter.BookingViewHolder bookingViewHolder) {

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
        if (homeFragment.searchPlaceLatLng != null && homeFragment.bottomSheetSearch == 1) {
            origin = new LatLng(homeFragment.searchPlaceLatLng.latitude, homeFragment.searchPlaceLatLng.longitude);
        } else if (homeFragment.searchPlaceLatLng != null && homeFragment.bottomSheetSearch == 0) {
            origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());
        } else {
            origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());
        }

        LatLng destination = latLngDestination;
        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        dismissDialog();
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            adapterDistance = distance;
                            adapterDuration = duration;
//                            bookingSensors.setDistance(adapterDistance);
//                            bookingSensors.setDuration(adapterDuration);
                            bookingViewHolder.textViewParkingDistance.setText(adapterDistance);
                            bookingViewHolder.textViewParkingTravelTime.setText(adapterDuration);
                            Timber.e("getDestinationDurationInfo duration -> %s", bookingViewHolder.textViewParkingTravelTime.getText().toString());
                            //------------Displaying Distance and Time-----------------\\
//                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
                            Timber.e("duration message -> %s", message);

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(context, "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\

    }

    public void updateData(ArrayList<BookingSensors> bookingSensors) {
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(bookingSensors);
        notifyDataSetChanged();
    }

    public void clear() {
        bookingSensorsArrayList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<BookingSensors> bookingSensorsArrayList) {
        bookingSensorsArrayList.addAll(bookingSensorsArrayList);
        notifyDataSetChanged();
    }

    public void swapeItem(int fromPosition, int toPosition) {
        Timber.e("swapeItem call hoiche");
        Collections.swap(bookingSensorsArrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textViewParkingAreaName)
        public TextView textViewParkingAreaName;
        @BindView(R.id.textViewParkingAreaCount)
        public TextView textViewParkingAreaCount;
        @BindView(R.id.textViewParkingAreaAddress)
        public TextView textViewParkingAreaAddress;
        @BindView(R.id.textViewParkingDistance)
        public TextView textViewParkingDistance;
        @BindView(R.id.textViewParkingTravelTime)
        public TextView textViewParkingTravelTime;
        @BindView(R.id.view)
        View view;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;

        public BookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
// Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
//            notifyItemChanged(selectedItem);
//            selectedItem = getAdapterPosition();
//            notifyItemChanged(selectedItem);

            // Do your another stuff for your onClick
            // get position
            int pos = getAdapterPosition();

            // check if item still exists
            if(pos != RecyclerView.NO_POSITION){
                Timber.e("adpter position click hoiche");
                BookingSensors clickedDataItem = bookingSensorsArrayList.get(pos);
                Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getParkingArea(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
