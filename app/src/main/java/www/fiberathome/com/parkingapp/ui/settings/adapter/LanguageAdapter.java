package www.fiberathome.com.parkingapp.ui.settings.adapter;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.Language;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.databinding.RowLanguageSettingBinding;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.SettingsViewHolder> {

    private final List<Language> dataSet;

    private final OnItemClickListener listener;

    private Context context;

    public LanguageAdapter(List<Language> dataSet, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RowLanguageSettingBinding itemBinding = RowLanguageSettingBinding.inflate(layoutInflater, parent, false);
        return new SettingsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SettingsViewHolder viewHolder, final int position) {
        Language data = dataSet.get(position);

        viewHolder.binding.textViewLanguageName.setText(data.getName());
        viewHolder.binding.textViewSubLanguageName.setText(data.getSubName());

        if (!data.isSelected()) {
            viewHolder.binding.imageViewCheckedIcon.setVisibility(View.GONE);
        } else {
            viewHolder.binding.imageViewCheckedIcon.setVisibility(View.VISIBLE);
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(data));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @SuppressLint("NonConstantResourceId")
    public static class SettingsViewHolder extends RecyclerView.ViewHolder {
        RowLanguageSettingBinding binding;

        public SettingsViewHolder(RowLanguageSettingBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Language language);
    }
}
