package www.fiberathome.com.parkingapp.ui.ratingReview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.fiberathome.com.parkingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingReviewFragment extends Fragment {

    public RatingReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating_review, container, false);
    }
}
