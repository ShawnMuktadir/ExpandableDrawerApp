package www.fiberathome.com.parkingapp.ui.navigation.settings;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.Language;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.databinding.FragmentSettingsBinding;
import www.fiberathome.com.parkingapp.ui.navigation.settings.adapter.LanguageAdapter;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class LanguageSettingFragment extends Fragment {

    FragmentSettingsBinding binding;
    private LanguageSettingActivity context;

    public LanguageSettingFragment() {
        // Required empty public constructor
    }

    public static LanguageSettingFragment newInstance() {
        return new LanguageSettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (LanguageSettingActivity) getActivity();

        initView(view);
    }

    private void initView(View view) {
        String[] names = new String[]{context.getResources().getString(R.string.english), context.getResources().getString(R.string.bangla)};
        String[] subNames = new String[]{context.getResources().getString(R.string.english), context.getResources().getString(R.string.bangla_bn)};
        String[] isoCodes = new String[]{LANGUAGE_EN, LANGUAGE_BN};

        /*int s1 = context.getResources().getString(R.string.language_settings).codePointAt(0);
        if (s1 >= 0x0980 && s1 <= 0x09E0) {
            Preferences.getInstance(context).setAppLanguage(LANGUAGE_BN);
            LanguagePreferences.getInstance(context).setAppLanguage(LANGUAGE_BN);
        } else {
            Preferences.getInstance(context).setAppLanguage(LANGUAGE_EN);
            LanguagePreferences.getInstance(context).setAppLanguage(LANGUAGE_EN);
        }*/

        LanguageAdapter languageAdapter = new LanguageAdapter(
                populateLanguageItem(names, subNames, isoCodes, new ArrayList<>()), (Language language) -> {

            if (Preferences.getInstance(context).getAppLanguage() != null &&
                    LanguagePreferences.getInstance(context).getAppLanguage() != null) {
                Preferences.getInstance(context).setAppLanguage(language.getIsoCode());
                LanguagePreferences.getInstance(context).setAppLanguage(language.getIsoCode());
                context.setAppLocale(language.getIsoCode());
            }
            //context.startActivityWithFinishAffinity(LanguageSettingActivity.class);
            context.startActivity(LanguageSettingActivity.class);
        });

        binding.recyclerViewSettings.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewSettings.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewSettings.setAdapter(languageAdapter);
    }

    private List<Language> populateLanguageItem(String[] names, String[] subNames, String[] isoCodes, List<Language> languages) {
        for (int i = 0; i < names.length; i++) {
            languages.add(new Language(
                    names[i], subNames[i], isoCodes[i], isoCodes[i].equalsIgnoreCase(Preferences.getInstance(context).getAppLanguage()),
                    isoCodes[i].equalsIgnoreCase(LanguagePreferences.getInstance(context).getAppLanguage())
            ));
        }

        return languages;
    }
}
