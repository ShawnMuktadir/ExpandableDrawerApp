package www.fiberathome.com.parkingapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import www.fiberathome.com.parkingapp.R;

@SuppressWarnings("unused")
public class ViewUtils {
    private static ViewUtils viewUtils;

    public static ViewUtils getInstance() {
        if (viewUtils == null) {
            viewUtils = new ViewUtils();
        }

        return viewUtils;
    }

    public int getToolBarHeight(Context context) {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBackground(Context context, View source, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            source.setBackground(context.getResources().getDrawable(resId));
        } else {
            source.setBackground(context.getResources().getDrawable(resId));
        }
    }

    //set margin programmatically
    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void hideStatusBar(Activity activityContext) {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        activityContext.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
