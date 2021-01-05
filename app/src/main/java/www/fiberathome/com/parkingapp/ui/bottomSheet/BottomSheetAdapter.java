package www.fiberathome.com.parkingapp.ui.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
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

    public BottomSheetAdapter(AdapterCallback callback) {
        this.mAdapterCallback = callback;
    }

    public BottomSheetAdapter(Context context, HomeFragment homeFragment, ArrayList<BookingSensors> sensors,
                              Location onConnectedLocation, AdapterCallback callback) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.bookingSensorsArrayList = sensors;
        this.location = onConnectedLocation;
        this.mAdapterCallback = callback;
    }

    public interface AdapterCallback {
        void onMethodCallback(LatLng latLng);

        void onMethodCallback(@NonNull TextBookingViewHolder holder);
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

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                // this will give you cell width dynamically
                int cellWidth = holder.itemView.getWidth();
                Timber.e("cellWidth -> %s", cellWidth);
                // this will give you cell height dynamically
                int cellHeight = holder.itemView.getHeight();
                Timber.e("cellHeight -> %s", cellHeight);
            }
        });

        if (bookingSensors.type == BookingSensors.TEXT_INFO_TYPE) {
            //view=holder.itemView;
            count++;
            if (count <= 1) {
                Timber.d("onBindViewHolder: -> %s", count);

                selectedItem = position;

                Timber.d("onBindViewHolder position -> %s", position);

                holder.relativeLayoutTxtBottom.setVisibility(View.VISIBLE);

                holder.textViewStatic.setText(bookingSensors.getText());

                //holder.itemView.setBackgroundColor(Color.LTGRAY);

                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));

                homeFragment.bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));

                ApplicationUtils.setMargins(holder.relativeLayoutTxt, 0, 0, 0, 0);
            }
        }
        else {
            holder.relativeLayoutTxtBottom.setVisibility(View.GONE);

            holder.itemView.requestLayout();

            ApplicationUtils.setMargins(holder.relativeLayoutTxt, 0, 0, 0, 0);
        }

        holder.textViewParkingAreaName.setText(bookingSensors.getParkingArea());

        holder.textViewParkingAreaCount.setText(bookingSensors.getCount());
        //holder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(bookingSensors.getDistance()) + " km");
        //holder.textViewParkingDistance.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");

        holder.textViewParkingDistance.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bookingSensors.getDistance()) + " km");

        DecimalFormat decimalFormat = new DecimalFormat("00.0");

        double tmp = Double.parseDouble(decimalFormat.format(Double
                .parseDouble(bookingSensors.getDuration())));
        holder.textViewParkingTravelTime.setText(String.valueOf(tmp) + " mins");
        //holder.textViewParkingTravelTime.setText(bookingSensors.getDuration());

        holder.itemView.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (homeFragment.fromRouteDrawn == 0) {
                    selectedItem = 0;
                    if (bookingSensorsArrayList != null && !bookingSensorsArrayList.isEmpty()) {
                        Collections.swap(bookingSensorsArrayList, position, 0);
                        notifyItemMoved(position, 0);
                        homeFragment.bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                    }

                    try {
                        notifyDataSetChanged();
                        homeFragment.linearLayoutSearchBottomButton.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Timber.e(e);
                    }

                    if (SharedData.getInstance().getOnConnectedLocation() != null) {
                        Location homeFragmentOnConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
                        if (homeFragment.mMap != null) {
                            homeFragment.mMap.clear();
                            if (ApplicationUtils.checkInternet(context)) {
                                homeFragment.fetchSensors(homeFragmentOnConnectedLocation);
                            } else {
                                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                                    Timber.e("Positive Button clicked");
                                    if (ApplicationUtils.checkInternet(context)) {
                                        homeFragment.fetchSensors(onConnectedLocation);
                                        //fetchBottomSheetSensors(onConnectedLocation);
                                    } else {
                                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                    }
                                }, (dialog, which) -> {
                                    Timber.e("Negative Button Clicked");
                                    dialog.dismiss();
                                    if (context != null) {
                                        ((Activity) context).finish();
                                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                    }
                                });
                            }
                            mAdapterCallback.onMethodCallback(new LatLng(bookingSensors.getLat(), bookingSensors.getLng()));
                            mAdapterCallback.onMethodCallback(holder);
                        }
                    }

                    homeFragment.removeCircle();

                    homeFragment.bottomSheetBehavior.setHideable(false);

                    homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    getDestinationDurationInfoForSearchLayout(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), holder, bookingSensors.type);

                    homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
                            holder.textViewParkingDistance.getText().toString(), holder.textViewParkingTravelTime.getText().toString(),
                            new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), true);

                    homeFragment.bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                } else {
                    homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    homeFragment.bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                    //ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.parking_spot_selection_rules), context);
                    ApplicationUtils.showAlertDialog(context.getString(R.string.you_have_to_exit_from_current_destination), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {
                        Timber.e("Positive Button clicked");
                        homeFragment.commonBackOperations();

                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                    });
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }

        });

        if (selectedItem == position) {
            //holder.itemView.setBackgroundColor(Color.LTGRAY);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
            //Toast.makeText(context, "if", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "onBindViewHolder: gray");
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            //Toast.makeText(context, "else", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "onBindViewHolder: transparent");
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
            case 1:
                return BookingSensors.INFO_TYPE;
            default:
                return -1;
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
                            /*if (type == BookingSensors.TEXT_INFO_TYPE) {

                            } else {
                                // Timber.e("getDestinationDurationInfo duration -> %s", bookingViewHolder.textViewParkingTravelTime.getText().toString());
                            }*/

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

    private void resizeView(View view, int newWidth, int newHeight) {
        try {
            Constructor<? extends ViewGroup.LayoutParams> ctor = view.getLayoutParams().getClass().getDeclaredConstructor(int.class, int.class);
            view.setLayoutParams(ctor.newInstance(newWidth, newHeight));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rowSize (@NonNull TextBookingViewHolder holder) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.relativeLayoutTxtBottom.getLayoutParams();
        params.height = 0;
        holder.relativeLayoutTxtBottom.setLayoutParams(params);
    }

    public void updateData(ArrayList<BookingSensors> bookingSensors) {
        Timber.e("updateData call hoiche");
        bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(bookingSensors);
        notifyDataSetChanged();
    }

    public void setData(ArrayList<BookingSensors> bookingSensors) {
        this.bookingSensorsArrayList.clear();
        bookingSensorsArrayList.addAll(bookingSensors);
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

    // implements View.OnClickListener
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