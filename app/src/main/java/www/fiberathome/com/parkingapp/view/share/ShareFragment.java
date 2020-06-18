package www.fiberathome.com.parkingapp.view.share;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.fiberathome.com.parkingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        shareApp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        shareApp();
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"LOCC Smart Parking App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "<b>LOCC Smart Parking App Link</b>...\n\n"+"https://play.google.com/store/apps/details?id=com.ipvworld.c4console&hl=en");
        startActivity(Intent.createChooser(shareIntent,"Share Via:"));
    }
}
