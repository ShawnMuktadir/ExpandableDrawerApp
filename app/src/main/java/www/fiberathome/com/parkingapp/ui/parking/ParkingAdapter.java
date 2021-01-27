package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

import static android.content.Context.LOCATION_SERVICE;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParkingActivity context;
    private ArrayList<SensorArea> sensorAreas;
    private double distance;
    private String duration;
    public LatLng location;
    private Location onConnectedLocation;

    private int selectedPosition = -1;
    private boolean isItemClicked = false;
    private boolean isExpanded = false;

    private ParkingAdapterClickListener mListener;

    public interface ParkingAdapterClickListener {
        void onItemClick(int position, double lat, double lng, String parkingArea, String count);
    }

    public ParkingAdapter(ParkingActivity context, ArrayList<SensorArea> sensorAreas,
                          Location onConnectedLocation, ParkingAdapterClickListener mListener) {
        this.context = context;
        this.sensorAreas = sensorAreas;
        this.onConnectedLocation = onConnectedLocation;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.parking_row, parent, false);
        context = (ParkingActivity) parent.getContext();
        return new ParkingViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ParkingViewHolder parkingViewHolder = (ParkingViewHolder) viewHolder;

        SensorArea sensorArea = sensorAreas.get(position);

        parkingViewHolder.textViewParkingAreaName.setText(ApplicationUtils.capitalizeFirstLetter(sensorArea.getParkingArea()));

        parkingViewHolder.textViewParkingAreaCount.setText(sensorArea.getCount());

        distance = ApplicationUtils.calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                sensorArea.getLat(), sensorArea.getLng());
        sensorArea.setDistance(distance);

        parkingViewHolder.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(distance) + " km");

        sensorArea.setDuration(duration);

        parkingViewHolder.textViewParkingTravelTime.setText(sensorArea.getDuration());

        //Here I am just highlighting the background
        parkingViewHolder.itemView.setBackgroundColor(selectedPosition == position ?
                context.getResources().getColor(R.color.selectedColor) : Color.TRANSPARENT);

        parkingViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingViewHolder.relativeLayout.setBackgroundColor(selectedPosition == position ?
                        context.getResources().getColor(R.color.selectedColor) : Color.TRANSPARENT);
                mListener.onItemClick(position, sensorArea.getLat(), sensorArea.getLng(), sensorArea.getParkingArea(), sensorArea.getCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorAreas.size();
    }

    //called by parking fragment
    public void filterList(ArrayList<SensorArea> filteredList) {
        sensorAreas = filteredList;
        notifyDataSetChanged();
    }

    public void clearList() {
        sensorAreas.clear();
        notifyDataSetChanged();
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.putExtra("position",position);
                        Activity origin = (Activity)context;

                        origin.startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();*/
        }

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public static class ParkingViewHolder extends RecyclerView.ViewHolder {

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

        public ParkingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
