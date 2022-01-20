package www.fiberathome.com.parkingapp.ui.reservation.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.data.model.DepartureTimeData;
import www.fiberathome.com.parkingapp.databinding.RowHourLayoutBinding;

@SuppressWarnings({"unused", "RedundantSuppression", "NotifyDataSetChanged"})
public class ScheduleDepartureTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<DepartureTimeData> departureTimeDataArrayList;
    private final Context mContext;
    private final ScheduleDepartureTimeAdapterOnItemClickListeners clickListeners;
    private int selectedPosition = -1;

    public ScheduleDepartureTimeAdapter(ArrayList<DepartureTimeData> departureTimeDataArrayList,
                                        Context mContext, ScheduleDepartureTimeAdapterOnItemClickListeners clickListeners) {
        this.departureTimeDataArrayList = departureTimeDataArrayList;
        this.mContext = mContext;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        RowHourLayoutBinding itemBinding = RowHourLayoutBinding.inflate(layoutInflater, parent, false);
        return new ScheduleDepartureTimeViewHolder(itemBinding);
    }

    @SuppressLint({"SetTextI18n", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ScheduleDepartureTimeViewHolder holder = (ScheduleDepartureTimeViewHolder) viewHolder;
        DepartureTimeData departureTimeData = departureTimeDataArrayList.get(position);
        holder.binding.tvTime.setText(departureTimeData.getTitle());
        holder.binding.cvHourLayout.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            clickListeners.onClick(departureTimeData.getTimeValue());
        });
        if (selectedPosition == position) {
            holder.binding.cvHourLayout.setBackgroundColor(mContext.getResources().getColor(www.fiberathome.com.parkingapp.R.color.selectedColor));
        } else {
            holder.binding.cvHourLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return departureTimeDataArrayList.size();
    }

    public interface ScheduleDepartureTimeAdapterOnItemClickListeners {
        void onClick(double value);
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

