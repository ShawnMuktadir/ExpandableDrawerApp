package www.fiberathome.com.parkingapp.ui.bottomSheet;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.BookingSensors;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.SharedData;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.TextBookingViewHolderx> {
    private final String TAG = getClass().getSimpleName();
    public Context context;
    private HomeFragment homeFragment;
    private ArrayList<BookingSensors> bookingSensorsArrayList;
    public BottomSheetSensorAdapter.BookingViewHolder viewHolder;
    public Location location;
    private int selectedItem = -1;
    RecyclerView.ViewHolder holder;
    private View view = null;
    private int count = 0;

    public BottomSheetAdapter(Context context, HomeFragment homeFragment, ArrayList<BookingSensors> sensors, Location onConnectedLocation) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.bookingSensorsArrayList = sensors;
        this.location = onConnectedLocation;
    }

    @NonNull
    @Override
    public TextBookingViewHolderx onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_text_recycler_item, parent, false);
        return new TextBookingViewHolderx(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextBookingViewHolderx holder, int position) {
        BookingSensors bookingSensors = bookingSensorsArrayList.get(position);

        if (bookingSensors.type == BookingSensors.TEXT_INFO_TYPE) {
            //view=holder.itemView;
            count++;
            if (count <= 1) {
                Timber.d("onBindViewHolder: " + count);
                selectedItem = position;
                Timber.d("onBindViewHolder: " + position);
                holder.relativeLayoutxtBotoom.setVisibility(View.VISIBLE);
                holder.textViewStatic.setText(bookingSensors.getText());
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            }

            /*try {
                notifyDataSetChanged();
                //homeFragment.linearLayoutSearchBottomButton.setVisibility(View.GONE);
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }*/

        } else {
            holder.relativeLayoutxtBotoom.setVisibility(View.GONE);
        }

        holder.textViewParkingAreaName.setText(ApplicationUtils.capitalize(bookingSensors.getParkingArea().trim()));
        holder.textViewParkingAreaCount.setText(bookingSensors.getCount());
        holder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(bookingSensors.getDistance()) + " km");
        holder.textViewParkingTravelTime.setText(bookingSensors.getDuration());

        holder.itemView.setOnClickListener(v -> {

            selectedItem = 0;
            Collections.swap(bookingSensorsArrayList, position, 0);
            notifyItemMoved(position, 0);

            if (homeFragment.bottomSheetPlaceLatLng != null) {
                Toast.makeText(context, "Clicked!!!", Toast.LENGTH_SHORT).show();
                homeFragment.bottomSheetPlaceLatLngNearestLocations();
            }

            try {
                notifyDataSetChanged();
                homeFragment.linearLayoutSearchBottomButton.setVisibility(View.GONE);
            } catch (Exception e) {
                Timber.e(e);
            }

            Location homeFragmentOnConnectedLocation = null;
            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                homeFragmentOnConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
                if (homeFragment.mMap != null) {
                    homeFragment.mMap.clear();
                    homeFragment.fetchSensors(homeFragmentOnConnectedLocation);
                }
            }
            getDestinationDurationInfoForSearchLayout(context, new LatLng(bookingSensors.getLat(), bookingSensors.getLng()), holder, bookingSensors.type);
            homeFragment.layoutBottomSheetVisible(true, bookingSensors.getParkingArea(), bookingSensors.getCount(),
                    holder.textViewParkingDistance.getText().toString(), holder.textViewParkingTravelTime.getText().toString(),
                    new LatLng(bookingSensors.getLat(), bookingSensors.getLng()));

            homeFragment.bottomSheetBehavior.setHideable(false);
            homeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            homeFragment.bottomSheetBehavior.setPeekHeight(370);

        });


        if (selectedItem == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            //Toast.makeText(context, "if", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBindViewHolder: gray");
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            //Toast.makeText(context, "else", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBindViewHolder: transparent");
        }
        // holder.itemView.setBackgroundColor(selectedItem == position ? Color.LTGRAY : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return bookingSensorsArrayList.size();
    }


    private LatLng origin = null;
    private String fromCurrentLocationDistance;
    private String fromCurrentLocationDuration;
    private Location onConnectedLocation;

    private void getDestinationDurationInfoForSearchLayout(Context context, LatLng latLngDestination, TextBookingViewHolderx textBookingViewHolder, int type) {

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\

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
//                        dismissDialog();
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            fromCurrentLocationDistance = distance;
                            fromCurrentLocationDuration = duration;
                            Timber.e("fromCurrentLocationDistance -> %s", fromCurrentLocationDistance);
                            Timber.e("fromCurrentLocationDuration -> %s", fromCurrentLocationDuration);
//                            if (homeFragment.bottomSheetSearch == 0) {
                            Timber.e("adapter homeFragment.bottomSheetSearch == 0 e dhukche");
//                            bookingViewHolder.textViewParkingDistance.setText(fromCurrentLocationDistance);
//                            bookingViewHolder.textViewParkingTravelTime.setText(fromCurrentLocationDuration);
//                            }

                            homeFragment.textViewBottomSheetParkingDistance.setText(fromCurrentLocationDistance);
                            homeFragment.textViewBottomSheetParkingTravelTime.setText(fromCurrentLocationDuration);
                            if (type == BookingSensors.TEXT_INFO_TYPE) {

                            } else {
                                // Timber.e("getDestinationDurationInfo duration -> %s", bookingViewHolder.textViewParkingTravelTime.getText().toString());
                            }

                            //------------Displaying Distance and Time-----------------\\
//                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
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

    // implements View.OnClickListener
    public static class TextBookingViewHolderx extends RecyclerView.ViewHolder {

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
//        @BindView(R.id.view)
//        View view;
        @BindView(R.id.relativeLayoutTxt)
        public RelativeLayout relativeLayoutxt;
        @BindView(R.id.textBottom)
        public RelativeLayout relativeLayoutxtBotoom;

        public TextBookingViewHolderx(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
