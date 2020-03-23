package www.fiberathome.com.parkingapp.ui.parking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.model.GlobalVars;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.ui.MainActivity;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.SharedData;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingFragment parkingFragment;
    private HomeFragment homeFragment;
    private int selectedItem;
    private double distance;
    public LatLng location;

    public ParkingAdapter(Context context, ParkingFragment parkingFragment, ArrayList<SensorArea> sensorAreas) {
        this.context = context;
        this.parkingFragment = parkingFragment;
        this.sensorAreas = sensorAreas;
        selectedItem = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.parking_row, parent, false);
        return new ParkingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ParkingViewHolder parkingViewHolder = (ParkingViewHolder) viewHolder;
        SensorArea sensorArea = sensorAreas.get(position);
        parkingViewHolder.textViewParkingAreaName.setText(sensorArea.getParkingArea());
        parkingViewHolder.textViewParkingAreaCount.setText(sensorArea.getCount());

        distance = distance(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude, sensorArea.getLat(), sensorArea.getLng());
        sensorArea.setDistance(distance);
//        String[] duration = getDestinationInfo(new LatLng(sensorArea.getLat(), sensorArea.getLng()));
//        Timber.e("duration -> %s", duration);
        parkingViewHolder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
        Timber.e("distance -> %s", parkingViewHolder.textViewParkingDistance.getText());

        parkingViewHolder.card_view.setOnClickListener(v -> {
            EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorArea.getLat(), sensorArea.getLng())));

            //data is set in SharedData to retrieve this data in HomeFragment
            Timber.e("Sensor Area to SharedData -> %s", new Gson().toJson(sensorArea));
            SharedData.getInstance().setSensorArea(sensorArea);
            //Pop the Parking Fragment and Replace it with HomeFragment
            MainActivity parentActivity = (MainActivity) context;
            parentActivity.replaceFragment();
//            parkingFragment.layoutVisible(true, sensorArea.getParkingArea(), sensorArea.getCount(), distance, new LatLng(sensorArea.getLat(), sensorArea.getLng()));
            selectedItem = position;
            notifyDataSetChanged();
        });

        if (position == selectedItem) {
            //Show view visibility
            parkingViewHolder.view.setVisibility(View.VISIBLE);
        } else {
            //Hide view visibility
            parkingViewHolder.view.setVisibility(View.GONE);
        }
    }
    /**
     * Draw polyline on map, get distance and duration of the route
     *
//     * @param latLngDestination LatLng of the destination
     */
//    private void getDestinationInfo(LatLng latLngDestination) {
////        progressDialog();
//        String serverKey = getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
//        final LatLng origin = new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude);
//        final LatLng destination = latLngDestination;
//        //-------------Using AK Exorcist Google Direction Library---------------\\
//        GoogleDirection.withServerKey(serverKey)
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.DRIVING)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Path.Direction direction, String rawBody) {
////                        dismissDialog();
//                        String status = direction.getStatus();
//                        if (status.equals(RequestResult.OK)) {
//                            Route route = direction.getRouteList().get(0);
//                            Leg leg = route.getLegList().get(0);
//                            IDNA.Info distanceInfo = leg.getDistance();
//                            Info durationInfo = leg.getDuration();
//                            String distance = distanceInfo.getText();
//                            String duration = durationInfo.getText();
//
//                            //------------Displaying Distance and Time-----------------\\
//                            Timber.e("Distance Duration -> %s -> %s", distance, duration);
////                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
////                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
////                            StaticMethods.customSnackBar(consumerHomeActivity.parentLayout, message,
////                                    getResources().getColor(R.color.colorPrimary),
////                                    getResources().getColor(R.color.colorWhite), 3000);
//
//                            //--------------Drawing Path-----------------\\
////                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
////                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(),
////                                    directionPositionList, 5, getResources().getColor(R.color.colorPrimary));
////                            googleMap.addPolyline(polylineOptions);
//                            //--------------------------------------------\\
//
//                            //-----------Zooming the map according to marker bounds-------------\\
////                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
////                            builder.include(origin);
////                            builder.include(destination);
////                            LatLngBounds bounds = builder.build();
////
////                            int width = getResources().getDisplayMetrics().widthPixels;
////                            int height = getResources().getDisplayMetrics().heightPixels;
////                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
////
////                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
////                            googleMap.animateCamera(cu);
//                            //------------------------------------------------------------------\\
//
//                        } else if (status.equals(RequestResult.NOT_FOUND)) {
//                            Toast.makeText(getActivity(), "No routes exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                        // Do something here
//                    }
//                });
//        //-------------------------------------------------------------------------------\\
//
//    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double mile = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        mile = Math.acos(mile);
        mile = rad2deg(mile);
        mile = mile * 60 * 1.1515;
        double km = mile / 0.62137;
        Timber.e("distance -> %s", km);
        return (km);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public int getItemCount() {
        return sensorAreas.size();
    }

    public void filterList(ArrayList<SensorArea> filteredList) {
        sensorAreas = filteredList;
        notifyDataSetChanged();
    }


    public class ParkingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewParkingAreaName)
        TextView textViewParkingAreaName;
        @BindView(R.id.textViewParkingAreaCount)
        TextView textViewParkingAreaCount;
        @BindView(R.id.textViewParkingAreaAddress)
        TextView textViewParkingAreaAddress;
        @BindView(R.id.card_view)
        CardView card_view;
        @BindView(R.id.textViewParkingDistance)
        TextView textViewParkingDistance;
        @BindView(R.id.view)
        View view;

        public ParkingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
