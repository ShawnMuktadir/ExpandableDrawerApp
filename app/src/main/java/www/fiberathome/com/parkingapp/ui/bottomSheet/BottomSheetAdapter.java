package www.fiberathome.com.parkingapp.ui.bottomSheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.databinding.BottomSheetTextRecyclerItemBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.TextBookingViewHolder> {

    public Context context;
    public final HomeFragment homeFragment;
    private ArrayList<BookingSensors> bookingSensorsArrayList;
    public Location location;
    private final onItemClickListeners clickListeners;

    public BottomSheetAdapter(Context context, HomeFragment homeFragment,
                              Location onConnectedLocation, onItemClickListeners clickListeners) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.location = onConnectedLocation;
        this.clickListeners = clickListeners;
        this.bookingSensorsArrayList = new ArrayList<>();
    }

    public interface onItemClickListeners {
        void onClick(BookingSensors bookingSensors);
    }

    @NonNull
    @Override
    public TextBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        BottomSheetTextRecyclerItemBinding itemBinding = BottomSheetTextRecyclerItemBinding.inflate(layoutInflater, parent, false);
        return new TextBookingViewHolder(itemBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TextBookingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);
        if (bookingSensors.type == BookingSensors.SELECTED_INFO_TYPE) {
            holder.binding.textBottom.setVisibility(View.VISIBLE);
            holder.binding.textViewStatic.setText(bookingSensors.getText());
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
            holder.binding.textBottom.setVisibility(View.VISIBLE);
        } else {
            holder.binding.textBottom.setVisibility(View.GONE);
        }
        holder.binding.textViewParkingAreaName.setText(bookingSensors.getParkingArea());
        if (bookingSensors.getPsId() != null && !bookingSensors.getPsId().equalsIgnoreCase("")) {
            holder.binding.textViewPsId.setText("( Spot No: " + bookingSensors.getPsId() + " )");
        } else {
            holder.binding.textViewPsId.setText("");
        }
        if (bookingSensors.getOccupiedCount() != null) {
            holder.binding.textViewParkingAreaCount.setText(bookingSensors.getOccupiedCount() + "/" + bookingSensors.getCount());
        } else {
            holder.binding.textViewParkingAreaCount.setText(bookingSensors.getCount());
        }
        holder.binding.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");
        holder.binding.rowFG.setOnClickListener(view -> {
            if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                clickListeners.onClick(bookingSensors);
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("00.0", new DecimalFormatSymbols(Locale.US));
        double tmp = MathUtils.getInstance().convertToDouble(decimalFormat.format(Double
                .parseDouble(bookingSensors.getDuration())));
        holder.binding.textViewParkingTravelTime.setText(tmp + " mins");
    }

    @Override
    public int getItemCount() {
        if (bookingSensorsArrayList.size() > 6) {
            return 5;
        } else {
            return bookingSensorsArrayList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (bookingSensorsArrayList.get(position).type == 0) {
            return BookingSensors.SELECTED_INFO_TYPE;
        }
        return BookingSensors.INFO_TYPE;
    }

    public void setDataList(ArrayList<BookingSensors> dataList) {
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList = dataList;
        notifyDataSetChanged();
    }

    public void clear() {
        bookingSensorsArrayList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public static class TextBookingViewHolder extends RecyclerView.ViewHolder {
        BottomSheetTextRecyclerItemBinding binding;

        public TextBookingViewHolder(BottomSheetTextRecyclerItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}