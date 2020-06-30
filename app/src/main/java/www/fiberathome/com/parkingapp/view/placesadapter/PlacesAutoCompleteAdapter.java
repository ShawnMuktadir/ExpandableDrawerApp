package www.fiberathome.com.parkingapp.view.placesadapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
    private  final String TAG =getClass().getSimpleName();
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();

    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private ClickListener clickListener;
    private AutocompleteSessionToken token;

    private int selectedPosition = -1;

    public PlacesAutoCompleteAdapter(Context context, PlacesClient placesClient) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        token = AutocompleteSessionToken.newInstance();
        this.placesClient = placesClient;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(Place place);
    }

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
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
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
//                .setTypeFilter(TypeFilter.ADDRESS)
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
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.search_list_item_location, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int position) {
        mPredictionHolder.address.setText(mResultList.get(position).address);
        mPredictionHolder.area.setText(mResultList.get(position).area);

        // Here I am just highlighting the background
        mPredictionHolder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);
        mPredictionHolder.itemView.setOnClickListener(v -> {

            selectedPosition = position;
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                Timber.e(e);
            }
            PlaceAutocomplete item;
            Log.d(TAG, "List size :"+mResultList.size());
            Log.d(TAG, "position :"+selectedPosition);
//            Toast.makeText(mContext,"position:"+position,Toast.LENGTH_SHORT).show();
//            for (PlaceAutocomplete autoComplete:mResultList) {
//                Log.d(TAG, "onBindViewHolder: "+autoComplete);
//            }

                try {
                    item = getItem(selectedPosition);//mResultList.get(selectedPosition);
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
                            }, 1000);
                        }).addOnFailureListener(exception -> {
                            if (exception instanceof ApiException) {
                                Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(mContext, "Please try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "exception: "+e);
                }

//            throw new RuntimeException("Test Crash");
        });
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public static class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address, area;
        private LinearLayout mRow;

        PredictionHolder(View itemView) {

            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
            mRow = itemView.findViewById(R.id.item_view);
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

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public static class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

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

    public void clearList(){
        mResultList.clear();
        notifyDataSetChanged();
    }
}
