package www.fiberathome.com.parkingapp.ui.home.search;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static www.fiberathome.com.parkingapp.data.model.data.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.data.model.data.AppConstants.HISTORY_PLACE_SELECTED_OBJ;
import static www.fiberathome.com.parkingapp.data.model.data.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.data.model.data.AppConstants.NEW_PLACE_SELECTED_OBJ;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.search.SearchVisitedPlaceResponse;
import www.fiberathome.com.parkingapp.data.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.data.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.databinding.FragmentSearchBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.search.placesadapter.PlacesAutoCompleteAdapter;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.KeyboardUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class SearchFragment extends BaseFragment implements PlacesAutoCompleteAdapter.ClickListener {
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;

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

    private SearchActivity context;
    private SearchViewModel searchViewModel;
    FragmentSearchBinding binding;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (SearchActivity) getActivity();
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        setListeners();

        if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
            Places.initialize(context, context.getResources().getString(R.string.google_maps_key), new Locale("en"));
        } else {
            Places.initialize(context, context.getResources().getString(R.string.google_maps_key), new Locale("bn"));
        }

        placesClient = Places.createClient(context);

        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchSearchedDestinationPlace(Preferences.getInstance(context).getUser().getMobileNo());
            binding.editTextSearch.addTextChangedListener(filterTextWatcher);
            binding.editTextSearch.requestFocus();
            binding.editTextSearch.requestLayout();
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
        }
        setPlacesRecyclerAdapter();
        if (mAutoCompleteAdapter.getItemCount() == 0) {
            setNoData();
        } else {
            hideNoData();
        }
    }

    @Override
    public void onClick(Place place) {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
            Intent resultIntent = new Intent();
            if (place == null) {
                ////Timber.e("place null");
                context.setResult(RESULT_CANCELED, resultIntent);
                context.finish();
            } else {
                //Timber.e("place not null");
                String areaName;
                LatLng latLng = place.getLatLng();
                if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                    areaName = place.getName();
                } else {
                    areaName = place.getName();
                }

                String areaAddress = place.getAddress();
                //Timber.e("place address -> %s", areaAddress);
                String placeId = place.getId();
                //Timber.e("place id -> %s", placeId);
                if (latLng != null && areaName != null) {
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    SelectedPlace selectedplace = new SelectedPlace(placeId, areaName, areaAddress, latitude, longitude);
                    resultIntent.putExtra(NEW_PLACE_SELECTED_OBJ, selectedplace);
                    resultIntent.putExtra(NEW_PLACE_SELECTED, NEW_PLACE_SELECTED);
                    context.setResult(RESULT_OK, resultIntent);
                    context.finish();
                }
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
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
        hideLoading();
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
        hideLoading();
    }

    @Override
    public void onClick(SearchVisitorData visitorData) {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
            Intent resultIntent = new Intent();
            LatLng endLatLng = new LatLng(visitorData.getEndLat(), visitorData.getEndLng());
            String areaName = visitorData.getVisitedArea();
            String placeId = visitorData.getPlaceId();
            if (areaName != null) {
                double latitude = endLatLng.latitude;
                double longitude = endLatLng.longitude;
                SearchVisitorData searchVisitorData = new SearchVisitorData(areaName, placeId, latitude, longitude, latitude, longitude);
                resultIntent.putExtra(HISTORY_PLACE_SELECTED_OBJ, searchVisitorData);
                resultIntent.putExtra(HISTORY_PLACE_SELECTED, HISTORY_PLACE_SELECTED);
                context.setResult(RESULT_OK, resultIntent);
                context.finish();
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private final TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            mAutoCompleteAdapter.notifyDataSetChanged();
            if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
            }
        }

        //!s.toString().equals("")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                mAutoCompleteAdapter.notifyDataSetChanged();
            } else {
                mAutoCompleteAdapter.clearList();
            }
            if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
            }
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void setListeners() {
        binding.imageViewCross.setOnClickListener(v -> {
            startActivity(new Intent(context, HomeActivity.class));
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            context.finish();
        });

        binding.ivClearSearchText.setOnClickListener(view -> {
            binding.editTextSearch.setText("");
            mAutoCompleteAdapter.clearList();
            if (searchVisitorDataList != null) {
                hideNoData();
                updateAdapter();
            } else {
                setNoData();
            }
        });

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                    KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    binding.ivClearSearchText.setVisibility(View.VISIBLE);
                    if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
                    }
                } else {
                    if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
                    }
                    binding.ivClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
                    }
                    if (searchVisitorDataList != null) {
                        hideNoData();
                        updateAdapter();
                    } else {
                        setNoData();
                    }
                } else {
                    if (!ConnectivityUtils.getInstance().checkInternet(context)) {
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearch);
                    }
                }
            }
        });

        binding.editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String contents = binding.editTextSearch.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    mAutoCompleteAdapter.getFilter().filter(contents);
                    mAutoCompleteAdapter.notifyDataSetChanged();
                }
                //if something to do for empty edittext
                KeyboardUtils.getInstance().hideKeyboard(context);
                return true;
            }
            return false;
        });
    }

    private void fetchSearchedDestinationPlace(String mobileNo) {
        showLoading(context);

        searchViewModel.getSearchHistoryInit(mobileNo);
        searchViewModel.getSearchHistoryMutableData().observe(requireActivity(), (@NonNull SearchVisitedPlaceResponse response) -> {
            searchVisitedPlaceResponse = response;
            if (!response.getError()) {
                list = searchVisitedPlaceResponse.getVisitorData();
                hideLoading();
                visitedPlaceList = searchVisitedPlaceResponse.getVisitorData();
                if (visitedPlaceList != null) {
                    for (List<String> visitedPlaceData : visitedPlaceList) {
                        for (int i = 0; i < visitedPlaceData.size(); i++) {

                            //Timber.e("onResponse: i= -> %s", i);

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
                    }
                    if (isAdded()) {
                        setFragmentControls(searchVisitorDataList);
                    }
                }
            } else {
                Timber.e("response -> %s", new Gson().toJson(response));
            }
        });
    }

    private void setFragmentControls(ArrayList<SearchVisitorData> searchVisitorDataList) {
        this.searchVisitorDataList = searchVisitorDataList;
        binding.recyclerViewSearchPlaces.setHasFixedSize(true);
        binding.recyclerViewSearchPlaces.setNestedScrollingEnabled(false);
        binding.recyclerViewSearchPlaces.setMotionEventSplittingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerViewSearchPlaces.setLayoutManager(mLayoutManager);
        binding.recyclerViewSearchPlaces.setItemAnimator(new DefaultItemAnimator());
        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewSearchPlaces, false);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(context, placesClient, searchVisitorDataList);
        mAutoCompleteAdapter.setClickListener(this);
        mAutoCompleteAdapter.setDataList(searchVisitorDataList);
        binding.recyclerViewSearchPlaces.setAdapter(mAutoCompleteAdapter);
    }

    private void setPlacesRecyclerAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(context, placesClient, searchVisitorDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerViewSearchPlaces.setLayoutManager(mLayoutManager);
        binding.recyclerViewSearchPlaces.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewSearchPlaces.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Timber.e("onChildViewAttachedToWindow called");
                hideNoData();
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Timber.e("onChildViewDetachedFromWindow called");
                //setNoData();
            }
        });

        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewSearchPlaces, false);
        mAutoCompleteAdapter.setClickListener(this);
        binding.recyclerViewSearchPlaces.setAdapter(mAutoCompleteAdapter);
    }

    private void updateAdapter() {
        if (searchVisitorDataList != null) {
            if (mAutoCompleteAdapter != null) {
                mAutoCompleteAdapter = null;
            }
            setPlacesRecyclerAdapter();
        } else {
            setNoData();
        }
    }

    private void setNoData() {
        Timber.e("setNoData called");
        binding.imageViewSearchPlace.setVisibility(View.GONE);
        binding.tvEmptyView.setVisibility(View.VISIBLE);
        binding.linearLayoutEmptyView.setVisibility(View.VISIBLE);
    }

    private void hideNoData() {
        Timber.e("hideNoData called");
        binding.imageViewSearchPlace.setVisibility(View.GONE);
        binding.tvEmptyView.setVisibility(View.GONE);
        binding.linearLayoutEmptyView.setVisibility(View.GONE);
    }
}
