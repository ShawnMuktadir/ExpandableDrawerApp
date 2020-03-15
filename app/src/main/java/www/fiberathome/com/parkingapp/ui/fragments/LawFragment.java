package www.fiberathome.com.parkingapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LawFragment extends Fragment {

    @BindView(R.id.pdfView)
    PDFView pdfView;

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
        ButterKnife.bind(this, view);
        loadPDF();
        return view;
    }

    private void loadPDF() {
//        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromAsset("parkingrule.pdf").load();
    }
}
