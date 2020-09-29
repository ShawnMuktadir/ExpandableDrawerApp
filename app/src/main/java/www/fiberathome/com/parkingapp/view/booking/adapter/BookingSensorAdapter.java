package www.fiberathome.com.parkingapp.view.booking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.view.main.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class BookingSensorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//public class BookingSensorAdapter extends RecyclerView.Adapter<BookingSensorAdapter.BookingViewHolder> {

    private Context context;
    private HomeFragment homeFragment;
    private ArrayList<BookingSensors> bookingSensorsArrayList;
    public BookingViewHolder viewHolder;
    private int selectedItem = -1;
    private int total_types;

    public BookingSensorAdapter(Context context, HomeFragment homeFragment, ArrayList<BookingSensors> sensors) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.bookingSensorsArrayList = sensors;
        total_types = bookingSensorsArrayList.size();
//        selectedItem = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        switch (viewType) {
            case BookingSensors.INFO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_recycler_item, parent, false);
                return new BookingViewHolder(view);
            case BookingSensors.TEXT_INFO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_text_recycler_item, parent, false);
                return new TextBookingViewHolder(view);
        }
        return null;

//        View itemView;
//        itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.bottom_sheet_recycler_item, parent, false);
//        return new BookingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);
        if (bookingSensors != null) {
            switch (bookingSensors.type) {
                case BookingSensors.INFO_TYPE:
                    ((BookingViewHolder) holder).textViewParkingAreaName.setText(ApplicationUtils.capitalize(bookingSensors.getParkingArea()));
                    ((BookingViewHolder) holder).textViewParkingAreaCount.setText(bookingSensors.getCount());
                    ((BookingViewHolder) holder).textViewParkingDistance.setText(
                            new DecimalFormat("##.##").format(bookingSensors.getDistance()) + " km");
                    ((BookingViewHolder) holder).textViewParkingTravelTime.setText(bookingSensors.getDuration());

                    ((BookingViewHolder) holder).itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);

                    ((BookingViewHolder) holder).itemView.setOnClickListener(v -> {
                        selectedItem = position;
                        notifyDataSetChanged();
                        getDestinationDurationInfoForSearchLayout(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()),
                                ((BookingViewHolder) holder));
                        homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
                                ((BookingViewHolder) holder).textViewParkingDistance.getText().toString(),
                                ((BookingViewHolder) holder).textViewParkingTravelTime.getText().toString(),
                                new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), true);

                        homeFragment.bottomSheetBehavior.setHideable(false);
                        homeFragment.bottomSheetBehavior.setPeekHeight(400);
                    });
                    break;

                case BookingSensors.TEXT_INFO_TYPE:
                    ((TextBookingViewHolder) holder).textViewParkingAreaName.setText(ApplicationUtils.capitalize(bookingSensors.getParkingArea()));
                    ((TextBookingViewHolder) holder).textViewParkingAreaCount.setText(bookingSensors.getCount());
                    ((TextBookingViewHolder) holder).textViewParkingDistance.setText(
                            new DecimalFormat("##.##").format(bookingSensors.getDistance()) + " km");
                    ((TextBookingViewHolder) holder).textViewParkingTravelTime.setText(bookingSensors.getDuration());
                    ((TextBookingViewHolder) holder).textViewStatic.setText(bookingSensors.getText());
                    ((TextBookingViewHolder) holder).itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);

                    ((TextBookingViewHolder) holder).itemView.setOnClickListener(v -> {
                        selectedItem = position;
                        notifyDataSetChanged();
                        getDestinationDurationInfoForFirstSearchLayout(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()),
                                ((TextBookingViewHolder) holder));
                        homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
                                ((TextBookingViewHolder) holder).textViewParkingDistance.getText().toString(),
                                ((TextBookingViewHolder) holder).textViewParkingTravelTime.getText().toString(),
                                new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), true);

                        homeFragment.bottomSheetBehavior.setHideable(false);
//            homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        homeFragment.bottomSheetBehavior.setPeekHeight(400);
                    });
                    break;
            }
        }
    }

