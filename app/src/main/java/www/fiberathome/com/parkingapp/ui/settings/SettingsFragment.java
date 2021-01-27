package www.fiberathome.com.parkingapp.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.LocaleHelper;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TinyDB;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.preference.StaticData.APP_LANGUAGE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

@SuppressLint("NonConstantResourceId")
public class SettingsFragment extends BaseFragment implements View.OnClickListener, IOnBackPressListener {

    /*@BindView(R.id.textViewBan)
    TextView textViewBan;
    @BindView(R.id.textViewEng)
    TextView textViewEng;*/
    @BindView(R.id.linearLayoutLanguage)
    LinearLayout llDialogLanguage;

    @BindView(R.id.tv_dialog_language)
    TextView textViewLanguage;

    @BindView(R.id.ivDropDown)
    ImageView ivDropDown;

    private Unbinder unbinder;

    private Context context;

    private boolean lang_selected;
    private boolean selected = false;

    private Resources resources;

    private AlertDialog.Builder builder;

    private int language = 0;
    private boolean languageChanged = false;

    /* single item array instance to store
     which element is selected by user
     initially it should be set to zero meaning
     none of the element is selected by default */
    final int[] checkedItem = {-1};

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        builder = new AlertDialog.Builder(context);

        initView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.e("onStart called");
        if (SharedData.getInstance().getSelectedLanguage() != null || SharedPreManager.getInstance(context).getLanguage() != null
                || SharedPreManager.getInstance(context).getCheckedItem() != -1) {
            builder.setTitle(context.getResources().getString(R.string.select_language));
            textViewLanguage.setText(SharedData.getInstance().getSelectedLanguage());
            textViewLanguage.setText(SharedPreManager.getInstance(context).getLanguage());
            checkedItem[0] = SharedPreManager.getInstance(context).getCheckedItem();
            Timber.e("checkedItem[0] onStart -> %s", checkedItem[0]);
        } else {
            builder.setTitle(context.getResources().getString(R.string.select_language));
            textViewLanguage.setText(SharedPreManager.getInstance(context).getLanguage());
            Timber.e("select_language condition called");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.e("onResume called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Timber.e("onDestroyView called ");

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onStop() {
        SharedPreManager.getInstance(context).setCheckedItem(checkedItem[0]);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dialog_language:
            case R.id.linearLayoutLanguage:
            case R.id.ivDropDown:

                if (SharedData.getInstance().getSelectedLanguage() != null) {
                    builder.setTitle(context.getResources().getString(R.string.select_language));
                    textViewLanguage.setText(SharedData.getInstance().getSelectedLanguage());
                } else {
                    builder.setTitle(context.getResources().getString(R.string.select_language));
                }

                String[] languageDialogItems = {context.getResources().getString(R.string.english_item),
                        context.getResources().getString(R.string.bangla_item)};
                //context.getResources().getString(R.string.cancel)

                builder.setSingleChoiceItems(languageDialogItems, checkedItem[0], (dialog, which) -> {

                    // update the selected item which is selected by the user
                    // so that it should be selected when user opens the dialog next time
                    // and pass the instance to setSingleChoiceItems method
                    checkedItem[0] = which;
                    SharedPreManager.getInstance(context).setCheckedItem(checkedItem[0]);

                    switch (which) {
                        case 0:
                            SharedPreManager.getInstance(context).setCheckedItem(checkedItem[0]);
                            Timber.e("checkedItem[0] onClick -> %s", checkedItem[0]);
                            context = LocaleHelper.setLocale(context, "en");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.english_item));
                            //SharedData.getInstance().setSelectedLanguage(resources.getString(R.string.english_item));
                            SharedPreManager.getInstance(context).setLanguage(resources.getString(R.string.english_item));
                            setNewLocale("en", true);
                            break;
                        case 1:
                            SharedPreManager.getInstance(context).setCheckedItem(checkedItem[0]);
                            Timber.e("checkedItem[0] onClick -> %s", checkedItem[0]);
                            context = LocaleHelper.setLocale(context, "bn");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.bangla_item));
                            //SharedData.getInstance().setSelectedLanguage(resources.getString(R.string.bangla_item));
                            SharedPreManager.getInstance(context).setLanguage(resources.getString(R.string.bangla_item));
                            setNewLocale("bn", true);
                            break;

                        default:
                            context = LocaleHelper.setLocale(context, "en");
                            SharedPreManager.getInstance(context).setCheckedItem(checkedItem[0]);
                            Timber.e("checkedItem[0] onClick -> %s", checkedItem[0]);
                            context = LocaleHelper.setLocale(context, "en");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.english_item));
                            //SharedData.getInstance().setSelectedLanguage(resources.getString(R.string.english_item));
                            SharedPreManager.getInstance(context).setLanguage(resources.getString(R.string.english_item));
                        /*case 2:
                            dialog.dismiss();
                            break;*/
                    }

                    // when selected an item the dialog should be closed with the dismiss method
                    dialog.dismiss();
                });

                // set the negative button if the user
                // is not interested to select or change
                // already selected item
                builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                /*builder.setItems(languageDialogItems, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            context = LocaleHelper.setLocale(context, "en");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.english_item));
                            SharedData.getInstance().setSelectedLanguage(resources.getString(R.string.english_item));
                            SharedPreManager.getInstance(context).setLanguage(resources.getString(R.string.english_item));
                            setNewLocale("en", true);
                            break;
                        case 1:
                            context = LocaleHelper.setLocale(context, "bn");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.bangla_item));
                            SharedData.getInstance().setSelectedLanguage(resources.getString(R.string.bangla_item));
                            SharedPreManager.getInstance(context).setLanguage(resources.getString(R.string.bangla_item));
                            setNewLocale("bn", true);
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                });*/

                //builder.show();

                // create and build the AlertDialog instance
                // with the AlertDialog builder instance
                AlertDialog customAlertDialog = builder.create();

                // show the alert dialog when the button is clicked
                customAlertDialog.show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            HomeFragment homeFragment = new HomeFragment();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, homeFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();

        }
        return false;
    }

    private void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        llDialogLanguage.setOnClickListener(this);
        textViewLanguage.setOnClickListener(this);
        ivDropDown.setOnClickListener(this);
    }

    private void setNewLocale(String language, boolean restartProcess) {
        ParkingApp.localeManager.setNewLocale(context, language);

        if (restartProcess) {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            //System.exit(0);
        } else {
            Toast.makeText(context, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocale(String language, String country) {
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        Timber.e("DefaultLocale -> %s Language -> %s Country -> %s", Locale.getDefault(), language, country);
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putString(APP_LANGUAGE, language);
    }
}
