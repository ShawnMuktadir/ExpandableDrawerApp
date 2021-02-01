package www.fiberathome.com.parkingapp.ui.splash;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtil;
import www.fiberathome.com.parkingapp.utils.ForceUpdateChecker;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
public class SplashFragment extends BaseFragment implements ForceUpdateChecker.OnUpdateNeededListener {

    @BindView(R.id.splash_iv_logo)
    ImageView imageViewSplashLogo;

    private Unbinder unbinder;

    private static final int UPDATE_CODE = 1000;
    private SplashActivity context;

    public SplashFragment() {
        // Required empty public constructor
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (SplashActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ForceUpdateChecker.with(context).onUpdateNeeded(SplashFragment.this).check();
        } catch (Exception e) {
            e.getCause();
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void openActivity(Intent intent) {
        new Handler().postDelayed(() -> {
            if (ApplicationUtils.checkInternet(context)) {
                context.startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.finish();
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        checkUserLogin();
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_splash));
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            context.finish();
                        }, 700);
                    }
                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                    context.finish();
                });
            }
        }, 1000);
    }

    private void checkUserLogin() {
        // Check user is logged in
        if (SharedPreManager.getInstance(context).isLoggedIn() && SharedPreManager.getInstance(context) != null && SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("activity start if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            //openActivity(new Intent(context, MainActivity.class));
            openActivity(new Intent(context, HomeActivity.class));
        } else if (SharedPreManager.getInstance(context).isLoggedIn() && !SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("activity start else if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            openActivity(new Intent(context, PermissionActivity.class));
            //openActivity(new Intent(context, LocationPermissionActivity.class));
        } else {
            Timber.e("activity start else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            openActivity(new Intent(context, LoginActivity.class));
        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        DialogUtil.getInstance().alertDialog(
                requireActivity(),
                context.getResources().getString(R.string.new_version_available), context.getResources().getString(R.string.please_update_the_app),
                context.getResources().getString(R.string.update), context.getResources().getString(R.string.no_thanks),
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        redirectStore(updateUrl);
                    }

                    @Override
                    public void onNegativeClick() {
                        context.finishAffinity();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).show();
    }

    @Override
    public void noUpdateNeeded() {
        checkUserLogin();
    }

    private void redirectStore(String updateUrl) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.getCause();
            Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}