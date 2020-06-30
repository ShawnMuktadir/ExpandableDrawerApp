
package www.fiberathome.com.parkingapp.view.activity.search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.SelcectedPlace;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.view.placesadapter.PlacesAutoCompleteAdapter;
import www.fiberathome.com.parkingapp.utils.OnEditTextRightDrawableTouchListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;

import static www.fiberathome.com.parkingapp.preference.AppConstants.NEW_PLACE_SELECTED;

public class SearchActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {

    private final String TAG = getClass().getSimpleName();
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;
    private Context context;

    EditText editTextSearch;
    ImageView imageViewCross;
    RecyclerView placesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        initUI();
        setListeners();
        Places.initialize(getApplicationContext(), context.getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        editTextSearch.addTextChangedListener(filterTextWatcher);
        editTextSearch.requestFocus();

        setPlacesRecyclerAdapter();
    }

    private void initUI() {
        editTextSearch = findViewById(R.id.searchEdit);
        imageViewCross = findViewById(R.id.imageViewCross);
        placesRecyclerView = findViewById(R.id.placesRv);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        imageViewCross.setOnClickListener(v -> {
            finish();
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
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
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        //handle drawable cross button click listener programmatically
        editTextSearch.setOnTouchListener(
                new OnEditTextRightDrawableTouchListener(editTextSearch) {
                    @Override
                    public void OnDrawableClick() {
                        // The right drawable was clicked. Your action goes here.
                        editTextSearch.setText("");
                        mAutoCompleteAdapter.clearList();
                    }
                });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
//            if (!s.toString().equals("")) {
//                mAutoCompleteAdapter.getFilter().filter(s.toString());
//            }

            mAutoCompleteAdapter.notifyDataSetChanged();

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //!s.toString().equals("")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 2) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                mAutoCompleteAdapter.notifyDataSetChanged();
            }
        }
    };

    private void setPlacesRecyclerAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placesRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        placesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        placesRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, placesRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Movie movie = movieList.get(position);
//                Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(placesRecyclerView, false);
        mAutoCompleteAdapter.setClickListener(this);
        placesRecyclerView.setAdapter(mAutoCompleteAdapter);
    }

    @Override
    public void onClick(Place place) {

        Intent resultIntent = new Intent();
        if (place == null) {
            Timber.e("place null");
            setResult(RESULT_CANCELED, resultIntent);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    finish();
//                }
//            }, 500);
//            overridePendingTransition(0, 0);
        } else {
            Timber.e("place not null");
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
                Log.d("ShawnClick", "click: ");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        finish();
//                    }
//                }, 500);
//                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
