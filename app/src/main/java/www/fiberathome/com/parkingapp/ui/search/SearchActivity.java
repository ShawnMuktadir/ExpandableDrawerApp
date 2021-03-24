package www.fiberathome.com.parkingapp.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class SearchActivity extends NavigationActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setTitle(context.getResources().getString(R.string.parking_spot));

        //centerTitleAndSubtitle(toolbar);

        // Initialize Search fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SearchFragment.newInstance(), R.id.nav_host_fragment);

        changeDefaultActionBarDrawerToogleIconWithBackButton();
    }

    @SuppressLint("ResourceType")
    public void centerTitleAndSubtitle(Toolbar toolbar) {
        // Save current title and subtitle
        final CharSequence originalTitle = toolbar.getTitle();
        final CharSequence originalSubtitle = toolbar.getSubtitle();

        // Temporarily modify title and subtitle to help detecting each
        toolbar.setTitle(context.getResources().getString(R.string.parking_spot));
        toolbar.setSubtitle("subtitle");

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;

                if (textView.getText().equals(context.getResources().getString(R.string.parking_spot))) {
                    // Customize title's TextView
                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER;
                    params.setMargins(0, 25, 0, 0);
                    textView.setLayoutParams(params);
                    textView.setTextSize(18f);
                    textView.setShadowLayer(1f, 1f, 1f, Color.BLACK);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                /*else if(textView.getText().equals("subtitle")){
                    // Customize subtitle's TextView
                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    textView.setLayoutParams(params);
                }*/
            }

            // Restore title and subtitle
            toolbar.setTitle(originalTitle);
            toolbar.setSubtitle(originalSubtitle);
        }
    }
}