//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(@NonNull BookingViewHolder viewHolder, int position) {
//        this.viewHolder = viewHolder;
//
//        BookingViewHolder bookingViewHolder = (BookingViewHolder) viewHolder;
//
//        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);
//
//        bookingViewHolder.textViewParkingAreaName.setText(ApplicationUtils.capitalize(bookingSensors.getParkingArea()));
//        bookingViewHolder.textViewParkingAreaCount.setText(bookingSensors.getCount());
//
////        getDestinationDurationInfo(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), bookingViewHolder);
//
//        // Here I am just highlighting the background
//        bookingViewHolder.itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);
//
//        bookingViewHolder.itemView.setOnClickListener(v -> {
////            Collections.swap(bookingSensorsArrayList, position, 0);
////            notifyItemMoved(position, 0);
////            notifyDataSetChanged();
//            selectedItem = position;
//            notifyDataSetChanged();
//            getDestinationDurationInfoForSearchLayout(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()),
//                    bookingViewHolder);
//            homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
//                    bookingViewHolder.textViewParkingDistance.getText().toString(),
//                    bookingViewHolder.textViewParkingTravelTime.getText().toString(),
//                    new LatLng(bookingSensors.getLat(), bookingSensors.getLng()));
//
//            homeFragment.bottomSheetBehavior.setHideable(false);
////            homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
//            homeFragment.bottomSheetBehavior.setPeekHeight(300);
//        });
//    }

    @Override
    public int getItemViewType(int position) {
        switch (bookingSensorsArrayList.get(position).type) {
            case 0:
                return BookingSensors.TEXT_INFO_TYPE;
            case 1:
                return BookingSensors.INFO_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return (null != bookingSensorsArrayList ? bookingSensorsArrayList.size() : 0);
    }

    //    private LatLng origin;
    private String adapterDistance;
    private String adapterDuration;
    private LatLng origin = null;

    private void getDestinationDurationInfo(Context context, LatLng latLngDestination, BookingSensorAdapter.BookingViewHolder bookingViewHolder) {

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
//        if (homeFragment.searchPlaceLatLng != null && homeFragment.bottomSheetSearch == 1) {
//            origin = new LatLng(homeFragment.searchPlaceLatLng.latitude, homeFragment.searchPlaceLatLng.longitude);
//            Timber.e("adapter if e dhukche location, bottomSheetSearch -> %s %s", origin, homeFragment.bottomSheetSearch);
//        } else if (homeFragment.searchPlaceLatLng != null && homeFragment.bottomSheetSearch == 0) {
//            origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());
//            Timber.e("adapter if else e dhukche location, bottomSheetSearch -> %s %s", origin, homeFragment.bottomSheetSearch);
//        } else {
            origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());
//            Timber.e("adapter else e dhukche location, bottomSheetSearch -> %s %s", origin, homeFragment.bottomSheetSearch);
//        }

        LatLng destination = latLngDestination;
//        LatLng destination;
//        if (homeFragment.searchPlaceLatLng != null && homeFragment.bottomSheetSearch == 0) {
//            destination = new LatLng(homeFragment.searchPlaceLatLng.latitude, homeFragment.searchPlaceLatLng.longitude);
//        } else {
//            destination = latLngDestination;
//        }
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
                            Timber.e("BookingSensorAdapter adapterDistance adapterDuration -> %s %s", adapterDistance, adapterDuration);
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

    private String fromCurrentLocationDistance;
    private String fromCurrentLocationDuration;

    public void getDestinationDurationInfoForSearchLayout(Context context, LatLng latLngDestination, BookingSensorAdapter.BookingViewHolder bookingViewHolder) {

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\

        origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());

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
                            fromCurrentLocationDistance = distance;
                            fromCurrentLocationDuration = duration;
                            Timber.e("fromCurrentLocationDistance -> %s", fromCurrentLocationDistance);
                            Timber.e("fromCurrentLocationDuration -> %s", fromCurrentLocationDuration);
//                            if (homeFragment.bottomSheetSearch == 0) {
                            Timber.e("adapter homeFragment.bottomSheetSearch == 0 e dhukche");
                            bookingViewHolder.textViewParkingDistance.setText(fromCurrentLocationDistance);
                            bookingViewHolder.textViewParkingTravelTime.setText(fromCurrentLocationDuration);
//                            }

                            homeFragment.textViewBottomSheetParkingDistance.setText(fromCurrentLocationDistance);
                            homeFragment.textViewBottomSheetParkingTravelTime.setText(fromCurrentLocationDuration);

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

    public void getDestinationDurationInfoForFirstSearchLayout(Context context, LatLng latLngDestination, BookingSensorAdapter.TextBookingViewHolder textBookingViewHolder) {

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\

        origin = new LatLng(HomeFragment.currentLocation.getLatitude(), HomeFragment.currentLocation.getLongitude());

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
                            fromCurrentLocationDistance = distance;
                            fromCurrentLocationDuration = duration;
                            Timber.e("fromCurrentLocationDistance -> %s", fromCurrentLocationDistance);
                            Timber.e("fromCurrentLocationDuration -> %s", fromCurrentLocationDuration);
//                            if (homeFragment.bottomSheetSearch == 0) {
                            Timber.e("adapter homeFragment.bottomSheetSearch == 0 e dhukche");
//                            textBookingViewHolder.textViewParkingDistance.setText(fromCurrentLocationDistance);
//                            textBookingViewHolder.textViewParkingTravelTime.setText(fromCurrentLocationDuration);
//                            }

                            homeFragment.textViewBottomSheetParkingDistance.setText(fromCurrentLocationDistance);
                            homeFragment.textViewBottomSheetParkingTravelTime.setText(fromCurrentLocationDuration);

                            Timber.e("getDestinationDurationInfo duration -> %s", textBookingViewHolder.textViewParkingTravelTime.getText().toString());
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
        Timber.e("updateData call hoiche");
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
            notifyItemChanged(selectedItem);
            selectedItem = getAdapterPosition();
            notifyItemChanged(selectedItem);

            // Do your another stuff for your onClick
            // get position
//            int pos = getAdapterPosition();
//
//            // check if item still exists
//            if (pos != RecyclerView.NO_POSITION) {
//                Timber.e("adpter position click hoiche");
//                BookingSensors clickedDataItem = bookingSensorsArrayList.get(pos);
//                Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getParkingArea(), Toast.LENGTH_SHORT).show();
//            }
        }
    }

    public class TextBookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        @BindView(R.id.textViewStatic)
        public TextView textViewStatic;
        @BindView(R.id.view)
        View view;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;


        public TextBookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selectedItem);
            selectedItem = getAdapterPosition();
            notifyItemChanged(selectedItem);
        }
    }
}
