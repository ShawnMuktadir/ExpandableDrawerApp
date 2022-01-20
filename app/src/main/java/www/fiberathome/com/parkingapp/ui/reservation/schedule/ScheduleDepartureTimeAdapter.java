package www.fiberathome.com.parkingapp.ui.reservation.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.data.model.DepartureTimeData;
import www.fiberathome.com.parkingapp.databinding.RowHourLayoutBinding;

public class ScheduleDepartureTimeAdapter extends RecyclerView.Adapter<ScheduleDepartureTimeAdapter.ScheduleDepartureTimeViewHolder> {

    private final ArrayList<DepartureTimeData> departureTimeDataArrayList;
    private final Context mContext;
    private final OnItemClickListeners clickListeners;

    public ScheduleDepartureTimeAdapter(ArrayList<DepartureTimeData> departureTimeDataArrayList, Context mContext, OnItemClickListeners clickListeners) {
        this.departureTimeDataArrayList = departureTimeDataArrayList;
        this.mContext = mContext;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public ScheduleDepartureTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        RowHourLayoutBinding itemBinding = RowHourLayoutBinding.inflate(layoutInflater, parent, false);
        return new ScheduleDepartureTimeViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleDepartureTimeViewHolder holder, int position) {
        // Set the data to textview and imageview.
        DepartureTimeData departureTimeData = departureTimeDataArrayList.get(position);
        holder.binding.tvTime.setText(departureTimeData.getTitle());
        holder.binding.cvHourLayout.setOnClickListener(v -> clickListeners.onClick(departureTimeData.getTimeValue()));
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return departureTimeDataArrayList.size();
    }

    public interface OnItemClickListeners {
        void onClick(double timeValue);
    }

    // View Holder Class to handle Recycler View.
    public static class ScheduleDepartureTimeViewHolder extends RecyclerView.ViewHolder {

        RowHourLayoutBinding binding;

        public ScheduleDepartureTimeViewHolder(RowHourLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

