package www.fiberathome.com.parkingapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

@SuppressWarnings("unused")
public class AnimationUtils {

    private static AnimationUtils animationUtils;

    public static AnimationUtils getInstance() {
        if (animationUtils == null) {
            animationUtils = new AnimationUtils();
        }

        return animationUtils;
    }

    public void fadeOutAnimation(View viewToFadeOut) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 1f, 0f);

        fadeOut.setDuration(500);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
                super.onAnimationEnd(animation);
                viewToFadeOut.setVisibility(View.GONE);
            }
        });

        fadeOut.start();
    }

    public void fadeInAnimation(View viewToFadeIn) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToFadeIn, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewToFadeIn.setVisibility(View.VISIBLE);
                viewToFadeIn.setAlpha(0);
            }
        });

        fadeIn.start();
    }

    public ValueAnimator polylineAnimator() {
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
