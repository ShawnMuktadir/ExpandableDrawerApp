package www.fiberathome.com.parkingapp.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.law.LawItem;
import www.fiberathome.com.parkingapp.model.law.LocalJson;
import www.fiberathome.com.parkingapp.model.law.Result;
import www.fiberathome.com.parkingapp.view.lawAdapter.LawAdapter;
import www.fiberathome.com.parkingapp.utils.OnEditTextRightDrawableTouchListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class LawFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.editTextSearchLaw)
    EditText editTextSearchLaw;
    @BindView(R.id.textViewNoData)
    TextView textViewNoData;

    private RecyclerView recyclerView;
    private LawAdapter lawAdapter;
    private Context context;
    private ArrayList<Result> resultArrayList = new ArrayList<>();

    public LawFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_law, container, false);
        ButterKnife.bind(this, view);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        //    loadPDF();
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
            lawAdapter = new LawAdapter(lawItems);
            recyclerView.setAdapter(lawAdapter);
        }
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

    private void loadPDF() {
//        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromAsset("parkingrule.pdf").load();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        editTextSearchLaw.addTextChangedListener(new TextWatcher() {
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
                /*if (s.length() > 0) {
                    filter(s.toString());
                }*/

                //drawing cross button if text appears programmatically
                if (s.length() > 0) {
                    editTextSearchLaw.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    editTextSearchLaw.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                lawAdapter.getFilter().filter(s.toString());
            }
        });

        //handle drawable cross button click listener programmatically
        editTextSearchLaw.setOnTouchListener(
                new OnEditTextRightDrawableTouchListener(editTextSearchLaw) {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void OnDrawableClick() {
                        // The right drawable was clicked. Your action goes here.
                        editTextSearchLaw.setText("");
                        fetchJSONFromAsset();
                    }
                });
    }

    private void filter(String text) {
        ArrayList<Result> filteredList = new ArrayList<>();

        for (Result item : resultArrayList) {

            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) || item.getLaws().contains(text.toLowerCase())) {
                hideNoData();
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No data", Toast.LENGTH_LONG).show();
//            TastyToastUtils.showTastyErrorToast(context, "No data");
//            setNoData();
        }

        filterList(filteredList);
    }

    private void filterList(ArrayList<Result> filteredList) {
        this.resultArrayList = filteredList;
        lawAdapter.notifyDataSetChanged();
    }

    private void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }
}
