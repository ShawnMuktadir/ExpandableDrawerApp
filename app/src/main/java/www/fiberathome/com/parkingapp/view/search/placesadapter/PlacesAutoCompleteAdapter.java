package www.fiberathome.com.parkingapp.view.search.placesadapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Handler;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.parking.EmptyViewHolder;

import static android.content.Context.LOCATION_SERVICE;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final String TAG = getClass().getSimpleName();
    public ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private PlacesClient placesClient;
    private ClickListener clickListener;
    private AutocompleteSessionToken token;
    public ArrayList<SearchVisitorData> searchVisitorDataList = new ArrayList<>();

    private int selectedPosition = -1;
    private static final int VIEW_TYPE_PLACE = 0;
    private static final int VIEW_TYPE_HISTORY = 1;
    private static final int VIEW_TYPE_EMPTY = 2;
    private boolean isShownEmpty = false;

    public PlacesAutoCompleteAdapter(Context context, PlacesClient placesClient, ArrayList<SearchVisitorData> searchVisitorDataList) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        token = AutocompleteSessionToken.newInstance();
        this.placesClient = placesClient;
        this.searchVisitorDataList = searchVisitorDataList;
    }

    public PlacesAutoCompleteAdapter(Context context, ArrayList<SearchVisitorData> searchVisitorDataList) {
        this.mContext = context;
        this.searchVisitorDataList = searchVisitorDataList;
    }

    CharSequence charSequence;

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    charSequence = constraint;
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                        isShownEmpty = false;
                    }

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = (ArrayList<PlaceAutocomplete>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
//                    notifyDataSetInvalidated();
                    searchVisitorDataList.clear();
                    notifyDataSetChanged();
                    isShownEmpty = true;
//                    Toast.makeText(mContext, "No Places found!", Toast.LENGTH_SHORT).show();
//                    final Toast toast = Toast.makeText(mContext, "No Places found!", Toast.LENGTH_SHORT);
                    Toast toast = TastyToast.makeText(mContext, "No Places found!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 700);
//                    TastyToastUtils.showTastyErrorToast(mContext, "No Places found!");
                }
            }
        };
    }

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
//                .setCountry("IN")
//                .setTypeFilter(TypeFilter.REGIONS)
                .setSessionToken(token)
                .setCountry("BD")
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                }

            return resultList;
        } else {
            return resultList;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.search_list_item_location, parent, false);
