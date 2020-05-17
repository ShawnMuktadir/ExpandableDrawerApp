
package www.fiberathome.com.parkingapp.ui.activity.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.SelcectedPlace;
import www.fiberathome.com.parkingapp.ui.placesadapter.PlacesAutoCompleteAdapter;
import www.fiberathome.com.parkingapp.utils.OnEditTextRightDrawableTouchListener;

import static www.fiberathome.com.parkingapp.preference.AppConstants.NEW_PLACE_SELECTED;

public class SearchActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        EditText searchEdit = findViewById(R.id.searchEdit);
        ImageView imageViewCross = findViewById(R.id.imageViewCross);
        RecyclerView placesRv = findViewById(R.id.placesRv);

        searchEdit.addTextChangedListener(filterTextWatcher);
        searchEdit.requestFocus();
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient);
        placesRv.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        placesRv.setAdapter(mAutoCompleteAdapter);

        imageViewCross.setOnClickListener(v -> {
            finish();
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                if (charSequence.length() > 0) {
//                    ivClearSearchText.setVisibility(View.VISIBLE);
//                } else {
//                    ivClearSearchText.setVisibility(View.GONE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s.length() >= 0) {
////                    filter(s.toString());
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                }

                //drawing cross button if text appears programmatically
                if (s.length() > 0) {
                    searchEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    searchEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        //handle drawable cross button click listener programmatically
        searchEdit.setOnTouchListener(
                new OnEditTextRightDrawableTouchListener(searchEdit) {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void OnDrawableClick() {
                        // The right drawable was clicked. Your action goes here.
                        searchEdit.setText("");
                    }
                });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };


    @Override
    public void click(Place place) {

        Intent resultIntent = new Intent();

        if (place == null) {
            setResult(RESULT_CANCELED, resultIntent);
        } else {
            LatLng latLng = place.getLatLng();
            String areaName = place.getName();
            if (latLng != null && areaName != null) {
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                SelcectedPlace selectedplace = new SelcectedPlace(areaName, latitude, longitude);
                // resultIntent.putExtra(NEW_PLACE_SELECTED,place);
                //String result=new Gson().toJson(place);
                resultIntent.putExtra(NEW_PLACE_SELECTED, selectedplace);
                setResult(RESULT_OK, resultIntent);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        }, 1000);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
