package www.fiberathome.com.parkingapp.ui.parking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.SensorArea;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingFragment parkingFragment;
    private int selectedItem;

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

        parkingViewHolder.card_view.setOnClickListener(v -> {
            parkingFragment.layoutVisible(true, sensorArea.getParkingArea(), sensorArea.getCount(), new LatLng(sensorArea.getLat(), sensorArea.getLng()));
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
        @BindView(R.id.view)
        View view;

        public ParkingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
