package www.fiberathome.com.parkingapp.view.settings;

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
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.LocaleHelper;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TinyDB;
import www.fiberathome.com.parkingapp.view.main.MainActivity;
import www.fiberathome.com.parkingapp.view.main.home.HomeFragment;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.preference.StaticData.APP_LANGUAGE;
import static www.fiberathome.com.parkingapp.view.main.MainActivity.GPS_REQUEST_CODE;

public class SettingsFragment extends Fragment implements View.OnClickListener, IOnBackPressListener {

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
    private boolean lang_selected;
    private boolean selected = false;
    private Context context;
    private Resources resources;

    private int language = 0;
    private boolean languageChanged = false;

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

        initView(view);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (languageChanged) {
            Timber.e("LanguageChanged -> %s, Language -> %s", languageChanged, language);
            switchLanguageAndRestartApp();
        }*/
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dialog_language:
            case R.id.linearLayoutLanguage:
            case R.id.ivDropDown:
                //llDialogLanguage.setOnClickListener(v -> {
                final String[] Language = {"ENGLISH", "BANGLA"};
                final int checkedItem = 0;
                /*if (selected && lang_selected) {
                    checkedItem = 0;
                } else {
                    checkedItem = 0;
                }*/

                AlertDialog.Builder languageDialog = new AlertDialog.Builder(context);
                languageDialog.setTitle("Select Language");

                String[] languageDialogItems = {"English","Bangla"};

                languageDialog.setItems(languageDialogItems, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            context = LocaleHelper.setLocale(context, "en");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.lang_select_en));
                            setLocale("en", "US");
                            setNewLocale("en", true);
                            break;
                        case 1:
                            context = LocaleHelper.setLocale(context, "bn");
                            resources = context.getResources();
                            textViewLanguage.setText(resources.getString(R.string.lang_select_bn));
                            setLocale("bn", "BD");
                            setNewLocale("bn", true);
                            break;
                    }
                });
                languageDialog.show();

                /*final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Select a Language...")
                        .set(Language, checkedItem, (dialog, i) -> {
                            //Toast.makeText(context, "" + which, Toast.LENGTH_SHORT).show();
                            textViewLanguage.setText(Language[i]);
                            lang_selected = Language[i].equals("ENGLISH");
                            //if user select prefered language as English then
                            if (Language[i].equals("ENGLISH")) {
                                context = LocaleHelper.setLocale(context, "en");
                                resources = context.getResources();
                                textViewLanguage.setText(resources.getString(R.string.lang_select_en));
                                setLocale("en", "US");
                                setNewLocale("en", true);
                            }
                            //if user select prefered language as bangla then
                            if (Language[i].equals("BANGLA")) {
                                context = LocaleHelper.setLocale(context, "bn");
                                resources = context.getResources();
                                textViewLanguage.setText(resources.getString(R.string.lang_select_bn));
                                setLocale("bn", "BD");
                                setNewLocale("bn", true);
                            }
                        });
                        //.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();*/
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
//            HomeFragment nextFrag = new HomeFragment();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, "Please enable GPS");
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

        /*textViewBan.setOnClickListener(v -> {
            Timber.e("textViewBan clicked");
            ApplicationUtils.showAlertDialog(context.getString(R.string.change_language), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {
                //setNewLocale(LANGUAGE_BANGLA, false);
                //context = LocaleHelper.setLocale(context, "en");
                resources = context.getResources();
                language = 0;
                languageChanged = true;
                switchLanguageAndRestartApp();
            }, (dialog, which) -> {
                dialog.dismiss();
            });
        });

        textViewEng.setOnClickListener(v -> {
            Timber.e("textViewEng clicked");
            ApplicationUtils.showAlertDialog(context.getString(R.string.change_language), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {
                //setNewLocale(LANGUAGE_ENGLISH, false);
                //context = LocaleHelper.setLocale(context, "en");
                resources = context.getResources();
                language = 1;
                languageChanged = true;
                switchLanguageAndRestartApp();
            }, (dialog, which) -> {
                dialog.dismiss();
            });
        });*/
    }

    private void switchLanguageAndRestartApp() {
        /*if (language == 0) {
            switchLangSelection(textViewEng, textViewBan);
            context = LocaleHelper.setLocale(context, "bn");
            resources = context.getResources();
            setLocale("bn", "BD");
            openMainActivity();
        } else {
            switchLangSelection(textViewBan, textViewEng);
            context = LocaleHelper.setLocale(context, "en");
            resources = context.getResources();
            setLocale("en", "US");
            openMainActivity();
        }*/
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        ParkingApp.localeManager.setNewLocale(context, language);

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        requireActivity().overridePendingTransition(0, 0);
        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(context, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void openMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    private void switchLangSelection(TextView textView1, TextView textView2) {
        ApplicationUtils.setTextColor(textView1, context, R.color.white);
        textView1.setOnClickListener(null);
        ApplicationUtils.setBackground(context, textView1, R.color.colorPrimary);
        ApplicationUtils.setTextColor(textView2, context, R.color.white);
        ApplicationUtils.setBackground(context, textView2, R.color.dark_gray);
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
