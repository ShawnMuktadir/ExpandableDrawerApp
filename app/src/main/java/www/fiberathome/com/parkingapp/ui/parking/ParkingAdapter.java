package www.fiberathome.com.parkingapp.ui.parking;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.databinding.ParkingRowBinding;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.utils.TextUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParkingActivity context;
    private ArrayList<SensorArea> sensorAreas;
    public LatLng location;
    public final Location onConnectedLocation;
    private int selectedPosition = -1;
    private final ParkingAdapterClickListener mListener;

    public interface ParkingAdapterClickListener {
        void onItemClick(int position, double lat, double lng, String parkingArea, String count, String placeId);
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
        context = (ParkingActivity) parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ParkingRowBinding itemBinding = ParkingRowBinding.inflate(layoutInflater, parent, false);
        return new ParkingViewHolder(itemBinding);
    }

    @SuppressLint({"SetTextI18n", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ParkingViewHolder parkingViewHolder = (ParkingViewHolder) viewHolder;
        SensorArea sensorArea = sensorAreas.get(position);

        parkingViewHolder.binding.textViewParkingAreaName.setText(TextUtils.getInstance().capitalizeFirstLetter(sensorArea.getParkingArea()));

        parkingViewHolder.binding.textViewParkingAreaCount.setText(sensorArea.getCount());

        parkingViewHolder.binding.textViewParkingDistance.setText(new DecimalFormat("##.#",
                new DecimalFormatSymbols(Locale.US)).format(sensorArea.getDistance()) + " km");

        parkingViewHolder.binding.textViewParkingTravelTime.setText(sensorArea.getDuration());

        parkingViewHolder.binding.relativeLayout.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
            mListener.onItemClick(position, sensorArea.getEndLat(), sensorArea.getEndLng(), sensorArea.getParkingArea(), sensorArea.getCount(), sensorArea.getPlaceId());
        });

        if (selectedPosition == position) {
            parkingViewHolder.binding.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
        } else {
            parkingViewHolder.binding.relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        }
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

    public void setDataList(ArrayList<SensorArea> dataList) {
        this.sensorAreas = dataList;
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
            Timber.e("else called");
        }

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        ParkingRowBinding binding;

        public ParkingViewHolder(ParkingRowBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
