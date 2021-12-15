package www.fiberathome.com.parkingapp.ui.law;

import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentLawBinding;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.law.LawItem;
import www.fiberathome.com.parkingapp.model.response.law.LocalJson;
import www.fiberathome.com.parkingapp.model.response.law.Result;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.KeyboardUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class LawFragment extends BaseFragment implements IOnBackPressListener {

    FragmentLawBinding binding;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLawBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (LawActivity) getActivity();

        setListeners();

        String jsonResult = fetchJSONFromAsset();

        loadLocalJsonRecyclerView(jsonResult);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            binding.recyclerView.setLayoutManager(mLayoutManager);
            binding.recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerView.setMotionEventSplittingEnabled(false);
            lawAdapter = new LawAdapter(lawItems, this);
            binding.recyclerView.setAdapter(lawAdapter);
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

        binding.editTextSearchLaw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
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
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.editTextSearchLaw.setText("");
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    lawAdapter.getFilter().filter(s.toString());
                }

                if (s.length() == 0) {
                    fetchJSONFromAsset();
                }
            }
        });

        binding.ivClearSearchText.setOnClickListener(view -> {
            binding.editTextSearchLaw.setText("");
            hideNoData();
            fetchJSONFromAsset();
        });

        binding.editTextSearchLaw.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String contents = binding.editTextSearchLaw.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)
                            && TextUtils.getInstance().textContainsBangla(contents)) {
                        setNoDataForBangla();
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.editTextSearchLaw.setText("");
                    } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)
                            && TextUtils.getInstance().textContainsEnglish(contents)) {
                        setNoDataForEnglish();
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.editTextSearchLaw.setText("");
                    } else {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        lawAdapter.getFilter().filter(contents);
                    }
                } else {
                    //if something to do for empty edit text
                    KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextSearchLaw);
                    return true;
                }
            }
            return false;
        });
    }

    private void setNoDataForBangla() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_data_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.install_bangla_keyboard), context);
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
}
