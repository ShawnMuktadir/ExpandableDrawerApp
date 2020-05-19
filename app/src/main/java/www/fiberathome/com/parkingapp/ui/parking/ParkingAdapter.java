package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.ui.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.SharedData;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingFragment parkingFragment;
    private HomeFragment homeFragment;
    private int selectedItem = -1;
    private double distance;
    private String duration;
    public LatLng location;
    private Location onConnectedLocation;

    public ParkingAdapter(Context context, ParkingFragment parkingFragment, ArrayList<SensorArea> sensorAreas, Location onConnectedLocation) {
        this.context = context;
        this.parkingFragment = parkingFragment;
        this.sensorAreas = sensorAreas;
        this.onConnectedLocation = onConnectedLocation;
//        selectedItem = -1;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ParkingViewHolder parkingViewHolder = (ParkingViewHolder) viewHolder;
        SensorArea sensorArea = sensorAreas.get(position);
        parkingViewHolder.textViewParkingAreaName.setText(ApplicationUtils.capitalize(sensorArea.getParkingArea()));
        parkingViewHolder.textViewParkingAreaCount.setText(sensorArea.getCount());

        distance = ApplicationUtils.distance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                sensorArea.getLat(), sensorArea.getLng());
        sensorArea.setDistance(distance);
        parkingViewHolder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
        Timber.e("adapter distance -> %s", parkingViewHolder.textViewParkingDistance.getText());
        sensorArea.setDuration(duration);
        parkingViewHolder.textViewParkingTravelTime.setText(sensorArea.getDuration());

        parkingViewHolder.itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);
        parkingViewHolder.itemView.setOnClickListener(v -> {
            selectedItem = position;
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                Timber.e(e);
            }
            EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorArea.getLat(), sensorArea.getLng())));
            //data is set in SharedData, to retrieve this data in HomeFragment
            Timber.e("Sensor Area to SharedData -> %s", new Gson().toJson(sensorArea));
            SharedData.getInstance().setSensorArea(sensorArea);
            //Pop the Parking Fragment and Replace it with HomeFragment
            MainActivity parentActivity = (MainActivity) context;
            parentActivity.replaceFragment();
//            EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(HomeFragment.location));
        });

//        parkingViewHolder.relativeLayout.setOnClickListener(v -> {
//            EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorArea.getLat(), sensorArea.getLng())));
////            parkingFragment.layoutVisible(true, sensorArea.getParkingArea(), sensorArea.getCount(), String.valueOf(distance), new LatLng(sensorArea.getLat(), sensorArea.getLng()));
//
//            //data is set in SharedData, to retrieve this data in HomeFragment
//            Timber.e("Sensor Area to SharedData -> %s", new Gson().toJson(sensorArea));
//            SharedData.getInstance().setSensorArea(sensorArea);
//            //Pop the Parking Fragment and Replace it with HomeFragment
//            MainActivity parentActivity = (MainActivity) context;
//            parentActivity.replaceFragment();
//            selectedItem = position;
//            notifyDataSetChanged();
////            EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(HomeFragment.location));
//        });
//        if (position == selectedItem) {
//            //Show view visibility
//            parkingViewHolder.view.setVisibility(View.VISIBLE);
//        } else {
//            //Hide view visibility
//            parkingViewHolder.view.setVisibility(View.GONE);
//        }
    }


    /**
     * Draw polyline on map, get distance and duration of the route
     * <p>
     * //     * @param latLngDestination LatLng of the destination
     */

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

    //parking fragment theke call hoiche
    public void filterList(ArrayList<SensorArea> filteredList) {
        sensorAreas = filteredList;
        notifyDataSetChanged();
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textViewParkingAreaName)
        TextView textViewParkingAreaName;
        @BindView(R.id.textViewParkingAreaCount)
        TextView textViewParkingAreaCount;
        @BindView(R.id.textViewParkingAreaAddress)
        TextView textViewParkingAreaAddress;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;
        @BindView(R.id.textViewParkingDistance)
        TextView textViewParkingDistance;
        @BindView(R.id.textViewParkingTravelTime)
        TextView textViewParkingTravelTime;
        @BindView(R.id.view)
        View view;

        public ParkingViewHolder(View itemView) {
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
            try {
                notifyItemChanged(selectedItem);
                selectedItem = getAdapterPosition();
                notifyItemChanged(selectedItem);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
