package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.model.sensors.SensorArea;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;

import static android.content.Context.LOCATION_SERVICE;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingFragment parkingFragment;
    private HomeFragment homeFragment;
    private double distance;
    private String duration;
    public LatLng location;
    private Location onConnectedLocation;

    private int selectedPosition = -1;
    private boolean isItemClicked = false;
    private boolean isExpanded = false;

    private ParkingAdapterClickListener mListener;

    public interface ParkingAdapterClickListener {
        void onItemClick(int position);
    }

    public ParkingAdapter(Context context, ParkingFragment parkingFragment, HomeFragment homeFragment, ArrayList<SensorArea> sensorAreas, Location onConnectedLocation, ParkingAdapterClickListener mListener) {
        this.context = context;
        this.parkingFragment = parkingFragment;
        this.homeFragment = homeFragment;
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
        context = parent.getContext();
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

        // Here I am just highlighting the background
        parkingViewHolder.itemView.setBackgroundColor(selectedPosition == position ?
                context.getResources().getColor(R.color.selectedColor) : Color.TRANSPARENT);
        //parkingViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
        parkingViewHolder.itemView.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                selectedPosition = position;
                mListener.onItemClick(position);
                try {
                    notifyDataSetChanged();
                    Timber.e("try called");
                    //data is set in SharedData, to retrieve this data in HomeFragment
                    Timber.e("Sensor Area to SharedData -> %s", new Gson().toJson(sensorArea));
                    SharedData.getInstance().setSensorArea(sensorArea);
                } catch (Exception e) {
                    Timber.e("try catch called -> %s", e.getMessage());
                }
                //Pop the Parking Fragment and Replace it with HomeFragment
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mListener.onItemClick(position);
                        EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorArea.getLat(), sensorArea.getLng())));
                    }
                }, 300);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
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
//            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                    .setTitle("GPS Permissions")
//                    .setMessage("GPS is required for this app to work. Please enable GPS.")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        intent.putExtra("position",position);
//                        Activity origin = (Activity)context;
//
//                        origin.startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();
        }

        return false;
    }

    // implements View.OnClickListener
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
