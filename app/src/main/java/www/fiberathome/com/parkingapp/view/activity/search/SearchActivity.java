package www.fiberathome.com.parkingapp.view.activity.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.SelectedPlace;
import www.fiberathome.com.parkingapp.model.response.SearchVisitedPlaceResponse;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.view.placesadapter.PlacesAutoCompleteAdapter;

import static www.fiberathome.com.parkingapp.preference.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.preference.AppConstants.NEW_PLACE_SELECTED;

public class SearchActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.ivClearSearchText)
    ImageView ivClearSearchText;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.imageViewCross)
    ImageView imageViewCross;
    @BindView(R.id.recyclerViewSearchPlaces)
    RecyclerView recyclerViewSearchPlaces;
    @BindView(R.id.imageViewSearchPlace)
    ImageView imageViewSearchPlace;
    @BindView(R.id.tvEmptyView)
    TextView tvEmptyView;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);
//        initUI();
        setListeners();

        Places.initialize(getApplicationContext(), context.getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

//        if (searchVisitorDataList != null)
        fetchSearchVisitorPlace(SharedPreManager.getInstance(context).getUser().getMobileNo());

        Timber.e("searchActivity mobileNo -> %s", SharedPreManager.getInstance(context).getUser().getMobileNo());
        editTextSearch.addTextChangedListener(filterTextWatcher);
        editTextSearch.requestFocus();
        editTextSearch.requestLayout();
//        editTextSearch.clearAnimation();
//        editTextSearch.clearFocus();

        setPlacesRecyclerAdapter();
//        if (mAutoCompleteAdapter.getItemCount() == 0) {
//            setNoData();
//        } else if (mAutoCompleteAdapter != null) {
//            hideNoData();
//        }
    }

    private void initUI() {
        editTextSearch = findViewById(R.id.editTextSearch);
        imageViewCross = findViewById(R.id.imageViewCross);
        recyclerViewSearchPlaces = findViewById(R.id.recyclerViewSearchPlaces);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        imageViewCross.setOnClickListener(v -> {
            finish();
        });

        ivClearSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearch.setText("");
                mAutoCompleteAdapter.clearList();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    ivClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s.length() >= 0) {
////                    filter(s.toString());
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                }

                //drawing cross button if text appears programmatically