//        return new PredictionHolder(convertView);

        View itemView;
        if (viewType == VIEW_TYPE_PLACE) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item_location, parent, false);
            return new SearchPredictionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HISTORY) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_list_item_location, parent, false);
            return new SearchHistoryViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_empty, parent, false);
            return new EmptyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof SearchPredictionViewHolder) {
            SearchPredictionViewHolder mSearchPredictionViewHolder = (SearchPredictionViewHolder) viewHolder;
            if (charSequence != null) {
                mSearchPredictionViewHolder.address.setText(mResultList.get(position).address);
                mSearchPredictionViewHolder.area.setText(mResultList.get(position).area);
            }
            // Here I am just highlighting the background
            mSearchPredictionViewHolder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);

            mSearchPredictionViewHolder.itemView.setOnClickListener(v -> {

                if (isGPSEnabled(position) && ApplicationUtils.checkInternet(mContext)) {
                    selectedPosition = position;
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    PlaceAutocomplete item;
                    Log.d(TAG, "List size :" + mResultList.size());
                    Log.d(TAG, "position :" + selectedPosition);
//            Toast.makeText(mContext,"position:"+position,Toast.LENGTH_SHORT).show();
//            for (PlaceAutocomplete autoComplete:mResultList) {
//                Log.d(TAG, "onBindViewHolder: "+autoComplete);
//            }

                    try {
                        item = getItem(selectedPosition);   //mResultList.get(selectedPosition);
                        if (v.getId() == R.id.item_view) {

                            String placeId = String.valueOf(item.placeId);
                            Timber.e("placeId -> %s", placeId);

                            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).setSessionToken(token).build();
                            placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                                Place place = response.getPlace();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        clickListener.onClick(place);
                                    }
                                }, 100);
                            }).addOnFailureListener(exception -> {
                                if (exception instanceof ApiException) {
                                    Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IndexOutOfBoundsException e) {
                        //Toast.makeText(mContext, "Please try again", Toast.LENGTH_SHORT).show();
                        ApplicationUtils.showMessageDialog("Please try again!", mContext);
                        Log.d(TAG, "exception: " + e);
                        //throw new RuntimeException("Test Crash");
                    }
                } else {
                    TastyToastUtils.showTastyWarningToast(mContext, mContext.getResources().getString(R.string.connect_to_internet_gps));
                }
            });
        } else if (viewHolder instanceof SearchHistoryViewHolder) {
            SearchHistoryViewHolder searchHistoryViewHolder = (SearchHistoryViewHolder) viewHolder;
            final SearchVisitorData visitorData = searchVisitorDataList.get(position);

            if (visitorData != null) {
                searchHistoryViewHolder.textViewHistoryArea.setText(visitorData.getVisitedArea());
                searchHistoryViewHolder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);
                searchHistoryViewHolder.itemView.setOnClickListener(v -> {
                    if (isGPSEnabled(position) && ApplicationUtils.checkInternet(mContext)) {
                        selectedPosition = position;
                        try {
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            Timber.e(e);
                        }

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clickListener.onClick(visitorData);
                            }
                        }, 100);
                    } else {
                        TastyToastUtils.showTastyWarningToast(mContext, mContext.getResources().getString(R.string.connect_to_internet_gps));
                    }
                });
            } else {
                setNoData(searchHistoryViewHolder);
            }
        } else {
            Timber.e("EmptyViewHolder -> POSITION:: %s", position);
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) viewHolder;
            if (searchVisitorDataList.isEmpty()) {
                emptyViewHolder.imageViewSearchPlace.setVisibility(View.VISIBLE);
                emptyViewHolder.tvEmptyView.setVisibility(View.VISIBLE);
            } else {
                emptyViewHolder.imageViewSearchPlace.setVisibility(View.GONE);
                emptyViewHolder.tvEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (!mResultList.isEmpty()) {
            Timber.e("getItemCount if");
            return mResultList.size();
        } else if (!searchVisitorDataList.isEmpty()) {
            Timber.e("getItemCount else if");
            if (searchVisitorDataList.size() > 10) {
                return 10;
            } else {
                return searchVisitorDataList.size();
            }
        } else {
            Timber.e("getItemCount else ");
            return mResultList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (searchVisitorDataList.size() == 0 && mResultList.isEmpty()) {
            Timber.e("getItemViewType if");
            return VIEW_TYPE_EMPTY;
        } else if (mResultList.size() == 0) {
            Timber.e("getItemViewType else if");
            return VIEW_TYPE_HISTORY;
        } else {
            Timber.e("getItemViewType else");
            return VIEW_TYPE_PLACE;
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setData(ArrayList<SearchVisitorData> searchVisitorDataList) {
        this.searchVisitorDataList = searchVisitorDataList;
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(Place place);

        void onClick(SearchVisitorData visitorData);
    }

    private boolean isGPSEnabled(int position) {

        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
//            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                    .setTitle("GPS Permissions")
//                    .setMessage("GPS is required for this app to work. Please enable GPS.")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        intent.putExtra("position",position);
//                        Activity origin = (Activity)context;
//
//                        origin.startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();
        }

        return false;
    }

    public boolean isExist(String strName) {
        for (int i = 0; i < searchVisitorDataList.size(); i++) {
            if (searchVisitorDataList.get(i).equals(strName)) {
                searchVisitorDataList.remove(i);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    private void setNoData(SearchPredictionViewHolder mSearchPredictionViewHolder) {
        mSearchPredictionViewHolder.imageViewSearchPlace.setVisibility(View.VISIBLE);
        mSearchPredictionViewHolder.textViewNoData.setVisibility(View.VISIBLE);
    }

    private void setNoData(SearchHistoryViewHolder searchHistoryViewHolder) {
        searchHistoryViewHolder.imageViewSearchPlace.setVisibility(View.GONE);
        searchHistoryViewHolder.textViewNoData.setVisibility(View.VISIBLE);
    }

    public void selectData(SearchVisitorData data) {
        searchVisitorDataList.remove(data);
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            e.getCause();
        }
    }

    public boolean checkIfAlreadySelected(SearchVisitorData searchVisitorData) {
        return searchVisitorDataList.contains(searchVisitorData);
    }

    private PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public static class SearchPredictionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address, area;
        private LinearLayout mRow;
        @BindView(R.id.imageViewSearchPlace)
        ImageView imageViewSearchPlace;
        @BindView(R.id.textViewNoData)
        TextView textViewNoData;

        SearchPredictionViewHolder(View itemView) {

            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
            mRow = itemView.findViewById(R.id.item_view);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
//            if (v.getId() == R.id.item_view) {
//
//                String placeId = String.valueOf(item.placeId);
//
//                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
//                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).setSessionToken(token).build();
//                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//                    @Override
//                    public void onSuccess(FetchPlaceResponse response) {
//                        Place place = response.getPlace();
//                        clickListener.click(place);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        if (exception instanceof ApiException) {
//                            Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
        }
    }

    public static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewHistoryArea)
        TextView textViewHistoryArea;
        @BindView(R.id.textViewHistoryAddress)
        TextView textViewHistoryAddress;
        @BindView(R.id.circleImageView)
        ImageView circleImageView;
        @BindView(R.id.imageViewSearchPlace)
        ImageView imageViewSearchPlace;
        @BindView(R.id.textViewNoData)
        TextView textViewNoData;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public static class PlaceAutocomplete {

        private CharSequence placeId;
        private CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @NonNull
        @Override
        public String toString() {
            return area.toString();
        }
    }

    public void clearList() {
        mResultList.clear();
        notifyDataSetChanged();
    }
}
