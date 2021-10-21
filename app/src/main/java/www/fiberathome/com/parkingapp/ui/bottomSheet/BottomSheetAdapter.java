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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_text_recycler_item, parent, false);
        context = parent.getContext();
        return new TextBookingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TextBookingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);

        if (bookingSensors.type == BookingSensors.TEXT_INFO_TYPE) {

            selectedItem = position;

            holder.relativeLayoutTxtBottom.setVisibility(View.VISIBLE);

            holder.textViewStatic.setText(bookingSensors.getText());

            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));

            holder.relativeLayoutTxtBottom.setVisibility(View.VISIBLE);

        } else {
            holder.relativeLayoutTxtBottom.setVisibility(View.GONE);
        }

        holder.textViewParkingAreaName.setText(bookingSensors.getParkingArea());

        holder.textViewParkingAreaCount.setText(bookingSensors.getCount());

        holder.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");

        holder.rowFG.setOnClickListener(view -> {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                clickListeners.onClick(bookingSensors);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("00.0", new DecimalFormatSymbols(Locale.US));

        double tmp = MathUtils.getInstance().convertToDouble(decimalFormat.format(Double
                .parseDouble(bookingSensors.getDuration())));

        holder.textViewParkingTravelTime.setText(tmp + " mins");

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
            return BookingSensors.TEXT_INFO_TYPE;
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

        @BindView(R.id.textViewParkingAreaName)
        public TextView textViewParkingAreaName;

        @BindView(R.id.textViewParkingAreaCount)
        public TextView textViewParkingAreaCount;

        @BindView(R.id.textViewParkingAreaAddress)
        public TextView textViewParkingAreaAddress;

        @BindView(R.id.textViewParkingDistance)
        public TextView textViewParkingDistance;

        @BindView(R.id.textViewParkingTravelTime)
        public TextView textViewParkingTravelTime;

        @BindView(R.id.textViewStatic)
        public TextView textViewStatic;

        @BindView(R.id.relativeLayoutTxt)
        public RelativeLayout relativeLayoutTxt;

        @BindView(R.id.textBottom)
        public RelativeLayout relativeLayoutTxtBottom;

        @BindView(R.id.rowFG)
        public LinearLayout rowFG;

        public TextBookingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}