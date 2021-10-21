package www.fiberathome.com.parkingapp.ui.law;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.law.LawItem;
import www.fiberathome.com.parkingapp.model.response.law.LocalJson;
import www.fiberathome.com.parkingapp.model.response.law.Result;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.KeyboardUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;

@SuppressLint("NonConstantResourceId")
public class LawFragment extends BaseFragment implements IOnBackPressListener {

    @BindView(R.id.editTextSearchLaw)
    EditText editTextSearchLaw;

    @BindView(R.id.ivClearSearchText)
    ImageView ivClearSearchText;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.textViewNoData)
    public TextView textViewNoData;

    private Unbinder unbinder;

    private LawActivity context;

    private LawAdapter lawAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_law, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (LawActivity) getActivity();

        setListeners();

        String jsonResult = fetchJSONFromAsset();

        loadLocalJsonRecyclerView(jsonResult);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("onDestroyView called ");
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private void loadLocalJsonRecyclerView(String jsonResult) {
        LocalJson localJson = new Gson().fromJson(jsonResult, LocalJson.class);
        if (localJson != null) {
            List<Result> resultList = localJson.getResult();
            List<LawItem> lawItems = new ArrayList<>();

            for (Result r : resultList) {
                Timber.d("onViewCreated Result: -> %s", r.getTitle());
                LawItem item = new LawItem(r.getTitle(), r.getLaws());
                lawItems.add(item);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setMotionEventSplittingEnabled(false);
            lawAdapter = new LawAdapter(lawItems, this);
            recyclerView.setAdapter(lawAdapter);
        }
    }

    private String fetchJSONFromAsset() {
        String json = null;
        try {
            if (getActivity() != null) {
                InputStream is;
                if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
                    is = getActivity().getAssets().open("traficlaw.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, StandardCharsets.UTF_8);
                } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
                    is = getActivity().getAssets().open("traficlaw_bangla.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, StandardCharsets.UTF_8);
                } else {
                    is = getActivity().getAssets().open("traficlaw.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        editTextSearchLaw.addTextChangedListener(new TextWatcher() {
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
                if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) &&
                        TextUtils.getInstance().textContainsBangla(s.toString())) {
                    setNoDataForBangla();
                    recyclerView.setVisibility(View.GONE);
                    editTextSearchLaw.setText("");
                } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) &&
                        TextUtils.getInstance().textContainsEnglish(s.toString())) {
                    setNoDataForEnglish();
                    recyclerView.setVisibility(View.GONE);
                    editTextSearchLaw.setText("");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    lawAdapter.getFilter().filter(s.toString());
                }

                if (s.length() == 0) {
                    fetchJSONFromAsset();
                }
            }
        });

        ivClearSearchText.setOnClickListener(view -> {
            editTextSearchLaw.setText("");
            hideNoData();
            fetchJSONFromAsset();
        });

        editTextSearchLaw.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String contents = editTextSearchLaw.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)
                            && TextUtils.getInstance().textContainsBangla(contents)) {
                        setNoDataForBangla();
                        recyclerView.setVisibility(View.GONE);
                        editTextSearchLaw.setText("");
                    } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)
                            && TextUtils.getInstance().textContainsEnglish(contents)) {
                        setNoDataForEnglish();
                        recyclerView.setVisibility(View.GONE);
                        editTextSearchLaw.setText("");
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        lawAdapter.getFilter().filter(contents);
                    }
                } else {
                    //if something to do for empty edit text
                    KeyboardUtils.getInstance().hideKeyboard(context, editTextSearchLaw);
                    return true;
                }
            }
            return false;
        });
    }

    private void setNoDataForBangla() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.install_bangla_keyboard), context);
    }

    private void setNoDataForEnglish() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_english), context);
    }

    public void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    public void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("isGPSEnabled else called");
        }
        return false;
    }
}
