package www.fiberathome.com.parkingapp.ui.splash;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.databinding.ActivityBaseBinding;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private Context context;
    ActivityBaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;

        //Initialize splash fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SplashFragment.newInstance(), R.id.frameLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            setAppLocale(LANGUAGE_EN);
        } else {
            setAppLocale(Preferences.getInstance(context).getAppLanguage());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // refresh your views here
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            setAppLocale(LANGUAGE_EN);
        } else {
            setAppLocale(Preferences.getInstance(context).getAppLanguage());
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().alertDialog(context,
                (Activity) context,
                context.getResources().getString(R.string.are_you_sure_you_want_to_exit),
                context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        SplashActivity.super.onBackPressed();
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                    }

                    @Override
                    public void onNegativeClick() {
                        //null for this
                    }
                }).show();
    }
}