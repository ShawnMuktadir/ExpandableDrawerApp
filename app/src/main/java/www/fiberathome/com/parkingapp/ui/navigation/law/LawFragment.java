package www.fiberathome.com.parkingapp.ui.navigation.law;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.law.LawData;
import www.fiberathome.com.parkingapp.databinding.FragmentLawBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.KeyboardUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class LawFragment extends BaseFragment implements IOnBackPressListener {

    private List<List<String>> lawList = null;
    private String lawTitle = null;
    private String lawDescription = null;
    private final ArrayList<LawData> lawDataArrayList = new ArrayList<>();

    FragmentLawBinding binding;
    private LawActivity context;
    private LawAdapter lawAdapter;
    private LawViewModel lawViewModel;

    public LawFragment() {
        // Required empty public constructor
    }

    public static LawFragment newInstance() {
        return new LawFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLawBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (LawActivity) getActivity();
        lawViewModel = new ViewModelProvider(this).get(LawViewModel.class);
        setListeners();
        fetchParkingLaws();
    }

    @Override
    public boolean onBackPressed() {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private void fetchParkingLaws() {
        showLoading(context);

        lawViewModel.initFetchParkingLaws();
        lawViewModel.getParkingLawsResponseMutableLiveData().observe(context, lawResponse -> {
            hideLoading();
            if (lawResponse != null) {
                lawList = lawResponse.getParkingLaw();
                if (lawList != null) {
                    for (List<String> baseStringList : lawList) {
                        for (int i = 0; i < baseStringList.size(); i++) {

                            if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                                if (i == 1) {
                                    lawTitle = baseStringList.get(i);
                                }
                            } else {
                                if (i == 2) {
                                    lawTitle = baseStringList.get(i);
                                }
                            }

                            if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                                if (i == 3) {
                                    lawDescription = baseStringList.get(i);
                                }
                            } else {
                                if (i == 4) {
                                    lawDescription = baseStringList.get(i);
                                }
                            }
                        }

                        LawData lawData = new LawData(lawTitle, lawDescription);
                        lawDataArrayList.add(lawData);
                    }
                    setFragmentControls(lawDataArrayList);
                }
            }
        });
    }

    private void setFragmentControls(ArrayList<LawData> lawData) {
        binding.recyclerView.setHasFixedSize(false);
        binding.recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        ViewCompat.setNestedScrollingEnabled(binding.recyclerView, false);
        setAdapter(lawData);
    }

    private void setAdapter(ArrayList<LawData> lawData) {
        lawAdapter = new LawAdapter(context, lawData);
        binding.recyclerView.setAdapter(lawAdapter);
    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void setListeners() {
        binding.ivClearSearchText.setOnClickListener(view -> {
            binding.editTextSearchLaw.setText("");
            hideNoData();
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                updateAdapter();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });

        binding.editTextSearchLaw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.ivClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    binding.ivClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) &&
                        TextUtils.getInstance().textContainsBangla(s.toString())) {
                    setNoDataForBangla();
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.editTextSearchLaw.setText("");
                } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) &&
                        TextUtils.getInstance().textContainsEnglish(s.toString())) {
                    setNoDataForEnglish();
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.editTextSearchLaw.setText("");
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    filter(s.toString().trim());
                    lawAdapter.notifyDataSetChanged();
                }

                if (s.length() == 0) {
                    Timber.e("length 0 called");
                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                        updateAdapter();
                    } else {
                        Timber.e("else length 0 called");
                        //ToastUtils.getInstance().showToastMessage(context, "Please connect to internet");
                    }
                }
            }
        });

        binding.editTextSearchLaw.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String contents = binding.editTextSearchLaw.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    if (ConnectivityUtils.getInstance().checkInternet(context) && ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                        if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) &&
                                TextUtils.getInstance().textContainsBangla(contents)) {
                            setNoDataForBangla();
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.editTextSearchLaw.setText("");
                        } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) &&
                                TextUtils.getInstance().textContainsEnglish(contents)) {
                            setNoDataForEnglish();
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.editTextSearchLaw.setText("");
                        } else {
                            filter(contents);
                            lawAdapter.notifyDataSetChanged();
                            KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearchLaw);
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                } else {
                    //if something to do for empty edittext
                    if (ConnectivityUtils.getInstance().checkInternet(context) && ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                        updateAdapter();
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearchLaw);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context,
                                context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void filter(String text) {
        ArrayList<LawData> filteredList = new ArrayList<>();
        if (ConnectivityUtils.getInstance().checkInternet(context) && ConnectivityUtils.getInstance().isGPSEnabled(context)) {

            if (!lawDataArrayList.isEmpty()) {
                for (LawData item : lawDataArrayList) {
                    if (item.getTitle().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                        hideNoData();
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {
                    setNoData();
                } else {
                    hideNoData();
                }
                lawAdapter.filterList(filteredList);
            } else {
                Timber.e("sensorAreas is empty");
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context,
                    context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    private void setNoDataForBangla() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_bangla), context);
    }

    private void setNoDataForEnglish() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_english), context);
    }

    public void hideNoData() {
        binding.textViewNoData.setVisibility(View.GONE);
    }

    public void setNoData() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
    }

    private void updateAdapter() {
        if (lawAdapter != null) {
            lawAdapter = null;
        }
        setAdapter(lawDataArrayList);
    }
}
