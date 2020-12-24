package www.fiberathome.com.parkingapp.ui.search;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitedPlaceResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.search.placesadapter.PlacesAutoCompleteAdapter;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.NEW_PLACE_SELECTED;

@SuppressLint("NonConstantResourceId")
public class SearchFragment extends BaseFragment implements PlacesAutoCompleteAdapter.ClickListener {

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

    @BindView(R.id.linearLayoutEmptyView)
    LinearLayout linearLayoutEmptyView;

    @BindView(R.id.tvEmptyView)
    TextView tvEmptyView;

    private Unbinder unbinder;

    private SearchActivity context;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    private PlacesClient placesClient;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        context = (SearchActivity) getActivity();
        setListeners();

        //this.getSupportActionBar().hide();

        Places.initialize(context, context.getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(context);

        if (ApplicationUtils.checkInternet(context)) {
            fetchSearchVisitorPlace(SharedPreManager.getInstance(context).getUser().getMobileNo());
            Timber.e("searchActivity mobileNo -> %s", SharedPreManager.getInstance(context).getUser().getMobileNo());
            editTextSearch.addTextChangedListener(filterTextWatcher);
            editTextSearch.requestFocus();
            editTextSearch.requestLayout();
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
        }

        setPlacesRecyclerAdapter();

        if (mAutoCompleteAdapter.getItemCount() == 0) {
            setNoData();
        } else {
            hideNoData();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        imageViewCross.setOnClickListener(v -> {
            startActivity(new Intent(context, HomeActivity.class));
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            context.finish();
        });

        ivClearSearchText.setOnClickListener(view -> {
            editTextSearch.setText("");
            mAutoCompleteAdapter.clearList();
            if (searchVisitorDataList != null) {
                hideNoData();
                fetchSearchVisitorPlace(SharedPreManager.getInstance(context).getUser().getMobileNo());
            } else {
                setNoData();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!ApplicationUtils.checkInternet(context)) {
                    ApplicationUtils.hideKeyboard(context, editTextSearch);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    ivClearSearchText.setVisibility(View.VISIBLE);
                    if (!ApplicationUtils.checkInternet(context)) {
                        ApplicationUtils.hideKeyboard(context, editTextSearch);
                    }
                } else {
                    if (!ApplicationUtils.checkInternet(context)) {
                        ApplicationUtils.hideKeyboard(context, editTextSearch);
                    }
                    ivClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    if (!ApplicationUtils.checkInternet(context)) {
                        ApplicationUtils.hideKeyboard(context, editTextSearch);
                    }
                    if (searchVisitorDataList != null) {
                        hideNoData();
                        fetchSearchVisitorPlace(SharedPreManager.getInstance(context).getUser().getMobileNo());
                    } else {
                        setNoData();
                    }
                } else {
                    if (!ApplicationUtils.checkInternet(context)) {
                        ApplicationUtils.hideKeyboard(context, editTextSearch);
                    }
                }
                /*if (s.length() >= 0) {
                    //filter(s.toString());
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                }

                drawing cross button if text appears programmatically
                if (s.length() > 0) {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }*/
            }
        });

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String contents = editTextSearch.getText().toString().trim();
                if (contents.length() > 0) {
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
    }

    @Override
    public void onClick(Place place) {
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            Intent resultIntent = new Intent();
            if (place == null) {
                Timber.e("place null");
                context.setResult(RESULT_CANCELED, resultIntent);
                /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {*/
                context.finish();
               /* }
                }, 500);
                overridePendingTransition(0, 0);*/
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
                    context.setResult(RESULT_OK, resultIntent);
                    /*Log.d("ShawnClick", "click: ");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                    context.finish();
                    /*}
                }, 500);
                overridePendingTransition(0, 0);*/
                }
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    @Override
    public void onClick(SearchVisitorData visitorData) {
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            Intent resultIntent = new Intent();
            LatLng endLatLng = new LatLng(visitorData.getEndLat(), visitorData.getEndLng());
            String areaName = visitorData.getVisitedArea();
            String placeId = visitorData.getPlaceId();
            if (areaName != null) {
                double latitude = endLatLng.latitude;
                double longitude = endLatLng.longitude;
                SearchVisitorData searchVisitorData = new SearchVisitorData(areaName, placeId, latitude, longitude, latitude, longitude);
                // resultIntent.putExtra(NEW_PLACE_SELECTED,place);
                //String result=new Gson().toJson(place);
                resultIntent.putExtra(HISTORY_PLACE_SELECTED, searchVisitorData);
                context.setResult(RESULT_OK, resultIntent);
                //Log.d("ShawnClick", "click: ");
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                context.finish();
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (context.isFinishing()) {
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        //dismissProgressDialog();
        hideLoading();
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
        //dismissProgressDialog();
        hideLoading();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(editTextSearch).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }*/

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            /*if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
            }*/

            mAutoCompleteAdapter.notifyDataSetChanged();
            if (!ApplicationUtils.checkInternet(context)) {
                ApplicationUtils.hideKeyboard(context, editTextSearch);
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!ApplicationUtils.checkInternet(context)) {
                ApplicationUtils.hideKeyboard(context, editTextSearch);
            }
        }

        //!s.toString().equals("")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                mAutoCompleteAdapter.notifyDataSetChanged();
                if (!ApplicationUtils.checkInternet(context)) {
                    ApplicationUtils.hideKeyboard(context, editTextSearch);
                }
            } else {
                mAutoCompleteAdapter.clearList();
                if (!ApplicationUtils.checkInternet(context)) {
                    ApplicationUtils.hideKeyboard(context, editTextSearch);
                }
            }
        }
    };

    private ArrayList<SearchVisitorData> searchVisitorDataList = new ArrayList<>();

    private SearchVisitorData searchVisitorData;
    private List<List<String>> visitedPlaceList = null;
    private List<List<String>> list;
    private SearchVisitedPlaceResponse searchVisitedPlaceResponse;
    private String parkingArea = null;
    private String placeId = null;
    private double endLat = 0.0;
    private double endLng = 0.0;
    private double startLat = 0.0;
    private double startLng = 0.0;

    private ProgressDialog progressDialog;

    private void fetchSearchVisitorPlace(String mobileNo) {
        Timber.e("fetchSearchVisitorPlace mobileNo -> %s,", mobileNo);
        Timber.e("fetchSearchVisitorPlace() called");

        //progressDialog = ApplicationUtils.progressDialog(context, "Enabling GPS ....");

        showLoading(context);

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, AppConfig.URL_SEARCH_HISTORY_GET, response -> {
            Timber.e("fetchSearchVisitorPlace() stringRequest called");
            //progressDialog.dismiss();
            hideLoading();
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("visitor_data");
                    ArrayList<SearchVisitorData> noRepeat = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        searchVisitorData = new SearchVisitorData();
                        JSONArray array = jsonArray.getJSONArray(i);
                        if (array != null) {
                            hideNoData();
                            searchVisitorData.setVisitedArea(array.getString(6).trim());
                            searchVisitorData.setEndLat(Double.parseDouble(array.getString(2).trim()));
                            searchVisitorData.setEndLng(Double.parseDouble(array.getString(3).trim()));
                            searchVisitorData.setPlaceId(array.getString(1).trim());

                            searchVisitorDataList.add(searchVisitorData);

                            //Removing Duplicates;
                            /*Set<SearchVisitorData> s = new LinkedHashSet<SearchVisitorData>();
                            s.addAll(searchVisitorDataList);
                            //searchVisitorDataList = new ArrayList<SearchVisitorData>();
                            searchVisitorDataList.clear();
                            searchVisitorDataList.addAll(s);*/

                            Timber.e("searchVisitorDataList -> %s", new Gson().toJson(searchVisitorDataList));
                        } else {
                            setNoData();
                        }
                    }

                    /*setFragmentControls(removeDuplicatesSearchVisitorData(searchVisitorDataList));
                    setFragmentControls(getUniqueList(searchVisitorDataList));
                    setFragmentControls(clearListFromDuplicateVisitedArea(searchVisitorDataList));
                    setFragmentControls(noRepeat);*/
                    setFragmentControls(searchVisitorDataList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Timber.e("response search history is null");
            }


        }, error -> {
            Timber.e("jsonObject onErrorResponse get post -> %s", error.getMessage());
            Timber.e("jsonObject onError Cause get post -> %s", error.getCause());
            //showMessage(error.getMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Timber.e("map called");
                Map<String, String> params = new HashMap<>();
                params.put("mobile_number", mobileNo);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);

        //fetch data by retrofit

        /*ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<SearchVisitedPlaceResponse> call = request.getVisitorData();
        call.enqueue(new Callback<SearchVisitedPlaceResponse>() {
            @Override
            public void onResponse(@NotNull Call<SearchVisitedPlaceResponse> call, @NotNull Response<SearchVisitedPlaceResponse> response) {
                Timber.e("onResponse searchHistory-> %s", new Gson().toJson(response.body()));

                if (response.body() != null) {
                    list = response.body().getVisitorData();
                    Timber.e("list -> %s", list);

                    searchVisitedPlaceResponse = response.body();

                    visitedPlaceList = searchVisitedPlaceResponse.getVisitorData();
                    if (visitedPlaceList!=null){
                        for (List<String> visitedPlaceData : visitedPlaceList) {
                            for (int i = 0; i < visitedPlaceData.size(); i++) {

                                Log.d(TAG, "onResponse: i=" + i);

                                if (i == 6) {
                                    parkingArea = visitedPlaceData.get(i);
                                }

                                if (i == 1) {
                                    placeId = visitedPlaceData.get(i);
                                }

                                if (i == 2) {
                                    endLat = Double.parseDouble(visitedPlaceData.get(i));
                                }

                                if (i == 3) {
                                    endLng = Double.parseDouble(visitedPlaceData.get(i));
                                }

                                if (i == 4) {
                                    startLat = Double.parseDouble(visitedPlaceData.get(i));
                                }

                                if (i == 5) {
                                    startLng = Double.parseDouble(visitedPlaceData.get(i));
                                }
                            }
                            SearchVisitorData searchVisitorData = new SearchVisitorData(parkingArea, placeId, endLat, endLng, startLat, startLng);
                            searchVisitorDataList.add(searchVisitorData);
                            Timber.e("searchVisitorData -> %s", new Gson().toJson(searchVisitorData));
                        }
                        setFragmentControls(searchVisitorDataList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchVisitedPlaceResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                ApplicationUtils.showMessageDialog("Something went wrong...Please try later!", context);
            }
        });*/
    }

