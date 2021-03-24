package www.fiberathome.com.parkingapp.utils;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

public class AnimationUtils {

    private static AnimationUtils animationUtils;

    public static AnimationUtils getInstance() {
        if (animationUtils == null) {
            animationUtils = new AnimationUtils();
        }

        return animationUtils;
    }

    public ValueAnimator polylineAnimator()  {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(4000);
        return valueAnimator;
    }

    public ValueAnimator carAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        return valueAnimator;
    }

}