//                if (s.length() > 0) {
//                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
//                } else {
//                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                }
            }
        });

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String contents = editTextSearch.getText().toString().trim();
                if (contents.length() > 2) {
                    //do search
                    mAutoCompleteAdapter.getFilter().filter(contents);
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    ApplicationUtils.hideKeyboard(context);
                } else
                    //if something to do for empty edittext
                    ApplicationUtils.hideKeyboard(context);
                return true;
            }
            return false;
        });

        //handle special characters
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
            }
        };
        editTextSearch.setFilters(new InputFilter[]{filter});
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
            } else {
                mAutoCompleteAdapter.clearList();
            }
        }
    };

    private void setPlacesRecyclerAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient, searchVisitorDataList);
        recyclerViewSearchPlaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearchPlaces.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewSearchPlaces.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSearchPlaces.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewSearchPlaces, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(recyclerViewSearchPlaces, false);
        mAutoCompleteAdapter.setClickListener(this);
        recyclerViewSearchPlaces.setAdapter(mAutoCompleteAdapter);
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
            String areaAddress = place.getAddress();
            Timber.e("place address -> %s", areaAddress);
            String placeId = place.getId();
            Timber.e("place id -> %s", placeId);
            if (latLng != null && areaName != null) {
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                SelectedPlace selectedplace = new SelectedPlace(placeId, areaName, areaAddress, latitude, longitude);
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
    public void onClick(SearchVisitorData visitorData) {
        Intent resultIntent = new Intent();
        LatLng endLatLng = new LatLng(visitorData.getEndLat(), visitorData.getEndLng());
        String areaName = visitorData.getVisitedArea();
        String placeId = visitorData.getPlaceId();
        if (endLatLng != null && areaName != null) {
            double latitude = endLatLng.latitude;
            double longitude = endLatLng.longitude;
            SearchVisitorData searchVisitorData = new SearchVisitorData(areaName, placeId, latitude, longitude, latitude, longitude);
            // resultIntent.putExtra(NEW_PLACE_SELECTED,place);
            //String result=new Gson().toJson(place);
            resultIntent.putExtra(HISTORY_PLACE_SELECTED, searchVisitorData);
            setResult(RESULT_OK, resultIntent);
            Log.d("ShawnClick", "click: ");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private ArrayList<SearchVisitorData> searchVisitorDataList = new ArrayList<>();
    private List<List<String>> visitedPlaceList = null;
    private List<List<String>> list;
    private SearchVisitedPlaceResponse searchVisitedPlaceResponse;
    private String parkingArea = null;
    private String placeId = null;
    private double endLat = 0.0;
    private double endLng = 0.0;
    private double startLat = 0.0;
    private double startLng = 0.0;


    private void fetchSearchVisitorPlace(String mobileNo) {
        Timber.e("fetchSearchVisitorPlace mobileNo -> %s,", mobileNo);
        Timber.e("fetchSearchVisitorPlace() e dhukche");

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, AppConfig.URL_SEARCH_HISTORY_GET, response -> {
            Timber.e("fetchSearchVisitorPlace() stringRequest e dhukche");
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("visitor_data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SearchVisitorData searchVisitorData = new SearchVisitorData();
                        JSONArray array = jsonArray.getJSONArray(i);
                        if (array != null) {
                            hideNoData();
                            searchVisitorData.setVisitedArea(array.getString(6).trim());
                            searchVisitorData.setEndLat(Double.parseDouble(array.getString(2).trim()));
                            searchVisitorData.setEndLng(Double.parseDouble(array.getString(3).trim()));
                            searchVisitorData.setPlaceId(array.getString(1).trim());

                            searchVisitorDataList.add(searchVisitorData);
                            Timber.e("searchVisitorDataList -> %s", new Gson().toJson(searchVisitorDataList));
                        } else {
                            setNoData();
                        }
                    }
                    setFragmentControls(searchVisitorDataList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Timber.e("response search history is null");
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("jsonObject onErrorResponse get post -> %s", error.getMessage());
                Timber.e("jsonObject onErrorResponse get post -> %s", error.getCause());
//                showMessage(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Timber.e("map e dhukche");
                Map<String, String> params = new HashMap<>();
                params.put("mobile_number", mobileNo);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);

//        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
//        Call<SearchVisitedPlaceResponse> call = request.getVisitorData();
//        call.enqueue(new Callback<SearchVisitedPlaceResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<SearchVisitedPlaceResponse> call, @NotNull Response<SearchVisitedPlaceResponse> response) {
//                Timber.e("onResponse searchHistory-> %s", new Gson().toJson(response.body()));
//
//                if (response.body() != null) {
//                    list = response.body().getVisitorData();
//                    Timber.e("list -> %s", list);
//
//                    searchVisitedPlaceResponse = response.body();
//
//                    visitedPlaceList = searchVisitedPlaceResponse.getVisitorData();
//                    if (visitedPlaceList!=null){
//                        for (List<String> visitedPlaceData : visitedPlaceList) {
//                            for (int i = 0; i < visitedPlaceData.size(); i++) {
//
//                                Log.d(TAG, "onResponse: i=" + i);
//
//                                if (i == 6) {
//                                    parkingArea = visitedPlaceData.get(i);
//                                }
//
//                                if (i == 1) {
//                                    placeId = visitedPlaceData.get(i);
//                                }
//
//                                if (i == 2) {
//                                    endLat = Double.parseDouble(visitedPlaceData.get(i));
//                                }
//
//                                if (i == 3) {
//                                    endLng = Double.parseDouble(visitedPlaceData.get(i));
//                                }
//
//                                if (i == 4) {
//                                    startLat = Double.parseDouble(visitedPlaceData.get(i));
//                                }
//
//                                if (i == 5) {
//                                    startLng = Double.parseDouble(visitedPlaceData.get(i));
//                                }
//                            }
//                            SearchVisitorData searchVisitorData = new SearchVisitorData(parkingArea, placeId, endLat, endLng, startLat, startLng);
//                            searchVisitorDataList.add(searchVisitorData);
//                            Timber.e("searchVisitorData -> %s", new Gson().toJson(searchVisitorData));
//                        }
//                        setFragmentControls(searchVisitorDataList);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<SearchVisitedPlaceResponse> call, @NonNull Throwable t) {
//                Timber.e("onFailure -> %s", t.getMessage());
//                ApplicationUtils.showMessageDialog("Something went wrong...Please try later!", context);
//            }
//        });
    }

    private void setNoData() {
        Timber.e("setNoData te dhukche");
        imageViewSearchPlace.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.VISIBLE);
    }

    private void hideNoData() {
        Timber.e("hideNoData te dhukche");
        imageViewSearchPlace.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);
    }

    public boolean isExist(String strName) {

        for (int i = 0; i < searchVisitorDataList.size(); i++) {
            if (searchVisitorDataList.get(i).equals(strName)) {
//                searchVisitorDataList.remove(i);
//                mAutoCompleteAdapter.notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void setFragmentControls(ArrayList<SearchVisitorData> searchVisitorDataList) {
        Timber.e("setFragmentControls searchActivity e dhukche");
        this.searchVisitorDataList = searchVisitorDataList;
        recyclerViewSearchPlaces.setHasFixedSize(true);
        recyclerViewSearchPlaces.setItemViewCacheSize(20);
        recyclerViewSearchPlaces.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerViewSearchPlaces.setLayoutManager(mLayoutManager);
        recyclerViewSearchPlaces.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewSearchPlaces.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSearchPlaces.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewSearchPlaces, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(recyclerViewSearchPlaces, false);
        mAutoCompleteAdapter = null;
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(context, placesClient, searchVisitorDataList);
        mAutoCompleteAdapter.setClickListener(this);
        recyclerViewSearchPlaces.setAdapter(mAutoCompleteAdapter);
    }
}
