package www.fiberathome.com.parkingapp.view.parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.data.preference.SharedData;

public class ParkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingFragment parkingFragment;
    private HomeFragment homeFragment;
    private double distance;
    private String duration;
    public LatLng location;
    private Location onConnectedLocation;

    private int selectedPosition = -1;
    private boolean isItemClicked = false;
    private boolean isExpanded = false;

    private onItemClickListener clickListener;

    public ParkingAdapter(Context context, ParkingFragment parkingFragment, HomeFragment homeFragment, ArrayList<SensorArea> sensorAreas, Location onConnectedLocation) {
        this.context = context;
        this.parkingFragment = parkingFragment;
        this.homeFragment = homeFragment;
        this.sensorAreas = sensorAreas;
        this.onConnectedLocation = onConnectedLocation;
//        EventBus.getDefault().register(this);
    }

    public ParkingAdapter(Context context,HomeFragment homeFragment){
        this.context= context;
        this.homeFragment = homeFragment;
    }

    public void setClickListener(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface onItemClickListener {
        void onClick();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ParkingViewHolder parkingViewHolder = (ParkingViewHolder) viewHolder;
        SensorArea sensorArea = sensorAreas.get(position);
        parkingViewHolder.textViewParkingAreaName.setText(ApplicationUtils.capitalize(sensorArea.getParkingArea()));
        parkingViewHolder.textViewParkingAreaCount.setText(sensorArea.getCount());

        distance = ApplicationUtils.distance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                sensorArea.getLat(), sensorArea.getLng());
        sensorArea.setDistance(distance);
        parkingViewHolder.textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
        Timber.e("adapter distance -> %s", parkingViewHolder.textViewParkingDistance.getText());
        sensorArea.setDuration(duration);
        parkingViewHolder.textViewParkingTravelTime.setText(sensorArea.getDuration());

        // Here I am just highlighting the background
        parkingViewHolder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);

        parkingViewHolder.relativeLayout.setOnClickListener(v -> {
            selectedPosition = position;
            try {
                clickListener.onClick();
                notifyDataSetChanged();
                Timber.e("try e dhukche");
            } catch (Exception e) {
                Timber.e("try catch e dhukche -> %s",e.getMessage());
            }
//            homeFragment.updateBottomSheetForParkingAdapter();

            EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorArea.getLat(), sensorArea.getLng())));
//            parkingFragment.layoutVisible(true, sensorArea.getParkingArea(), sensorArea.getCount(), String.valueOf(distance), new LatLng(sensorArea.getLat(), sensorArea.getLng()));

            //data is set in SharedData, to retrieve this data in HomeFragment
            Timber.e("Sensor Area to SharedData -> %s", new Gson().toJson(sensorArea));
            SharedData.getInstance().setSensorArea(sensorArea);
            //Pop the Parking Fragment and Replace it with HomeFragment
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    EventBus.getDefault().post(new SetMarkerEvent(HomeFragment.location));
//                    MainActivity parentActivity = (MainActivity) context;
//                    parentActivity.replaceFragment();
                }
            }, 500);
//            EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(HomeFragment.location));

            if (isItemClicked){
                Timber.e("isItemClicked -> %s",isItemClicked);
                Timber.e("bottomSheet if");
                homeFragment.bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int i) {
                        switch (i) {
                            case BottomSheetBehavior.STATE_HIDDEN:
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:

                                if (homeFragment.mMap != null)
                                    homeFragment.mMap.clear();
                                homeFragment.fetchSensors(onConnectedLocation);

                                final int interval = 100; // 1 Second
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable(){
                                    public void run() {
                                        homeFragment.layoutVisible(false, "", "", "",  null);
                                        Animation animSlideDown = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                                        homeFragment.linearLayoutBottom.startAnimation(animSlideDown);
                                    }
                                };
                                handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                                handler.postDelayed(runnable, interval);

//                                homeFragment.layoutBottomSheetVisible(false, "", "", "", "", null);
//                        btn.setText("Close Sheet");
                            case BottomSheetBehavior.STATE_COLLAPSED:

                                Timber.e("bottom sheet expanded");
                                isExpanded = true;
//                                homeFragment.layoutBottomSheetVisible(false, "", "", "", "", null);
                                if (isExpanded){
                                    homeFragment.layoutVisible(true, parkingViewHolder.textViewParkingAreaName.getText().toString(), parkingViewHolder.textViewParkingAreaCount.getText().toString(),
                                            parkingViewHolder.textViewParkingDistance.getText().toString(),
                                            new LatLng(sensorArea.getLat(), sensorArea.getLng()));
                                    Animation animSlideUp = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                                    homeFragment.linearLayoutBottom.startAnimation(animSlideUp);
                                }
                                break;

                            case BottomSheetBehavior.STATE_DRAGGING:
                                final int interval1 = 100; // 1 Second
                                Handler handler1 = new Handler();
                                Runnable runnable1 = new Runnable(){
                                    public void run() {
                                        homeFragment.layoutVisible(false, "", "", "",  null);
                                        Animation animSlideDown = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                                        homeFragment.linearLayoutBottom.startAnimation(animSlideDown);
                                    }
                                };
                                handler1.postAtTime(runnable1, System.currentTimeMillis()+interval1);
                                handler1.postDelayed(runnable1, interval1);
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:

                                break;
                            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                homeFragment.layoutVisible(false, "", "", "",  null);
                                break;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float slideOffset) {

                    }
                });

                if (isExpanded){
                    Timber.e("isExpanded method e dhukche");
                    homeFragment.layoutBottomSheetVisible(false, "", "", "", "", null);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return sensorAreas.size();
    }

    //parking fragment theke call hoiche
    public void filterList(ArrayList<SensorArea> filteredList) {
        sensorAreas = filteredList;
        notifyDataSetChanged();
    }

    // implements View.OnClickListener
    public static class ParkingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewParkingAreaName)
        TextView textViewParkingAreaName;
        @BindView(R.id.textViewParkingAreaCount)
        TextView textViewParkingAreaCount;
        @BindView(R.id.textViewParkingAreaAddress)
        TextView textViewParkingAreaAddress;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;
        @BindView(R.id.textViewParkingDistance)
        TextView textViewParkingDistance;
        @BindView(R.id.textViewParkingTravelTime)
        TextView textViewParkingTravelTime;
//        @BindView(R.id.view)
//        View view;

        public ParkingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
