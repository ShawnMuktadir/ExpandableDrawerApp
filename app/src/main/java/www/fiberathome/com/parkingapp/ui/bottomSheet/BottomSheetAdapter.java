package www.fiberathome.com.parkingapp.ui.bottomSheet;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.TextBookingViewHolder> {

    private final String TAG = getClass().getSimpleName();

    public Context context;

    private HomeFragment homeFragment;

    private ArrayList<BookingSensors> bookingSensorsArrayList;

    public Location location;

    private int selectedItem = -1;

    private int count = 0;

    private AdapterCallback mAdapterCallback;
    private onItemClickListeners clickListeners;

    public BottomSheetAdapter(AdapterCallback callback) {
        this.mAdapterCallback = callback;
    }

    public BottomSheetAdapter(Context context, HomeFragment homeFragment, ArrayList<BookingSensors> sensors,
                              Location onConnectedLocation, AdapterCallback callback, onItemClickListeners clickListeners) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.bookingSensorsArrayList = sensors;
        this.location = onConnectedLocation;
        this.mAdapterCallback = callback;
        this.clickListeners = clickListeners;
    }

    public interface AdapterCallback {
        void onMethodCallback(LatLng latLng);
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
    public void onBindViewHolder(@NonNull TextBookingViewHolder holder, int position) {
        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);

        if (bookingSensors.type == BookingSensors.TEXT_INFO_TYPE) {

            selectedItem = position;

            holder.relativeLayoutTxtBottom.setVisibility(View.VISIBLE);

            holder.textViewStatic.setText(bookingSensors.getText());

            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));

            holder.relativeLayoutTxtBottom.setVisibility(View.VISIBLE);

            //homeFragment.bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));

        } else {
            holder.relativeLayoutTxtBottom.setVisibility(View.GONE);
        }

        holder.textViewParkingAreaName.setText(bookingSensors.getParkingArea());

        holder.textViewParkingAreaCount.setText(bookingSensors.getCount());
        holder.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");

        holder.rowFG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListeners.onClick(bookingSensors);
            }
        });


        DecimalFormat decimalFormat = new DecimalFormat("00.0");

        double tmp = Double.parseDouble(decimalFormat.format(Double
                .parseDouble(bookingSensors.getDuration())));

        holder.textViewParkingTravelTime.setText(String.valueOf(tmp) + " mins");

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
        switch (bookingSensorsArrayList.get(position).type) {
            case 0:
                return BookingSensors.TEXT_INFO_TYPE;
            default:
                return BookingSensors.INFO_TYPE;
        }
    }

    private LatLng origin = null;
    private String fromCurrentLocationDistance;
    private String fromCurrentLocationDuration;
    private Location onConnectedLocation;

    private void getDestinationDurationInfoForSearchLayout(Context context, LatLng latLngDestination, TextBookingViewHolder textBookingViewHolder, int type) {

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }
        // Api Key For Google Direction API
        String serverKey = context.getResources().getString(R.string.google_maps_key);

        origin = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());

        LatLng destination = latLngDestination;

        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        //dismissDialog();
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            textBookingViewHolder.textViewParkingDistance.setText(distance);
                            textBookingViewHolder.textViewParkingTravelTime.setText(duration);
                            fromCurrentLocationDistance = distance;
                            fromCurrentLocationDuration = duration;
                            Timber.e("fromCurrentLocationDistance -> %s", fromCurrentLocationDistance);
                            Timber.e("fromCurrentLocationDuration -> %s", fromCurrentLocationDuration);
                            Timber.e("adapter homeFragment.bottomSheetSearch == 0 called");

                            homeFragment.textViewBottomSheetParkingDistance.setText(fromCurrentLocationDistance);
                            homeFragment.textViewBottomSheetParkingTravelTime.setText(fromCurrentLocationDuration);

                            //------------Displaying Distance and Time-----------------\\
                            //showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
                            Timber.e("duration message -> %s", message);

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(context, "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\
    }

    public void updateData(ArrayList<BookingSensors> bookingSensors) {
        Timber.e("updateData call hoiche");
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(bookingSensors);
        notifyDataSetChanged();
    }

    public void setData(ArrayList<BookingSensors> bookingSensors) {
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(bookingSensors);
        notifyDataSetChanged();
    }

    public void setDataList(ArrayList<BookingSensors> dataList) {
        this.bookingSensorsArrayList = dataList;
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
            /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();*/

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