    private void setFragmentControls(ArrayList<SearchVisitorData> searchVisitorDataList) {
        Timber.e("setFragmentControls searchActivity called");
        this.searchVisitorDataList = searchVisitorDataList;

        recyclerViewSearchPlaces.setHasFixedSize(true);
        recyclerViewSearchPlaces.setItemViewCacheSize(20);
        recyclerViewSearchPlaces.setNestedScrollingEnabled(false);
        recyclerViewSearchPlaces.setMotionEventSplittingEnabled(false);

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

    private void setPlacesRecyclerAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(context, placesClient, searchVisitorDataList);
        recyclerViewSearchPlaces.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSearchPlaces.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewSearchPlaces.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSearchPlaces.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewSearchPlaces, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerViewSearchPlaces.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Timber.e("onChildViewAttachedToWindow tcalled");
                hideNoData();
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Timber.e("onChildViewDetachedFromWindow tcalled");
                //setNoData();
            }
        });
        ViewCompat.setNestedScrollingEnabled(recyclerViewSearchPlaces, false);
        mAutoCompleteAdapter.setClickListener(this);
        recyclerViewSearchPlaces.setAdapter(mAutoCompleteAdapter);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void setNoData() {
        Timber.e("setNoData called");
        imageViewSearchPlace.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
        linearLayoutEmptyView.setVisibility(View.VISIBLE);
    }

    private void hideNoData() {
        Timber.e("hideNoData called");
        imageViewSearchPlace.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);
        linearLayoutEmptyView.setVisibility(View.GONE);
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }

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

    public void selectData(SearchVisitorData data) {
        if (!searchVisitorDataList.contains(searchVisitorData)) {
            searchVisitorDataList.add(searchVisitorData);
            mAutoCompleteAdapter.setData(searchVisitorDataList);
        }
    }

    public boolean checkIfAlreadyExist(SearchVisitorData searchVisitorData) {
        //return searchVisitorDataList.contains(searchVisitorData);
        if (!searchVisitorDataList.contains(searchVisitorData)) {
            searchVisitorDataList.add(searchVisitorData);
            mAutoCompleteAdapter.setData(searchVisitorDataList);
            return true;
        } else {
            return false;
        }
    }

    public boolean isExist(String strName) {
        for (int i = 0; i < searchVisitorDataList.size(); i++) {
            if (searchVisitorDataList.get(i).equals(strName)) {
                searchVisitorDataList.remove(i);
                mAutoCompleteAdapter.notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    private ArrayList<SearchVisitorData> removeDuplicates(ArrayList<SearchVisitorData> list) {
        int count = list.size();

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).equals(list.get(j))) {
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }

    public ArrayList<SearchVisitorData> removeDuplicatesSearchVisitorData(ArrayList<SearchVisitorData> list) {
        // Set set1 = new LinkedHashSet(list);
        Set<SearchVisitorData> set = new TreeSet<SearchVisitorData>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (((SearchVisitorData) o1).getVisitedArea().equalsIgnoreCase(((SearchVisitorData) o2).getVisitedArea())) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);
        final ArrayList<SearchVisitorData> newList = new ArrayList<SearchVisitorData>(set);
        return newList;
    }

    private ArrayList<SearchVisitorData> clearListFromDuplicateVisitedArea(ArrayList<SearchVisitorData> visitedList) {

        Map<String, SearchVisitorData> cleanMap = new LinkedHashMap<String, SearchVisitorData>();
        for (int i = 0; i < visitedList.size(); i++) {
            cleanMap.put(visitedList.get(i).getVisitedArea(), visitedList.get(i));
        }
        ArrayList<SearchVisitorData> list = new ArrayList<SearchVisitorData>(cleanMap.values());
        return list;
    }

    public ArrayList<SearchVisitorData> getUniqueList(ArrayList<SearchVisitorData> alertList) {
        ArrayList<SearchVisitorData> uniqueAlerts = new ArrayList<SearchVisitorData>();
        for (SearchVisitorData alert : alertList) {
            if (!uniqueAlerts.contains(alert)) {
                uniqueAlerts.add(alert);
            }
        }
        return uniqueAlerts;
    }
}
