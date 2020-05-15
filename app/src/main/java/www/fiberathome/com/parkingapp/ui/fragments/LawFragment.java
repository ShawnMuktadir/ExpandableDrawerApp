package www.fiberathome.com.parkingapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import www.fiberathome.com.parkingapp.ui.lawAdapter.LawAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LawFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.pdfView)
    PDFView pdfView;

    private RecyclerView recyclerView;
    private Context context;

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
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ButterKnife.bind(this, view);
        //    loadPDF();
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
            LawAdapter adapter = new LawAdapter(lawItems);
            recyclerView.setAdapter(adapter);
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
}
