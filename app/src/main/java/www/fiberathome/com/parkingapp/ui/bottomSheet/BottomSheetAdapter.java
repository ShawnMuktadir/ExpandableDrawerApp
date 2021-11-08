package www.fiberathome.com.parkingapp.ui.bottomSheet;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.databinding.BottomSheetTextRecyclerItemBinding;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.TextBookingViewHolder> {

    public Context context;

    public final HomeFragment homeFragment;

    private ArrayList<BookingSensors> bookingSensorsArrayList;

    public Location location;

    private int selectedItem = -1;

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

            selectedItem = position;

            holder.binding.textBottom.setVisibility(View.VISIBLE);

            holder.binding.textViewStatic.setText(bookingSensors.getText());

            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));

            holder.binding.textBottom.setVisibility(View.VISIBLE);

        } else {
            holder.binding.textBottom.setVisibility(View.GONE);
        }

        holder.binding.textViewParkingAreaName.setText(bookingSensors.getParkingArea());

        holder.binding.textViewParkingAreaCount.setText(bookingSensors.getCount());

        holder.binding.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");

        holder.binding.rowFG.setOnClickListener(view -> {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                clickListeners.onClick(bookingSensors);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("00.0", new DecimalFormatSymbols(Locale.US));

        double tmp = MathUtils.getInstance().convertToDouble(decimalFormat.format(Double
                .parseDouble(bookingSensors.getDuration())));

        holder.binding.textViewParkingTravelTime.setText(tmp + " mins");

        if (selectedItem == position) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
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
        this.bookingSensorsArrayList = dataList;
        notifyDataSetChanged();
    }

    public void update(ArrayList<BookingSensors> datas) {
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear() {
        bookingSensorsArrayList.clear();
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
    public static class TextBookingViewHolder extends RecyclerView.ViewHolder {
        BottomSheetTextRecyclerItemBinding binding;

        public TextBookingViewHolder(BottomSheetTextRecyclerItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}