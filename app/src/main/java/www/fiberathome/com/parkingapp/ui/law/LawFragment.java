package www.fiberathome.com.parkingapp.ui.law;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.law.LawItem;
import www.fiberathome.com.parkingapp.model.law.LocalJson;
import www.fiberathome.com.parkingapp.model.law.Result;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;

public class LawFragment extends Fragment implements IOnBackPressListener {


    private final String TAG = getClass().getSimpleName();
//    @BindView(R.id.pdfView)
//    PDFView pdfView;
    @BindView(R.id.editTextSearchLaw)
    EditText editTextSearchLaw;
    @BindView(R.id.ivClearSearchText)
    ImageView ivClearSearchText;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textViewNoData)
    public TextView textViewNoData;

    private Context context;
    private Unbinder unbinder;
    private LawAdapter lawAdapter;
    private ArrayList<Result> resultArrayList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_law, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        loadPDF();
        setListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String jsonResult = fetchJSONFromAsset();

        LocalJson localJson = new Gson().fromJson(jsonResult, LocalJson.class);
        if (localJson != null) {
//            Toast.makeText(context, "Data", Toast.LENGTH_SHORT).show();
            List<Result> resultList = localJson.getResult();
            List<LawItem> lawItems = new ArrayList<>();

            for (Result r : resultList) {
                Log.d(TAG, "onViewCreated: " + r.getTitle());
                LawItem item = new LawItem(r.getTitle(), r.getLaws());
                lawItems.add(item);
            }
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setMotionEventSplittingEnabled(false);
            lawAdapter = new LawAdapter(lawItems, this);
            recyclerView.setAdapter(lawAdapter);
        }
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

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

//            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                    .setTitle("GPS Permissions")
//                    .setMessage("GPS is required for this app to work. Please enable GPS.")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();

        }
        return false;
    }

    private String fetchJSONFromAsset() {
        String json = null;
        try {
            if (getActivity() != null) {
                InputStream is = getActivity().getAssets().open("traficlaw.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
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
                /*if (s.length() > 0) {
                    filter(s.toString());
                }*/
                lawAdapter.getFilter().filter(s.toString());

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
                    lawAdapter.getFilter().filter(contents);
                    lawAdapter.notifyDataSetChanged();
                    ApplicationUtils.hideKeyboard(context, editTextSearchLaw);
                } else {
                    //if something to do for empty edittext
                    ApplicationUtils.hideKeyboard(context, editTextSearchLaw);
                    return true;
                }
            }
            return false;
        });

        //handle special characters
//        InputFilter filter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                boolean keepOriginal = true;
//                StringBuilder sb = new StringBuilder(end - start);
//                for (int i = start; i < end; i++) {
//                    char c = source.charAt(i);
//                    if (isCharAllowed(c)) // put your condition here
//                        sb.append(c);
//                    else
//                        keepOriginal = false;
//                }
//                if (keepOriginal)
//                    return null;
//                else {
//                    if (source instanceof Spanned) {
//                        SpannableString sp = new SpannableString(sb);
//                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
//                        return sp;
//                    } else {
//                        return sb;
//                    }
//                }
//            }
//
//            private boolean isCharAllowed(char c) {
//                return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
//            }
//        };
//        editTextSearchLaw.setFilters(new InputFilter[]{filter});
    }

    public void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    public void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_record_found));
    }

    private void loadPDF() {
//        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
//        pdfView.fromAsset("parkingrule.pdf").load();
    }
}